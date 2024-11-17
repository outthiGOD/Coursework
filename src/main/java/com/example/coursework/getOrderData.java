package com.example.coursework;

public class getOrderData {
    private Integer orderId;
    private String nameUser;
    private String addressPickUpPoint;
    private String orderStatusName;
    private String nameProduct;
    private Integer orderQuantity;

    public getOrderData(Integer orderId, String nameUser, String addressPickUpPoint, String orderStatusName, String nameProduct, Integer orderQuantity) {
        this.orderId = orderId;
        this.nameUser = nameUser;
        this.addressPickUpPoint = addressPickUpPoint;
        this.orderStatusName = orderStatusName;
        this.nameProduct = nameProduct;
        this.orderQuantity = orderQuantity;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getOrderUserName() {
        return nameUser;
    }

    public String getAddressPickUpPoint() {
        return addressPickUpPoint;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }
}
