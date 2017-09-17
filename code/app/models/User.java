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
            myURL = "jdbc:mysql://localhost/mydatabase";
        }
        else
        {
            myDriver = "com.mysql.jdbc.Driver";
            myURL = "jdbc:mysql://localhost/database1?zeroDateTimeBehavior=convertToNull";
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
                conn = DriverManager.getConnection(myURL, "user1", "0Database!");
            }
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), email VARCHAR(60))");
            boolean shouldInsert = checkUserConditions();
            if (shouldInsert)
            {
                PreparedStatement prepSt = conn.prepareStatement("INSERT INTO user(id, fname, lname, email) VALUES (?,?,?,?)");
                prepSt.setString(1,Long.toString(this.fbId));
                prepSt.setString(2,this.firstName);
                prepSt.setString(3,this.lastName);
                prepSt.setString(4,this.email);
                prepSt.executeUpdate();
            }
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

}
