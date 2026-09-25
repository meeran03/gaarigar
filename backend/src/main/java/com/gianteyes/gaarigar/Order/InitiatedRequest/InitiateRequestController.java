package com.gianteyes.gaarigar.Order.InitiatedRequest;

import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.AcceptFuelDeliveryRequestDto;
import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.InitiateFuelRequestResponseDto;
import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.InitiateMechanicRequestResponseDto;
import com.gianteyes.gaarigar.Order.InitiatedRequest.dto.InitiatedRequestDto;
import com.gianteyes.gaarigar.Order.dto.OrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/initiate-request")
public class InitiateRequestController {
    @Autowired
    private InitiateRequestService initiateRequestService;

    @PostMapping("/create/mechanic-request")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('CUSTOMER') and @access.sameUser(#obj.customer)")
    public InitiateMechanicRequestResponseDto createMechanicRequest(@Valid @RequestBody InitiatedRequestDto obj) {
        return initiateRequestService.createMechanicRequest(obj);
    }

    @PostMapping("/accept/mechanic-request/{id}/{mechanicId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#mechanicId) and @access.nearbyRequest(#id,'MECHANIC')")
    public OrderResponseDto acceptMechanicRequest(@PathVariable Long id, @PathVariable Long mechanicId) {
        return initiateRequestService.acceptMechanicRequest(mechanicId, id);
    }

    @PostMapping("/reject/mechanic-request/{id}/{mechanicId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#mechanicId) and @access.nearbyRequest(#id,'MECHANIC')")
    public void rejectMechanicRequest(@PathVariable Long id, @PathVariable Long mechanicId) {
        initiateRequestService.rejectMechanicRequest(mechanicId, id);
    }


    @PostMapping("/reject/fuel-delivery-request/{id}/{petrolPumpId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#petrolPumpId) and @access.nearbyRequest(#id,'PETROL_PUMP')")
    public void rejectFuelDeliveryRequest(@PathVariable Long id, @PathVariable Long petrolPumpId) {
        initiateRequestService.rejectFuelDeliveryRequest(petrolPumpId, id);
    }


    @PostMapping("/ask/mechanic/{mechanicId}/{initiatedRequestId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.requestOwner(#initiatedRequestId)")
    public ResponseEntity<Object> askMechanic(@PathVariable Long mechanicId, @PathVariable Long initiatedRequestId) {
        return initiateRequestService.askMechanic(mechanicId, initiatedRequestId);
    }

    @PostMapping("/ask/petrolpump/{petrolpumpId}/{initiatedRequestId}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.requestOwner(#initiatedRequestId)")
    public ResponseEntity<Object> askPetrolPump(@PathVariable Long petrolpumpId, @PathVariable Long initiatedRequestId) {
        return initiateRequestService.askPetrolPump(petrolpumpId, initiatedRequestId);
    }

    @PostMapping("/create/fuel-delivery-request")
    @org.springframework.security.access.prepost.PreAuthorize("@access.role('CUSTOMER') and @access.sameUser(#obj.customer)")
    public InitiateFuelRequestResponseDto createFuelDeliveryRequest(@Valid @RequestBody InitiatedRequestDto obj) {
        return initiateRequestService.createFuelDeliveryRequest(obj);
    }

    @PostMapping("/accept/fuel-delivery-request")
    @org.springframework.security.access.prepost.PreAuthorize("@access.sameUser(#obj.petrolPumpId) and @access.nearbyRequest(#obj.requestId,'PETROL_PUMP')")
    public void acceptFuelDeliveryRequest(@Valid @RequestBody AcceptFuelDeliveryRequestDto obj) {
        initiateRequestService.acceptFuelDeliveryRequest(obj);
    }

    @PutMapping("/cancel/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("@access.requestOwner(#id)")
    public void cancel(@PathVariable Long id) {
        this.initiateRequestService.cancel(id);
    }
}
