package com.gianteyes.gaarigar.Order.MechanicOrder;

import com.gianteyes.gaarigar.Order.InitiatedRequest.InitiateRequestModel;
import com.gianteyes.gaarigar.Order.OrderMapper;
import com.gianteyes.gaarigar.Order.OrderStatus;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicService;
import com.gianteyes.gaarigar.notification.Note;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.utils.SearchCriteria;
import com.gianteyes.gaarigar.utils.SearchOperation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class MechanicOrderService {
    @Autowired
    private MechanicOrderRepository mechanicOrderRepository;
    @Autowired
    private MechanicService mechanicService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderMapper orderMapper;

    public List<MechanicModel> findNearestAvailableMechanics(Location location) {
        return this.mechanicService.getAll();
    }

    public Note createAcceptedNoteForCustomer(MechanicOrderModel order) {
        return Note.builder()
                .subject("Mechanic Request Accepted")
                .content("Your request for Mechanic has been accepted by" + order.getMechanic().getFirstName())
                .data(new HashMap<String, String>() {{
                    put("type", "mechanic-order");
                    put("id", order.getId().toString());
                    put("email", order.getCustomer().getEmail());
                }})
                .user(order.getCustomer())
                .token(order.getCustomer().getFcmToken()).build();
    }

    public MechanicOrderModel convertRequestToOrder(InitiateRequestModel initiatedRequest, MechanicModel mechanic) {
        MechanicOrderModel order = MechanicOrderModel.builder()
                .customer(initiatedRequest.getCustomer())
                .mechanic(mechanic)
                .customerLocation(initiatedRequest.getLocation())
                .acceptedAt(now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .status(OrderStatus.ACCEPTED)
                .paymentMethod(initiatedRequest.getPaymentMethod())
                .notes(initiatedRequest.getNotes())
                .requestedAt(initiatedRequest.getRequestedAt())
                .orderType(initiatedRequest.getOrderType())
                .build();
        return mechanicOrderRepository.save(order);
    }

    public List<OrderResponseDto> getOrdersByQuery(Long userId, Map<String, String> queryParams) {
        // specification for query
        Specification spec = Specification.where(null);

        MechanicOrderSpecification mechanicOrderSpecification = new MechanicOrderSpecification();
        if (queryParams.containsKey("statuses")) {
            String[] statuses = queryParams.get("statuses").split(",");
            List<OrderStatus> orderStatuses = Arrays.stream(statuses).map(OrderStatus::valueOf).collect(Collectors.toList());
            mechanicOrderSpecification.add(new SearchCriteria("status", orderStatuses, SearchOperation.IN));
        }
        if (queryParams.containsKey("startDate")) {
            LocalDateTime startDate = LocalDateTime.parse(queryParams.get("startDate"));
            mechanicOrderSpecification.add(new SearchCriteria("requestedAt", startDate, SearchOperation.GREATER_THAN_EQUAL));
        }
       if (queryParams.containsKey("endDate")) {
            LocalDateTime endDate = LocalDateTime.parse(queryParams.get("endDate"));
            mechanicOrderSpecification.add(new SearchCriteria("requestedAt", endDate, SearchOperation.LESS_THAN_EQUAL));
 }
        // either customer or mechanic should have userId

        spec = spec.and(mechanicOrderSpecification);
        spec.and(MechanicOrderSpecification.hasMechanicIdOrCustomerId(userId));
        List<MechanicOrderModel> orders = mechanicOrderRepository.findAll(spec);
        orders = orders.stream().filter(v -> Objects.equals(v.getCustomer().getId(), userId) || Objects.equals(v.getMechanic().getId(), userId)).collect(Collectors.toList());
        return orderMapper.mapToMechanicOrderResponseDto(orders);
    }

    public Optional<MechanicOrderModel> getMechanicOrder(Long id) {
        return mechanicOrderRepository.findById(id);
    }
}
