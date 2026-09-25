package com.gianteyes.gaarigarmechanic.providers;

import android.location.Location;

import com.gianteyes.gaarigarmechanic.models.OrderModel;
import com.gianteyes.gaarigarmechanic.models.PaymentResponseDto;

import java.util.List;

public class CustomContextProvider {
    private static CustomContextProvider instance;
    private List<OrderModel> fetchedActiveOrders;
    private Location currentLocation;
    private PaymentResponseDto paymentResponseDto;

    private CustomContextProvider() {
    }

    public static CustomContextProvider getInstance() {
        if (CustomContextProvider.instance == null) {
            CustomContextProvider.instance = new CustomContextProvider();
        }
        return CustomContextProvider.instance;
    }

    public List<OrderModel> getFetchedActiveOrders() {
        return this.fetchedActiveOrders;
    }

    public void setOrders(final List<OrderModel> fetchedActiveOrders) {
        this.fetchedActiveOrders = fetchedActiveOrders;
    }

    public OrderModel findByOrderId(final Long orderId) {
        for (final OrderModel order : this.fetchedActiveOrders) {
            if (order.getOrderId().equals(orderId)) {
                return order;
            }
        }
        return null;
    }

    public Location getCurrentLocation() {
        return this.currentLocation;
    }

    public void setCurrentLocation(final Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public PaymentResponseDto getPaymentResponseDto() {
        return this.paymentResponseDto;
    }

    public void setPaymentResponseDto(final PaymentResponseDto paymentResponseDto) {
        this.paymentResponseDto = paymentResponseDto;
    }

}
