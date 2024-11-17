package com.example.coursework;

public class getProductsData {
    private Integer productsId;
    private String productsName;
    private Integer productsQuantity;
    private String nameManufacturer;
    private String nameProvider;
    private String nameCatalog;
    private Integer productsPrice;
    private String productsImage;

    public getProductsData(Integer productsId, String productsName, Integer productsQuantity, String nameManufacturer, String nameProvider, String nameCatalog, Integer productsPrice, String productsImage){
        this.productsId = productsId;
        this.productsName = productsName;
        this.productsQuantity = productsQuantity;
        this.nameManufacturer = nameManufacturer;
        this.nameProvider = nameProvider;
        this.nameCatalog = nameCatalog;
        this.productsPrice = productsPrice;
        this.productsImage = productsImage;
    }

    public Integer getProductsId(){
        return productsId;
    }

    public String getProductsName(){
        return productsName;
    }

    public Integer getProductsQuantity(){
        return productsQuantity;
    }

    public String getNameManufacturer() {
        return nameManufacturer;
    }

    public String getNameProvider() {
        return nameProvider;
    }

    public String getNameCatalog() {
        return nameCatalog;
    }

    public Integer getProductsPrice(){
        return productsPrice;
    }

    public String getProductsImage(){
        return productsImage;
    }

    public void setProductsQuantity(Integer productsQuantity){
        this.productsQuantity = productsQuantity;
    }

    public void setProductsPrice(Integer productsPrice){
        this.productsPrice = productsPrice;
    }
}