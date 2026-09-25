package com.gianteyes.gaarigar.Order.FuelDeliveryOrder;

import com.gianteyes.gaarigar.Order.InitiatedRequest.InitiateRequestModel;
import com.gianteyes.gaarigar.Order.OrderMapper;
import com.gianteyes.gaarigar.Order.OrderStatus;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import com.gianteyes.gaarigar.common.Location;
import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.notification.Note;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpService;
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
public class FuelOrderService {

    @Autowired
    private PetrolPumpService petrolPumpService;

    @Autowired
    private FuelOrderRepository fuelOrderRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private OrderMapper orderMapper;

    public List<PetrolPumpModel> findNearestAvailablePetrolPumps(Location location) {
        return this.petrolPumpService.getAll();
    }

    private Note createAcceptedNoteForCustomer(FuelDeliveryOrderModel order) {
        return Note.builder()
                .subject("Fuel Delivery Request Accepted")
                .content("Your request for Fuel has been accepted by" + order.getPetrolPump().getFirstName())
                .data(new HashMap<String, String>() {{
                    put("type", "fueldelivery-order");
                    put("id", order.getId().toString());
                }})
                .token(order.getCustomer().getFcmToken()).build();
    }


    public FuelDeliveryOrderModel convertRequestToOrder(InitiateRequestModel initiatedRequest, PetrolPumpModel petrolPump, float literPrice, float distance) {
        FuelDeliveryOrderModel order = FuelDeliveryOrderModel.builder()
                .customer(initiatedRequest.getCustomer())
                .petrolPump(petrolPump)
                .customerLocation(initiatedRequest.getLocation())
                .acceptedAt(now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .status(OrderStatus.ACCEPTED)
                .notes(initiatedRequest.getNotes())
                .noOfLitres(initiatedRequest.getNoOfLitres())
                .price((double) (literPrice * initiatedRequest.getNoOfLitres()))
                .requestedAt(initiatedRequest.getRequestedAt())
                .distance(distance)
                .paymentMethod(initiatedRequest.getPaymentMethod())
                .litrePrice(literPrice)
                .orderType(initiatedRequest.getOrderType())
                .build();
        return fuelOrderRepository.save(order);
    }

    public List<OrderResponseDto> getOrdersByQuery(Long userId, Map<String, String> queryParams) {
        // specification for query
        Specification spec = Specification.where(null);
        FuelDeliveryOrderSpecification fueldOrderSpecification = new FuelDeliveryOrderSpecification();
        if (queryParams.containsKey("statuses")) {
            String[] statuses = queryParams.get("statuses").split(",");
            List<OrderStatus> orderStatuses = Arrays.stream(statuses).map(OrderStatus::valueOf).collect(Collectors.toList());
            fueldOrderSpecification.add(new SearchCriteria("status", orderStatuses, SearchOperation.IN));
        }
        if (queryParams.containsKey("startDate")) {
            LocalDateTime startDate = LocalDateTime.parse(queryParams.get("startDate"));
            fueldOrderSpecification.add(new SearchCriteria("requestedAt", startDate, SearchOperation.GREATER_THAN_EQUAL));
        }
        if (queryParams.containsKey("endDate")) {
            LocalDateTime endDate = LocalDateTime.parse(queryParams.get("endDate"));
            fueldOrderSpecification.add(new SearchCriteria("requestedAt", endDate, SearchOperation.LESS_THAN_EQUAL));
        }
        // either customer or mechanic should have userId

        spec = spec.and(fueldOrderSpecification);
        spec.and(FuelDeliveryOrderSpecification.hasPetrolPumpIdOrCustomerId(userId));
        List<FuelDeliveryOrderModel> orders = fuelOrderRepository.findAll(spec);
        orders = orders.stream().filter(v -> Objects.equals(v.getCustomer().getId(), userId) || Objects.equals(v.getPetrolPump().getId(), userId)).collect(Collectors.toList());
        return orderMapper.mapToFuelDeliveryOrderResponseDto(orders);
    }

    public Optional<FuelDeliveryOrderModel> getFuelOrder(Long id) {
        return fuelOrderRepository.findById(id);
    }

}
