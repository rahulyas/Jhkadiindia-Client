package com.rahul.jhkadiindiaclient;

public class DataClass {
    private String productName;
    private String productRate;
    private String productDiscountrate;
    private String productDescription;
    private String dataImage;
    private String Key;

    public DataClass(String productName, String productRate, String productDiscountrate, String productDescription, String dataImage) {
        this.productName = productName;
        this.productRate = productRate;
        this.productDiscountrate = productDiscountrate;
        this.productDescription = productDescription;
        this.dataImage = dataImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductRate() {
        return productRate;
    }

    public void setProductRate(String productRate) {
        this.productRate = productRate;
    }

    public String getProductDiscountrate() {
        return productDiscountrate;
    }

    public void setProductDiscountrate(String productDiscountrate) {
        this.productDiscountrate = productDiscountrate;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public DataClass(){
    }

}
