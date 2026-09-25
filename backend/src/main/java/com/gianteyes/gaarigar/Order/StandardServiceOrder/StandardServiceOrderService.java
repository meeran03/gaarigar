package com.gianteyes.gaarigar.Order.StandardServiceOrder;

import com.gianteyes.gaarigar.Order.OrderMapper;
import com.gianteyes.gaarigar.Order.OrderStatus;
import com.gianteyes.gaarigar.Order.OrderType;
import com.gianteyes.gaarigar.Order.StandardServiceOrder.dto.request.CreateStandardServiceOrderRequest;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import com.gianteyes.gaarigar.customer.CustomerModel;
import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.exceptions.InvalidRequestStatusException;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.notification.Note;
import com.gianteyes.gaarigar.notification.NotificationUtil;
import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceModel;
import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceService;
import com.gianteyes.gaarigar.user.UserService;
import com.gianteyes.gaarigar.utils.SearchCriteria;
import com.gianteyes.gaarigar.utils.SearchOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@Service
public class StandardServiceOrderService {
    @Autowired
    private StandardServiceOrderRepository standardServiceOrderRepository;
    @Autowired
    private MechanicStandardServiceService mechanicStandardServiceService;
    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private NotificationUtil notificationUtil;
    @Autowired
    private OrderMapper orderMapper;

    Note createNoteForMechanic(StandardServiceOrder serviceModel) {
        return Note.builder()
                .subject("New Service Order")
                .content("You have a new service order for " + serviceModel.getMechanicStandardService().getStandardService().getName())
                .data(new HashMap<String, String>() {{
                    put("type", "service");
                    put("id", serviceModel.getId().toString());
                }})
                .user(serviceModel.getMechanicStandardService().getMechanic())
                .token(serviceModel.getMechanicStandardService().getMechanic().getFcmToken()).build();
    }

    @Transactional
    public OrderResponseDto createStandardServiceOrder(CreateStandardServiceOrderRequest standardServiceOrder) {
        MechanicStandardServiceModel mechanicStandardService = this.mechanicStandardServiceService.get(standardServiceOrder.getMechanicStandardServiceId());
        if (mechanicStandardService == null) {
            throw new ResourceNotFoundException("Service", "id", standardServiceOrder.getMechanicStandardServiceId());
        }
        CustomerModel customer = new CustomerModel();
        customer = customerService.getCustomerById(userService.getCurrentAuthenticatedUser().getId());
        customer.setImage(null);
        StandardServiceOrder order = StandardServiceOrder.builder()
                .mechanicStandardService(mechanicStandardService)
                .customerLocation(standardServiceOrder.getLocation())
                .paymentMethod(standardServiceOrder.getPaymentMethod())
                .requestedAt(now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES))
                .price(mechanicStandardService.getPrice())
                .notes(standardServiceOrder.getNotes())
                .status(OrderStatus.REQUESTED)
                .customer(customer)
                .orderType(OrderType.STANDARD_SERVICE)
                .build();
        StandardServiceOrder serviceOrder = this.standardServiceOrderRepository.save(order);
        notificationUtil.sendNotification(createNoteForMechanic(serviceOrder));
        return orderMapper.mapStandardServiceOrderToOrderResponse(serviceOrder);
    }

    private Note createCancellationNoteForCustomer(StandardServiceOrder serviceModel) {

        return Note.builder()
                .subject("Service Order Cancelled")
                .content("Your service order for " + serviceModel.getMechanicStandardService().getStandardService().getName() + " has been cancelled")
                .data(new HashMap<String, String>() {{
                    put("type", "service");
                    put("id", serviceModel.getId().toString());
                    put("email", serviceModel.getCustomer().getEmail());
                }})
                .templateName("OrderCancelledNotification")
                .mailSubject("Service Order Cancelled")
                .user(userService.getCurrentAuthenticatedUser())
                .token(serviceModel.getCustomer().getFcmToken()).build();

    }

    private Note createAcceptedNoteForCustomer(StandardServiceOrder serviceModel) {
        return Note.builder()
                .subject("Service Order Accepted")
                .content("Your service order for " + serviceModel.getMechanicStandardService().getStandardService().getName() + " has been accepted")
                .data(new HashMap<String, String>() {{
                    put("type", "service");
                    put("id", serviceModel.getId().toString());
                    put("email", serviceModel.getCustomer().getEmail());
                    put("paymentMethod", serviceModel.getPaymentMethod().toString());
                }})
                .templateName("OrderPlacedNotification")
                .mailSubject("Service Requested Accepted")
                .user(userService.getCurrentAuthenticatedUser())
                .token(serviceModel.getCustomer().getFcmToken()).build();
    }

    @Transactional
    public OrderResponseDto accept(Long orderId) {

        StandardServiceOrder order = this.standardServiceOrderRepository.findById(orderId).orElseThrow();
        if (order.getStatus() == OrderStatus.ACCEPTED || order.getStatus() == OrderStatus.REJECTED)
            throw new InvalidRequestStatusException(orderId, "has been acccepted or cancelled");
        if (order.getRequestedAt().plusMinutes(10).isAfter(LocalDateTime.now())) {
            order.setStatus(OrderStatus.ACCEPTED);
            order.setAcceptedAt(now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES));
            notificationUtil.sendNotification(createAcceptedNoteForCustomer(order));
            this.standardServiceOrderRepository.save(order);
            return orderMapper.mapStandardServiceOrderToOrderResponse(order);
        }
        throw new InvalidRequestStatusException("TIMEOUT! Request", "id");
    }

    public List<OrderResponseDto> getOrdersByQuery(Long userId, Map<String, String> queryParams) {
        // specification for query
        Specification spec = Specification.where(null);
        StandardServiceOrderSpecification ssSpec = new StandardServiceOrderSpecification();
        if (queryParams.containsKey("statuses")) {
            String[] statuses = queryParams.get("statuses").split(",");
            // convert to OrderStatus enums
            List<OrderStatus> orderStatuses = Arrays.stream(statuses).map(OrderStatus::valueOf).collect(Collectors.toList());
            ssSpec.add(new SearchCriteria("status", orderStatuses, SearchOperation.IN));
        }
        if (queryParams.containsKey("startDate")) {
            LocalDateTime startDate = LocalDateTime.parse(queryParams.get("startDate"));
            ssSpec.add(new SearchCriteria("requestedAt", startDate, SearchOperation.GREATER_THAN_EQUAL));
        }
        if (queryParams.containsKey("endDate")) {
            LocalDateTime endDate = LocalDateTime.parse(queryParams.get("endDate"));
            ssSpec.add(new SearchCriteria("requestedAt", endDate, SearchOperation.LESS_THAN_EQUAL));
        }
        // either customer or mechanic should have userId

        spec = spec.and(ssSpec);
        spec.and(StandardServiceOrderSpecification.hasMechanicIdOrCustomerId(userId));
        List<StandardServiceOrder> orders = standardServiceOrderRepository.findAll(spec);
        // filter the ones which contain userId as either mechanicId or customerId
        orders = orders.stream().filter(v -> Objects.equals(v.getCustomer().getId(), userId) || Objects.equals(v.getMechanicStandardService().getMechanic().getId(), userId)).collect(Collectors.toList());
        return orderMapper.mapToStandardServiceOrderResponseDto(orders);
    }

    public Optional<StandardServiceOrder> getStandardOrder(Long id) {
        return standardServiceOrderRepository.findById(id);
    }
}
