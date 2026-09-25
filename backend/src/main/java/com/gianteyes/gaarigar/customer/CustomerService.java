package com.gianteyes.gaarigar.customer;

import com.gianteyes.gaarigar.BaseUserService;
import com.gianteyes.gaarigar.config.GeneralConfig;
import com.gianteyes.gaarigar.customer.dto.CustomerMapper;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerOpDto;
import com.gianteyes.gaarigar.customer.dto.request.UpdateCustomerRequestDto;
import com.gianteyes.gaarigar.utils.FileUpload;
import org.locationtech.jts.io.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CustomerService extends BaseUserService<CustomerModel> {

    @Autowired
    private final CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private FileUpload fileUpload;
    @Autowired
    private GeneralConfig config;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        super(customerRepository);
        this.customerRepository = customerRepository;
    }

    public CustomerModel updateCustomerDetailsOptional(UpdateCustomerOpDto customer, Long customerId, MultipartFile file) {
        CustomerModel customerModel = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Customer not found"));
        if (Objects.nonNull(file)) {
            customerModel.setImage(this.uploadProfileImage(file, config.CUSTOMER_IMAGES_PATH));
        }
        if (Objects.nonNull(customer.getFirstName())) {
            customerModel.setFirstName(customer.getFirstName());
        }
        if (Objects.nonNull(customer.getLastName())) {
            customerModel.setLastName(customer.getLastName());
        }
        if (Objects.nonNull(customer.getPassword())) {
            customerModel.setPassword(customer.getPassword());
        } else {
            customerModel.setPassword(null);
        }
        customerModel = this.decorateForUpdateUser(customerModel, customerId);
        return this.updateUser(customerModel);
    }

    public CustomerModel updateCustomerDetails(UpdateCustomerRequestDto customer, Long userId) throws ParseException {
        if (!Objects.equals(customer.getImageFile().getOriginalFilename(), "")) {
            customer.setImage(this.uploadProfileImage(customer.getImageFile(), config.CUSTOMER_IMAGES_PATH));
        }
        if (!Objects.equals(customer.getPassword(), customer.getConfirmPassword())) {
            throw new IllegalStateException("Passwords do not match");
        }
        CustomerModel customerModel = this.customerMapper.convertToEntity(customer);
        CustomerModel updatedOriginal = this.decorateForUpdateUser(customerModel, userId);
        if (customerModel.getEmail() != null) {
            updatedOriginal.setEmail(customerModel.getEmail());
            updatedOriginal.setEmailNotifications(true);
        }
        return this.updateUser(updatedOriginal);
    }

    public CustomerModel getCustomerById(Long id) {
        return this.getByUserId(id);
    }

    public UpdateCustomerRequestDto getCustomerDetails(Long id) {
        UpdateCustomerRequestDto customerRequestDto = this.customerMapper.convertToDto(this.getCustomerById(id));
        if (customerRequestDto.getImage() != null) {
            customerRequestDto.setImage(fileUpload.generateUrl(customerRequestDto.getImage()));
        }
        return customerRequestDto;
    }

    public Long getCountOfNewCustomers(LocalDateTime start, LocalDateTime end) {
        return this.customerRepository.findByCreatedAtBetween(start, end).stream().count();
    }

}
