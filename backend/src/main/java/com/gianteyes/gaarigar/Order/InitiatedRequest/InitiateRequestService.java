package com.gianteyes.gaarigar.Order.InitiatedRequest;

import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelDeliveryOrderModel;
import com.gianteyes.gaarigar.Order.FuelDeliveryOrder.FuelOrderService;
import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.AcceptFuelDeliveryRequestDto;
import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.InitiateFuelRequestResponseDto;
import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.InitiateMechanicRequestResponseDto;
import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.InitiatedRequestDto;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderModel;
import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderService;
import com.gianteyes.gaarigar.Order.OrderMapper;
import com.gianteyes.gaarigar.Order.OrderModel;
import com.gianteyes.gaarigar.Order.OrderStatus;
import com.gianteyes.gaarigar.Order.OrderType;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import com.gianteyes.gaarigar.PubSub.Publisher;
import com.gianteyes.gaarigar.PubSub.RequestMessage;
import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.exceptions.InvalidRequestStatusException;
import com.gianteyes.gaarigar.exceptions.ResourceNotFoundException;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicService;
import com.gianteyes.gaarigar.notification.Note;
import com.gianteyes.gaarigar.notification.NotificationUtil;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpService;
import com.gianteyes.gaarigar.user.UserModel;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
public class InitiateRequestService {
    @Autowired
    private InitiateRequestRepository initiateRequestRepository;
    @Autowired
    private MechanicService mechanicService;

    @Autowired
    private PetrolPumpService petrolPumpService;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private Publisher publisher;

    @Autowired
    private FuelOrderService fuelOrderService;
    @Autowired
    private MechanicOrderService mechanicOrderService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private NotificationUtil notificationUtil;
    @Autowired
    private OrderMapper orderMapper;


    public void sendToMechanics(InitiateRequestModel request, List<? extends UserModel> mechanicModels) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("notes", request.getNotes());
        dataMap.put("latitude", request.getLocation().getLatitude().toString());
        dataMap.put("longitude", request.getLocation().getLongitude().toString());
        dataMap.put("customer", request.getCustomer().getFirstName() + " " + request.getCustomer().getLastName());
        RequestMessage requestMessage = RequestMessage.builder()
                .sender(request.getCustomer().getPhone())
                .data(dataMap).build();
        publisher.publish(requestMessage, mechanicModels, "/topic/mechanic-request");
    }

    public InitiateMechanicRequestResponseDto createMechanicRequest(InitiatedRequestDto obj) {
        List<MechanicModel> nearbyMechanics = mechanicService.searchNearbyMechanic(obj.getMechanicType(), obj.getLocation());
        if (nearbyMechanics.isEmpty()) {
            InitiateMechanicRequestResponseDto responseDto = InitiateMechanicRequestResponseDto.builder().build();
            return responseDto;
        }
        InitiateRequestModel initiatedRequestModel = mapper.map(obj, InitiateRequestModel.class);
        initiatedRequestModel.setCustomer(customerService.getByUserId(obj.getCustomer()));
        initiatedRequestModel.setOrderStatus(OrderStatus.REQUESTED);
        initiatedRequestModel.setOrderType(OrderType.MECHANIC);
        initiatedRequestModel.setRequestedAt(now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES));
        initiateRequestRepository.save(initiatedRequestModel);
        InitiateMechanicRequestResponseDto responseDto = InitiateMechanicRequestResponseDto.builder()
                .id(initiatedRequestModel.getId()).build();
        responseDto.setNearbyMechanicsFromMechanics(nearbyMechanics);
        return responseDto;
    }


    public InitiateFuelRequestResponseDto createFuelDeliveryRequest(InitiatedRequestDto obj) {
        List<PetrolPumpModel> nearbyPetrolPumps = petrolPumpService.searchNearbyPetrolPump(obj.getLocation());
        if (nearbyPetrolPumps.isEmpty()) {
            InitiateFuelRequestResponseDto responseDto = InitiateFuelRequestResponseDto.builder().build();
            return responseDto;
        }
        InitiateRequestModel initiatedRequestModel = mapper.map(obj, InitiateRequestModel.class);
        initiatedRequestModel.setCustomer(customerService.getByUserId(obj.getCustomer()));
        LocalDateTime n = LocalDateTime.now();
        // strip the seconds part
        LocalDateTime now = n.truncatedTo(ChronoUnit.MINUTES);
        initiatedRequestModel.setRequestedAt(now);
        initiatedRequestModel.setOrderStatus(OrderStatus.REQUESTED);
        initiatedRequestModel.setNoOfLitres(obj.getNoOfLitres());
        initiateRequestRepository.save(initiatedRequestModel);
//        sendToMechanics(initiatedRequestModel, nearbyMechanics);
        InitiateFuelRequestResponseDto responseDto = InitiateFuelRequestResponseDto.builder()
                .id(initiatedRequestModel.getId()).build();
        responseDto.setNearbyPetrolPumpsFromPetrolPumps(nearbyPetrolPumps);
        return responseDto;
    }


    public Note createAskMechanicNote(InitiateRequestModel request, MechanicModel mechanic) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("notes", request.getNotes());
        dataMap.put("latitude", request.getLocation().getLatitude().toString());
        dataMap.put("longitude", request.getLocation().getLongitude().toString());
        dataMap.put("customer", request.getCustomer().getFirstName() + " " + request.getCustomer().getLastName());
        dataMap.put("type", request.getOrderType().toString());
        dataMap.put("requestedAt", request.getRequestedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dataMap.put("id", request.getId().toString());
        return Note.builder()
                .subject("New Request")
                .content("You have a new request from " + request.getCustomer().getFirstName() + " " + request.getCustomer().getLastName())
                .token(mechanic.getFcmToken())
                .data(dataMap)
                .user(mechanic)
                .build();
    }

    public Note createAskPetrolPumpNote(InitiateRequestModel request, PetrolPumpModel petrolPump) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("notes", request.getNotes());
        dataMap.put("latitude", request.getLocation().getLatitude().toString());
        dataMap.put("longitude", request.getLocation().getLongitude().toString());
        dataMap.put("customer", request.getCustomer().getFirstName() + " " + request.getCustomer().getLastName());
        dataMap.put("noOfLitres", request.getNoOfLitres().toString());
        dataMap.put("type", request.getOrderType().toString());
        dataMap.put("requestedAt", request.getRequestedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dataMap.put("id", request.getId().toString());
        dataMap.put("email", request.getCustomer().getEmail());
        return Note.builder()
                .subject("New Request")
                .content("You have a new request from " + request.getCustomer().getFirstName() + " " + request.getCustomer().getLastName())
                .token(petrolPump.getFcmToken())
                .data(dataMap)
                .user(petrolPump)
                .build();
    }

    public ResponseEntity<Object> askMechanic(Long mechanicId, Long initiatedRequestId) {
        MechanicModel mechanicModel = mechanicService.getByUserId(mechanicId);
        InitiateRequestModel initiateRequestModel = initiateRequestRepository.findById(initiatedRequestId).orElseThrow();
        Note note = createAskMechanicNote(initiateRequestModel, mechanicModel);
        notificationUtil.sendNotification(note);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Object> askPetrolPump(Long petrolPumpId, Long initiatedRequestId) {
        PetrolPumpModel petrolPumpModel = petrolPumpService.getByUserId(petrolPumpId);
        InitiateRequestModel initiateRequestModel = initiateRequestRepository.findById(initiatedRequestId).orElseThrow();
        Note note = createAskPetrolPumpNote(initiateRequestModel, petrolPumpModel);
        notificationUtil.sendNotification(note);
        return ResponseEntity.ok().build();
    }

    public OrderResponseDto acceptMechanicRequest(Long mechanicId, Long requestId) {
        InitiateRequestModel initiateRequestModel = initiateRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request Not Found"));
        if (initiateRequestModel.getOrderStatus() == OrderStatus.ACCEPTED || initiateRequestModel.getOrderStatus() == OrderStatus.REJECTED)
            throw new InvalidRequestStatusException(requestId, "has been acccepted or cancelled");
        MechanicModel mechanicModel = mechanicService.getByUserId(mechanicId);
        if (mechanicModel == null)
            throw new ResourceNotFoundException("Mechanic", "id", mechanicId);
        initiateRequestModel.setOrderStatus(OrderStatus.ACCEPTED);
        MechanicOrderModel order = mechanicOrderService.convertRequestToOrder(initiateRequestModel, mechanicModel);
        Note note = mechanicOrderService.createAcceptedNoteForCustomer(order);
        notificationUtil.sendNotification(note);
        initiateRequestRepository.save(initiateRequestModel);
        return orderMapper.mapMechanicOrderToOrderResponse(order);
    }

    public void rejectMechanicRequest(Long mechanicId, Long requestId) {
        InitiateRequestModel initiateRequestModel = initiateRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request Not Found"));
        if (initiateRequestModel.getOrderStatus() != OrderStatus.REQUESTED)
            throw new RuntimeException("Cannot Accept");
        initiateRequestModel.setOrderStatus(OrderStatus.REJECTED);
        Note note = createRejectedNoteForCustomer(initiateRequestModel);
        notificationUtil.sendNotification(note);
        initiateRequestRepository.save(initiateRequestModel);
    }

    public void rejectFuelDeliveryRequest(Long petrolPumpId, Long requestId) {
        InitiateRequestModel initiateRequestModel = initiateRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request Not Found"));
        if (initiateRequestModel.getOrderStatus() != OrderStatus.REQUESTED)
            throw new RuntimeException("Cannot Accept");
        initiateRequestModel.setOrderStatus(OrderStatus.REJECTED);
        Note note = createRejectedNoteForCustomer(initiateRequestModel);
        notificationUtil.sendNotification(note);
        initiateRequestRepository.save(initiateRequestModel);
    }

    public Note createRejectedNoteForCustomer(InitiateRequestModel request) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("email", request.getCustomer().getEmail());
        return Note.builder()
                .subject(request.getOrderType().toString() + " Request Rejected")
                .content("Your request has been rejected.")
                .user(request.getCustomer())
                .data(new HashMap<>())
                .token(request.getCustomer().getFcmToken()).build();
    }

    public void acceptFuelDeliveryRequest(AcceptFuelDeliveryRequestDto obj) {
        Long requestId = obj.getRequestId();
        Long petrolPumpId = obj.getPetrolPumpId();
        InitiateRequestModel initiateRequestModel = initiateRequestRepository.findById(requestId).orElseThrow(() -> new RuntimeException("Request Not Found"));
        if (initiateRequestModel.getOrderStatus() == OrderStatus.ACCEPTED || initiateRequestModel.getOrderStatus() == OrderStatus.REJECTED)
            throw new InvalidRequestStatusException(requestId, "has been acccepted or cancelled");
        PetrolPumpModel petrolPumpModel = petrolPumpService.getByUserId(petrolPumpId);
        if (petrolPumpModel == null)
            throw new ResourceNotFoundException("PetrolPump", "id", petrolPumpId);
        initiateRequestModel.setOrderStatus(OrderStatus.ACCEPTED);
        FuelDeliveryOrderModel order = fuelOrderService.convertRequestToOrder(initiateRequestModel, petrolPumpModel, obj.getLiterPrice(), obj.getDistance());
        initiateRequestRepository.save(initiateRequestModel);
    }

    public void cancel(Long id) {
        InitiateRequestModel order = this.initiateRequestRepository.findById(id).orElseThrow();
        if (order.getOrderStatus() != OrderStatus.COMPLETED && order.getOrderStatus() != OrderStatus.REJECTED) {
            order.setOrderStatus(OrderStatus.REJECTED);
            initiateRequestRepository.save(order);
        } else {
            throw new InvalidRequestStatusException(id, "has already been completed or cancelled");
        }
    }

    public Note createAcceptedNoteForCustomer(OrderModel orderModel) {
        return Note.builder()
                .subject("Service Request Accepted")
                .content("Your service order for " + orderModel.getOrderType() + " has been accepted.")
                .data(new HashMap<String, String>() {{
                    put("type", "service");
                    put("id", orderModel.getId().toString());
                    put("email", orderModel.getCustomer().getEmail());
                    put("payment method", orderModel.getPaymentMethod().toString());
                }})
                .templateName("OrderPlacedNotification")
                .mailSubject("Service Request Accepted")
                .user(orderModel.getCustomer())
                .token(orderModel.getCustomer().getFcmToken()).build();
    }

    public OrderModel convertToOrder(OrderModel orderModel, InitiateRequestModel obj) {
        mapper.map(obj, orderModel);
        LocalDateTime acceptedAt = now(ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES);
        orderModel.setAcceptedAt(acceptedAt);
        notificationUtil.sendNotification(createAcceptedNoteForCustomer(orderModel));
        return orderModel;
    }
}
