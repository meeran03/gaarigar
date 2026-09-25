package com.example.mechanicapp.Models;

public class OrdersModel {


    public OrdersModel(String s) {
        this.order_detail = s;
    }

    public String getOrder_detail() {
        return order_detail;
    }

    public void setOrder_detail(String order_detail) {
        this.order_detail = order_detail;
    }

    private String order_detail;
}
