package com.gianteyes.gaarigar.webapp;

import com.gianteyes.gaarigar.reports.ReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminReportsController {
    @Autowired
    private ReportsService reportsService;

    public List<Object> getMostUsedServices(LocalDateTime startDate, LocalDateTime endDate) {
        return reportsService.getMostUsedServices(startDate, endDate);
    }

    @RequestMapping("/admin/reports/services")
    @Transactional
    public String getMostUsedServices(Model model, @RequestParam("startDate") Optional<String> startDate, @RequestParam("endDate") Optional<String> endDate) {

        LocalDateTime startDateTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endDateTime = LocalDateTime.now();
        if (startDate.isPresent()) {
            startDateTime = LocalDateTime.parse(startDate.get());
        }
        if (endDate.isPresent()) {
            endDateTime = LocalDateTime.parse(endDate.get());
        }
        List<Object> services = reportsService.getMostUsedServices(startDateTime, endDateTime);
        Long newCustomersCount = reportsService.getNewCustomersCount(startDateTime, endDateTime);
        Long completedOrdersCount = reportsService.getCompletedOrdersCount(startDateTime, endDateTime);
        Long cancelledOrdersCount = reportsService.getCancelledOrdersCount(startDateTime, endDateTime);
        model.addAttribute("topServices", services);
        model.addAttribute("newCustomersCount", newCustomersCount);
        model.addAttribute("completedOrdersCount", completedOrdersCount);
        model.addAttribute("cancelledOrdersCount", cancelledOrdersCount);
        model.addAttribute("startDate", startDateTime.toString());
        model.addAttribute("endDate", endDateTime.toString());
        return "serviceReports";
    }

    @RequestMapping("/admin/reports/users")
    @Transactional
    public String getActivePetrolPump(Model model, @RequestParam("startDate") Optional<String> startDate, @RequestParam("endDate") Optional<String> endDate) {

        LocalDateTime startDateTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endDateTime = LocalDateTime.now();
        if (startDate.isPresent()) {
            startDateTime = LocalDateTime.parse(startDate.get());
        }
        if (endDate.isPresent()) {
            endDateTime = LocalDateTime.parse(endDate.get());
        }

        Long newCustomersCount = reportsService.getNewCustomersCount(startDateTime, endDateTime);
        Long newMechanicsCount = reportsService.getNewMechanicsCount(startDateTime, endDateTime);
        Long newPetrolPumpsCount = reportsService.getNewPetrolPumpsCount(startDateTime, endDateTime);
        List<Object> cancelledPetrolPumpOrders = reportsService.getMostCancelledPetrolPumps(startDateTime, endDateTime);
        List<Object> cancelledMechanicOrders = reportsService.getMostCancelledMechanic(startDateTime, endDateTime);
        List<Object> mostActivePetrolPumps = reportsService.getMostActivePetrolPumps(startDateTime, endDateTime);
        List<Object> bestMechanicServices = reportsService.getBestMechanicServices(startDateTime, endDateTime);
        model.addAttribute("newCustomersCount", newCustomersCount);
        model.addAttribute("newMechanicsCount", newMechanicsCount);
        model.addAttribute("newPetrolPumpsCount", newPetrolPumpsCount);
        model.addAttribute("cancelledPetrolPumpOrdersCount", cancelledPetrolPumpOrders);
        model.addAttribute("cancelledMechanicOrdersCount", cancelledMechanicOrders);
        model.addAttribute("activePetrolPumps", mostActivePetrolPumps);
        model.addAttribute("bestMechanicServices", bestMechanicServices);


        model.addAttribute("startDate", startDateTime.toString());
        model.addAttribute("endDate", endDateTime.toString());

        return "userReports";
    }

}
