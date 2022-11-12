package com.example.its_5;

public class ProductClass {

    String User_ID;
    String epc;
    String Product;

    public ProductClass(String user_ID, String epc, String product) {
        User_ID = user_ID;
        this.epc = epc;
        Product = product;
    }

    public ProductClass(){

    }

    public String getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(String user_ID) {
        User_ID = user_ID;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getProduct() {
        return Product;
    }

    public void setProduct(String product) {
        Product = product;
    }
}
