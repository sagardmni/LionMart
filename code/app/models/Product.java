package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
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
    private String paymentMethod;

    public Product() {

    }

    public Product(long id, String uploadedBy, String imagePath, float price, String description,
                   Date dateUploaded, Date dateSold, float priceBought, String onlineLink,
                   float soldPrice, int condition, int months, int category, String location, String paymentMethod) {
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
        if (paymentMethod == "")
            this.paymentMethod = "Unspecified";
        else this.paymentMethod = paymentMethod;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
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

    public boolean performProductInsert(Connection conn){
        try{
            PreparedStatement prepSt = conn.prepareStatement("INSERT INTO product(id,imagepath, price, category, price_bought,"
                                             +"description, date_upload,online_link,price_sold,product_condition,months_used,"
                                             +"location,user_id, payment_method) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            prepSt.setLong(1,id);
            prepSt.setString(2,imagePath);
            prepSt.setFloat(3,price);
            prepSt.setInt(4,category);
            prepSt.setFloat(5,priceBought);
            prepSt.setString(6,description);
            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(dateUploaded.getTime());
            prepSt.setTimestamp(7,product_timestamp);
            prepSt.setString(8,onlineLink);
            prepSt.setFloat(9,soldPrice);
            prepSt.setInt(10,condition);
            prepSt.setInt(11,months);
            prepSt.setString(12,location);
            prepSt.setString(13, uploadedBy);
            prepSt.setString(14,paymentMethod);
            prepSt.executeUpdate();
        } catch(SQLException e)
        {
            e.printStackTrace();
            return false;
        }
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
            myURL = "jdbc:mysql://localhost/database1?zeroDateTimeBehavior=convertToNull";
        }
        try {
            Class.forName(myDriver);
            if(isTest)
            {
                conn = DriverManager.getConnection(myURL, "root", "");
            }
            else
            {
                conn = DriverManager.getConnection(myURL, "user1", "0Database!");
            }
            Statement st = conn.createStatement();
            PreparedStatement prepSt = null;
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");

            //Check conditions before actually attempting to insert into database
            boolean shouldInsert = checkConditions();
            if (!shouldInsert || !performProductInsert(conn))
            {
                return false;
            }
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
    // the below method is made for specific test cases and is not redundant.  DO NOT DELETE.
    public boolean addProductToDatabase2(boolean isTest) throws ClassNotFoundException {
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
            myURL = "jdbc:mysql://localhost/database1?zeroDateTimeBehavior=convertToNull";
        }
        try {
            Class.forName(myDriver);
            if(isTest)
            {
                conn = DriverManager.getConnection(myURL, "root", "");
            }
            else
            {
                conn = DriverManager.getConnection(myURL, "user1", "0Database!");
            }
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");

            //Check conditions before actually attempting to insert into database
            boolean shouldInsert = checkConditions();
            if (!shouldInsert || !performProductInsert(conn))
            {
                return false;
            }
            //Confirm that product is, in fact, inserted into DB.
            ResultSet rs = st.executeQuery("SELECT * from product where id = "+id);
            if(!rs.next())
                returnVal = false;
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return returnVal;
    }

    public boolean updateProductInDatabase() throws ClassNotFoundException {
        return updateProductInDatabase(false);
    }

    public boolean markAsSold(String price_sold, Timestamp product_sold_timestamp, boolean isTest) throws ClassNotFoundException{
        try{
            String myDriver = null;
            String myURL = null;
            if(isTest)
            {
                myDriver = "com.mysql.jdbc.Driver";
                myURL = "jdbc:mysql://localhost/mydatabase?zeroDateTimeBehavior=convertToNull";
            }
            else
            {
                myDriver = "com.mysql.jdbc.Driver";
                myURL = "jdbc:mysql://localhost/database1?zeroDateTimeBehavior=convertToNull";
            }
            Class.forName(myDriver);
            Connection conn = null;
            if(isTest)
                conn = DriverManager.getConnection(myURL, "root", "");
            else
                conn = DriverManager.getConnection(myURL, "user1", "0Database!");
            PreparedStatement prepSt = conn.prepareStatement("UPDATE product SET price_sold = ?, date_sold=? WHERE id=?");
            prepSt.setString(1,price_sold);
            prepSt.setTimestamp(2, product_sold_timestamp);
            prepSt.setLong(3,id);
            if(checkConditions())
                prepSt.executeUpdate();
            else
            {
                System.out.println("Product.checkConditions() failed");
                return false;
            }
        } catch(SQLException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateProductInDatabase(boolean isTest) throws ClassNotFoundException {
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
            myURL = "jdbc:mysql://localhost/database1?zeroDateTimeBehavior=convertToNull";
        }
        try {
            Class.forName(myDriver);
            if(isTest)
            {
                conn = DriverManager.getConnection(myURL, "root", "");
            }
            else
            {
                conn = DriverManager.getConnection(myURL, "user1", "0Database!");
            }
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP,online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");
            PreparedStatement prepSt = null;
            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(this.getDateUploaded().getTime());
            //Check conditions before actually attempting to update into database
            boolean shouldInsert = checkConditions();

            if (shouldInsert) {
                System.out.println("shouldinsert is True!");
                prepSt = conn.prepareStatement("UPDATE product SET price=?,category=?,price_bought=?,description=?,"
                                                +"date_upload=?,online_link=?,price_sold=?,product_condition=?,months_used=?," 
                                                + "location=? WHERE id=?");
                prepSt.setFloat(1,price);
                prepSt.setInt(2,category);
                prepSt.setFloat(3,priceBought);
                prepSt.setString(4,description);
                prepSt.setTimestamp(5,product_timestamp);
                prepSt.setString(6,onlineLink);
                prepSt.setFloat(7,soldPrice);
                prepSt.setInt(8,condition);
                prepSt.setInt(9,months);
                prepSt.setString(10,location);
                prepSt.setLong(11,id);
                prepSt.executeUpdate();
            }
            else
            {
                System.out.println("product upload check conditions failed");
                return false;
            }

            //Confirm that product is, in fact, inserted into DB.
            prepSt = conn.prepareStatement("SELECT * from product where id = ?");
            prepSt.setLong(1,id);
            ResultSet rs = prepSt.executeQuery();
            if(!rs.next()){
                System.out.println("updated product not found!");
                returnVal = false;
            }
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
