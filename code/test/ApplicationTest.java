import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import models.Product;
import models.User;
import controllers.Application;
import play.db.Database;
import play.db.Databases;
import java.util.Date;

import com.google.common.collect.ImmutableMap;

import java.sql.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;

/**
 *
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTest extends Application{
    String myDriver;
    String myURL;
    String name1;
    float price1;


    @Test
    public void UserRetrieveCheck() {
        myDriver = "com.mysql.jdbc.Driver";
        myURL = "jdbc:mysql://localhost/mydatabase";
        ResultSet rs = null;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), fbEmail VARCHAR(60))");
            st.executeUpdate("INSERT INTO user(id, fname, lname, fbEmail) VALUES ('123456789','akshay','kumar','ak@gmail.com')");

            rs = st.executeQuery("SELECT fname FROM user WHERE fbEmail=\'ak@gmail.com\'");
            while(rs.next()) {
                name1 = rs.getString("fname");
                System.out.println(rs.toString());
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        assertEquals("akshay", name1);
    }

    @Test
    public void ProductRetrieveCheck() {
        myDriver = "com.mysql.jdbc.Driver";
        myURL = "jdbc:mysql://localhost/mydatabase";
        ResultSet rs = null;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "");
            Statement st = conn.createStatement();
            Date d1 = new Date();
            Date d2 = new Date();

            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Mudd");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2) DEFAULT '-1.00',product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(p.getDateUploaded().getTime());
            st.executeUpdate("INSERT INTO product(id,imagepath, price, category, price_bought, description, date_upload,online_link,price_sold,product_condition,months_used,location,user_id) VALUES ("+p.getId()+",'"+p.getImagePath()+"',"+p.getPrice()+","+ p.getCategory()+","+p.getPriceBought()+",'"+p.getDescription()+"','"+product_timestamp+"','"+ p.getOnlineLink()+"',"+p.getSoldPrice()+","+p.getCondition()+","+p.getMonths()+",'"+p.getLocation()+"', '"+p.getUploadedBy()+"')");
            rs = st.executeQuery("SELECT price FROM product WHERE id='123456789';");
            if(rs.next()){
                price1 = rs.getFloat(1);
            }

            System.out.println(price1);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(12.34, price1,0.00);
    }

    @Test
    public void testCheckLimitForUser() throws ClassNotFoundException, SQLException {
        myDriver = "com.mysql.jdbc.Driver";
        myURL = "jdbc:mysql://localhost/mydatabase";
        int numRows = 0;
        ResultSet rs = null;
        boolean limitReached=false;
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myURL, "root", "");
        Statement st = conn.createStatement();
        Date d1 = new Date();
        Date d2 = new Date();
        Product[] prodArray = new Product[101];
        st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price FLOAT (8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought FLOAT(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold FLOAT(8,2) DEFAULT '-1.00',product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");

        for(int i=0;i<=100;i++){
            prodArray[i] = new Product(i,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Mudd");
            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(prodArray[i].getDateUploaded().getTime());
            st.executeUpdate("INSERT INTO product(id,imagepath, price, category, price_bought, description, date_upload,online_link,price_sold,product_condition,months_used,location,user_id) VALUES ("+prodArray[i].getId()+",'"+prodArray[i].getImagePath()+"',"+prodArray[i].getPrice()+","+ prodArray[i].getCategory()+","+prodArray[i].getPriceBought()+",'"+prodArray[i].getDescription()+"','"+product_timestamp+"','"+ prodArray[i].getOnlineLink()+"',"+prodArray[i].getSoldPrice()+","+prodArray[i].getCondition()+","+prodArray[i].getMonths()+",'"+prodArray[i].getLocation()+"', '"+prodArray[i].getUploadedBy()+"')");
        }
        rs = st.executeQuery("SELECT COUNT(*) FROM product WHERE user_id = '123456789' AND price_sold = -1.00;");
        rs.next();
        numRows = rs.getInt(1);
        System.out.println(numRows);
        if (numRows>99) {
            limitReached = true;
        }

        conn.close();
        assertTrue(limitReached);
    }
}