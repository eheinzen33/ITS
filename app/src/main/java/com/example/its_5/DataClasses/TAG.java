package com.example.its_5.DataClasses;

import java.sql.Date;

public class TAG {
    private Date date;
    private String EPC;
    private String product;
    private int quantity;
    private String stage;
    private boolean isChecked = false;

    public TAG(Date date, String EPC, String product, int quantity, String stage) {
        this.date = date;
        this.EPC = EPC;
        this.product = product;
        this.quantity = quantity;
        this.stage = stage;
    }

    public Date getDate() {
        return date;
    }

    public String getEPC() {
        return EPC;
    }

    public String getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStage() {
        return stage;
    }





    public void setChecked() {
        isChecked = true;
    }

    public void uncheck(){
        isChecked = false;
    }
    public boolean IsChecked(){
        return isChecked;

    }
}
