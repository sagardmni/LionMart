package models;

import java.util.Date;

/**
 * Created by akshay on 10/29/2016.
 */
public class Product {
    private long id;
    private long uploadedBy;
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

    public Product() {

    }

    public Product(long id, long uploadedBy, String imagePath, float price, String description,
                   Date dateUploaded, Date dateSold, float priceBought, String onlineLink,
                   float soldPrice, int condition, int months, int category) {
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

    public void setMonths(int months) {
        this.months = months;
    }
    public float getPriceBought() {
        return priceBought;
    }

    public void setPriceBought(float priceBought) {
        this.priceBought = priceBought;
    }

    public long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }
    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
