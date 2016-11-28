package models;

import controllers.routes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    public boolean addToDatabase() throws ClassNotFoundException {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), email VARCHAR(60))");

            st.executeUpdate("INSERT INTO user(id, fname, lname, email) VALUES ('"+ this.fbId +"','"+ this.firstName +"','"+ this.lastName +"','"+ this.email+"')");
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
