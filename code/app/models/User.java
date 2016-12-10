package models;

import controllers.routes;
import views.html.home;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by akshay on 10/29/2016.
 */
public class User {

    private long fbId;
    private String firstName;
    private String lastName;
    private String email;

    public User() {
    }

    public User(long fbId, String firstName, String lastName, String email) {
        this.fbId = fbId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public long getFbId() {
        return fbId;
    }

    public void setFbId(long fbId) {
        this.fbId = fbId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean checkUserConditions(){
        if(Long.toString(fbId).length() > 25 || Long.toString(fbId).length() == 0 || firstName.length() > 30
                || firstName.length() == 0 || lastName.length() > 30 || lastName.length() == 0 || email.length() > 30
                || email.length() == 0)
            return false;
        return true;
    }

    public boolean addToDatabase() throws ClassNotFoundException {
        return addToDatabase(false);
    }

    public boolean addToDatabase(boolean isTest) throws ClassNotFoundException {
        String myDriver = null;
        String myURL = null;
        if(isTest) {
            myDriver = "com.mysql.jdbc.Driver";
            myURL = "jdbc:mysql://localhost:3306/mydatabase";
        }
        else
        {
            myDriver = "com.mysql.jdbc.Driver";
            myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
        }
        try {
            Class.forName(myDriver);
            Connection conn = null;
            if(isTest)
            {
                conn = DriverManager.getConnection(myURL, "root", "");
            }
            else
            {
                conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            }
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), email VARCHAR(60))");
            boolean shouldInsert = checkUserConditions();
            if (shouldInsert)
                st.executeUpdate("INSERT INTO user(id, fname, lname, email) VALUES ('"+ this.fbId +"','"+ this.firstName +"','"+ this.lastName +"','"+ this.email+"')");
            else
                return false;
            if(isTest)
                st.executeUpdate("DROP TABLE user;");
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

//    public ArrayList<Product> getProductsUploaded() throws ClassNotFoundException {
//        ArrayList<Product> productList = new ArrayList<Product>();
//        String myDriver = "com.mysql.jdbc.Driver";
//        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
//        try {
//            Class.forName(myDriver);
//            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
//            Statement st = conn.createStatement();
//            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2) DEFAULT -1,product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
//            ResultSet rs = st.executeQuery("SELECT * FROM product WHERE user_id='"+ this.fbId+"' ORDER BY date_upload DESC");
//
//            while(rs.next()){
//                Product obj = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"));
//                productList.add(obj);
//            }
//            conn.close();
//            return productList;
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}
