package com.gianteyes.gaarigar.common.admin;

import com.gianteyes.gaarigar.BaseUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AdminService extends BaseUserService<AdminModel> {

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        super(adminRepository);
    }
}
