package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.sql.ResultSet;

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
        return months[this.getMonths()-1];
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

    public boolean checkConditions(){
        if(price > 999999 || price < 0 || imagePath.length()>100 || imagePath.length() == 0 || priceBought > 999999
                || priceBought < 0 || description.length() == 0 || description.length() > 65535 ||
            category > 4 || category < 1 || onlineLink.length() > 255 || condition > 5 || condition < 1 ||
                months > 4 || months < 1 || location.length()>255 || location.length() == 0)
            return false;
        return true;
    }

    public boolean addProductToDatabase() throws ClassNotFoundException {
        return addProductToDatabase(false);
    }

    public boolean addProductToDatabase(boolean isTest) throws ClassNotFoundException {
        String myDriver = null;
        String myURL = null;
        Connection conn = null;
        boolean returnVal = true;
        if(isTest) {
            myDriver = "com.mysql.jdbc.Driver";
            myURL = "jdbc:mysql://localhost/mydatabase?zeroDateTimeBehavior=convertToNull";
        }
        else{
            myDriver = "com.mysql.jdbc.Driver";
            myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
        }
        try {
            Class.forName(myDriver);
            if(isTest)
            {
                conn = DriverManager.getConnection(myURL, "root", "");
            }
            else
            {
                conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            }
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:00',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
            //TODO img path
            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(this.getDateUploaded().getTime());
            //Check conditions before actually attempting to insert into database
            boolean shouldInsert = checkConditions();
            if (shouldInsert)
                st.executeUpdate("INSERT INTO product(id,imagepath, price, category, price_bought, description, date_upload,online_link,price_sold,product_condition,months_used,location,user_id) VALUES ("+this.getId()+",'"+this.getImagePath()+"',"+this.getPrice()+","+ this.getCategory()+","+this.getPriceBought()+",'"+this.getDescription()+"','"+product_timestamp+"','"+ this.getOnlineLink()+"',"+this.getSoldPrice()+","+this.getCondition()+","+this.getMonths()+",'"+this.getLocation()+"', '"+this.getUploadedBy()+"')");
            else
                return false;
            //Confirm that product is, in fact, inserted into DB.
            ResultSet rs = st.executeQuery("SELECT * from product where id = "+id);
            if(!rs.next())
                returnVal = false;
            if(isTest)
                st.executeUpdate("DROP TABLE product;");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return returnVal;
    }
}
