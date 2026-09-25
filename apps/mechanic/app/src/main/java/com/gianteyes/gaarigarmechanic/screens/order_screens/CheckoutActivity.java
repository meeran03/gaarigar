package com.gianteyes.gaarigarmechanic.screens.order_screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.PaymentResponseDto;
import com.gianteyes.gaarigarmechanic.providers.CustomContextProvider;
import com.gianteyes.gaarigarmechanic.screens.HomeActivity;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

public class CheckoutActivity extends AppCompatActivity {
    public PaymentResponseDto response;
    PaymentSheet paymentSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        this.response = CustomContextProvider.getInstance().getPaymentResponseDto();
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        PaymentConfiguration.init(getApplicationContext(), response.getPublishableKey());
    }

    @Override
    protected void onStart() {
        super.onStart();
        presentPaymentSheet();
    }

    private void presentPaymentSheet() {
        paymentSheet.presentWithPaymentIntent(
                response.getCustomerSecret()
        );
    }

    private void onPaymentSheetResult(
            final PaymentSheetResult paymentSheetResult
    ) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            Toast.makeText(this, "Payment completed", Toast.LENGTH_SHORT).show();
            // if its completed we go back to the Home Activity
        }
        // go back
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
