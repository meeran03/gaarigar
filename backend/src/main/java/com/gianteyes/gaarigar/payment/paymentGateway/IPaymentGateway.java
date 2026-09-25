package com.gianteyes.gaarigar.payment.paymentGateway;

import com.gianteyes.gaarigar.user.UserModel;

public interface IPaymentGateway {
    String createUserAtGateway(UserModel user);

    Boolean checkUserAtGateway(UserModel user);

    String getUserIdAtGateway(UserModel user);
}
