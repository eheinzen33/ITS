package com.example.its_5.DataClasses;

public class ProductDataClass {
    String date;
    String product;
    String stage;
    String quantity;

    public ProductDataClass(String date, String product, String stage, String quantity) {
        this.date = date;
        this.product = product;
        this.stage = stage;
        this.quantity = quantity;
    }

    public ProductDataClass(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
