package com.gianteyes.gaarigar.webapp;

import com.gianteyes.gaarigar.customer.CustomerService;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerRequestDto;
import com.gianteyes.gaarigar.mechanic.MechanicService;
import com.gianteyes.gaarigar.mechanic.dto.request.UpdateMechanicRequestDto;
import com.gianteyes.gaarigar.petrolpump.PetrolPumpService;
import com.gianteyes.gaarigar.petrolpump.dto.request.UpdatePetrolPumpRequestDto;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminUserController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private MechanicService mechanicService;

    @Autowired
    private PetrolPumpService petrolPumpService;

    @RequestMapping("/admin/customers")
    public String customer(Model model) {
        model.addAttribute("users", customerService.getAll());
        model.addAttribute("userType", "Customers");
        return "users";
    }

    @RequestMapping("/admin/customers/edit/{id}")
    public String editCustomer(@PathVariable("id") Long id, Model model) {
        UpdateCustomerRequestDto customer = customerService.getCustomerDetails(id);
        if (customer.getImage() == null) {
            customer.setImage("https://www.w3schools.com/howto/img_avatar.png");
        }
        model.addAttribute("user", customer);
        model.addAttribute("userType", "Customer");
        return "editUser";
    }

    @PostMapping("/admin/customer/update")
    public String updateCustomer(UpdateCustomerRequestDto customer) throws ParseException, IllegalAccessException {
        customerService.updateCustomerDetails(customer, customer.getId());
        return "redirect:/admin/customers";
    }


    //    for mechanic
    @RequestMapping("/admin/mechanics")
    public String mechanic(Model model) {
        model.addAttribute("users", mechanicService.getAll());
        model.addAttribute("userType", "Mechanics");
        return "users";
    }

    @RequestMapping("/admin/mechanics/edit/{id}")
    public String editMechanic(@PathVariable("id") Long id, Model model) {
        UpdateMechanicRequestDto mechanic = mechanicService.getMechanicDetails(id);
        if (mechanic.getImage() == null) {
            mechanic.setImage("https://www.w3schools.com/howto/img_avatar.png");
        }
        model.addAttribute("user", mechanic);
        model.addAttribute("userType", "Mechanic");
        return "editUser";
    }

    @PostMapping("/admin/mechanic/update")
    public String updateMechanic(UpdateMechanicRequestDto mechanic) throws ParseException, IllegalAccessException {
        mechanicService.updateMechanicDetails(mechanic, mechanic.getId());
        return "redirect:/admin/mechanics";
    }

    //    for petrol pump
    @RequestMapping("/admin/petrolpumps")
    public String petrolPump(Model model) {
        model.addAttribute("users", petrolPumpService.getAll());
        model.addAttribute("userType", "Petrol Pumps");
        return "users";
    }

    @RequestMapping("/admin/petrolpumps/edit/{id}")
    public String editPetrolPump(@PathVariable("id") Long id, Model model) {
        UpdatePetrolPumpRequestDto petrolPump = petrolPumpService.getPetrolPumpDetails(id);
        if (petrolPump.getImage() == null) {
            petrolPump.setImage("https://www.w3schools.com/howto/img_avatar.png");
        }
        model.addAttribute("user", petrolPump);
        model.addAttribute("userType", "PetrolPump");
        return "editUser";
    }

    @PostMapping("/admin/petrolpump/update")
    public String updatePetrolPump(UpdatePetrolPumpRequestDto petrolPump) throws ParseException, IllegalAccessException {
        petrolPumpService.updatePetrolPumpDetails(petrolPump, petrolPump.getId());
        return "redirect:/admin/petrolpumps";
    }
}
