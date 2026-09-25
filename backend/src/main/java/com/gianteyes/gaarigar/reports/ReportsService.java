package com.gianteyes.gaarigar.reports;

import com.gianteyes.gaarigar.Order.MechanicOrder.MechanicOrderService;
import com.gianteyes.gaarigar.Order.OrderService;
import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.mechanic.MechanicModel;
import com.gianteyes.gaarigar.mechanic.MechanicService;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpModel;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpRepository;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpService;
import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceModel;
import com.gianteyes.gaarigar.standardservice.MechanicStandardServiceService;
import com.gianteyes.gaarigar.standardservice.StandardServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportsService {
    @Autowired
    private StandardServiceService standardServiceService;
    @Autowired
    private MechanicService mechanicService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private MechanicOrderService mechanicOrderService;

    @Autowired
    private MechanicStandardServiceService mechanicStandardServiceService;

    @Autowired
    private PetrolPumpService petrolPumpService;

    public List<Object> getMostUsedServices(LocalDateTime startDate, LocalDateTime endDate) {
        return standardServiceService.getMostRequestedStandardServices(startDate, endDate);
    }

    public List<Object> getMostCancelledPetrolPumps(LocalDateTime startDate, LocalDateTime endDate){
        return petrolPumpService.getMostCancelledPetrolPumps(startDate, endDate);
    }

    public List<Object> getMostCancelledMechanic(LocalDateTime startDate, LocalDateTime endDate){
        return mechanicService.getMostCancelledMechanics(startDate, endDate);
    }

    public List<Object> getMostActivePetrolPumps(LocalDateTime startDate, LocalDateTime endDate){
        return petrolPumpService.getMostActivePetrolPumps(startDate, endDate);
    }

    public List<Object> getBestMechanicServices(LocalDateTime startDate, LocalDateTime endDate) {
        return mechanicStandardServiceService.getBestMechanicServices(startDate, endDate);
    }

    public List<MechanicModel> getTopMechanics() {
        return mechanicService.getTopRatedMechanics();
    }

    public Long getNewCustomersCount(LocalDateTime startDate, LocalDateTime endDate) {
        return customerService.getCountOfNewCustomers(startDate, endDate);
    }
    public Long getNewMechanicsCount(LocalDateTime startDate, LocalDateTime endDate) {
        return mechanicService.getCountOfNewMechanics(startDate, endDate);
    }

    public Long getNewPetrolPumpsCount(LocalDateTime startDate, LocalDateTime endDate) {
        return petrolPumpService.getCountOfNewPetrolPumps(startDate, endDate);
    }
    public Long getCompletedOrdersCount(LocalDateTime startDate, LocalDateTime endDate) {
        return orderService.getCompletedOrdersCount(startDate, endDate);
    }

    public Long getCancelledOrdersCount(LocalDateTime startDate, LocalDateTime endDate) {
        return orderService.getCancelledOrdersCount(startDate, endDate);
    }


}
