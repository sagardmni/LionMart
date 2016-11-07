package models;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by akshay on 10/29/2016.
 */
public class Product {
    private long id;
    private String uploadedBy;
    private String imagePath;
    private float price;
    private String description;
    private Date dateUploaded;
    private Date dateSold;
    private float priceBought;
    private String onlineLink;
    private float soldPrice;
    private int condition;
    private int months;
    private int category;
    private String location;

    public Product() {

    }

    public Product(long id, String uploadedBy, String imagePath, float price, String description,
                   Date dateUploaded, Date dateSold, float priceBought, String onlineLink,
                   float soldPrice, int condition, int months, int category, String location) {
        this.id = id;
        this.uploadedBy = uploadedBy;
        this.imagePath = imagePath;
        this.price = price;
        this.description = description;
        this.dateUploaded = dateUploaded;
        this.dateSold = dateSold;
        this.priceBought = priceBought;
        this.onlineLink = onlineLink;
        this.soldPrice = soldPrice;
        this.condition = condition;
        this.months = months;
        this.category = category;
        this.location = location;
    }

    public long getId() {

        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Date getDateSold() {
        return dateSold;
    }

    public void setDateSold(Date dateSold) {
        this.dateSold = dateSold;
    }

    public String getOnlineLink() {
        return onlineLink;
    }

    public void setOnlineLink(String onlineLink) {
        this.onlineLink = onlineLink;
    }

    public float getSoldPrice() {
        return soldPrice;
    }

    public void setSoldPrice(float soldPrice) {
        this.soldPrice = soldPrice;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getMonths() {
        return months;
    }

    public String mapMonthsToString() {
        String[] months = new String[4];
        months[0] = "Less than 3 months";
        months[1] = "3-6 months";
        months[2] = "6 months - 3 years";
        months[3] = ">3 years";
        return months[this.getMonths()];
    }

    public void setMonths(int months) {
        this.months = months;
    }
    public float getPriceBought() {
        return priceBought;
    }

    public void setPriceBought(float priceBought) {
        this.priceBought = priceBought;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
