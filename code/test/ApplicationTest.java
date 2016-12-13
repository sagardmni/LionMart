import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import models.Product;
import models.User;
import controllers.Application;
import play.db.Database;
import play.db.Databases;
import java.util.Date;
import java.text.DecimalFormat;

import com.google.common.collect.ImmutableMap;

import java.sql.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;

/**
 *  !!  IMPORTANT MESSAGE !!
 *  PLEASE CHECK THE LOCAL DATABASE CONFIGURATIONS DURING TESTING. IT RUNS ON 3306 PORT IN MY PC.
 *  - Akshay
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTest extends Application{
    String myDriver;
    String myURL;


    @Test
    public void ValidUserInsertRetrieveCheck() {
        try {
            User u = new User(123456789,"akshay","kumar","ak@gmail.com");
            assertTrue(u.addToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidNameLengthRetrieveCheck() {
        try {
            String invalidFname = new String(new char[31]).replace("\0", "a");
            User u = new User(123456789,invalidFname,"kumar","ak@gmail.com");
            assertFalse(u.addToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidEmailLengthRetrieveCheck() {
        try {
            String invalidEmail = new String(new char[31]).replace("\0", "a");
            User u = new User(123456789,"akshay","kumar",invalidEmail);
            assertFalse(u.addToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ValidProductInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Mudd", "blah");
            assertTrue(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidPriceProductInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            int invalidPrice = 1000000;
            Product p = new Product(1234,"123456789","defaultImagePath", invalidPrice,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Mudd", "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidPriceBoughtProductInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            int invalidPriceBought = 1000000;
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, invalidPriceBought,"http://amazon.com", 11.00f,2,2,2,"Mudd", "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidImagepathProductInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            String imagePath = new String(new char[256]).replace("\0", "a");
            Product p = new Product(1234,"123456789",imagePath, 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Mudd", "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidProductConditionInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            int invalidCondition = 6;
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,invalidCondition,2,2,"Mudd","");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidProductMonthsInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            int invalidMonths = 5;
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,invalidMonths,2,"Mudd", "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidProductCategoryInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            int invalidCategory = 5;
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,invalidCategory,"Mudd", "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidProductDescriptionInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            String invalidDescription = new String(new char[65536]).replace("\0", "a");
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,invalidDescription,d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Mudd", "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidProductLocationInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            String location = new String(new char[256]).replace("\0", "a");
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,location, "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InvalidProductLinkInsertRetrieveCheck() {
        try {
            Date d1 = new Date();
            Date d2 = new Date();
            String invalidLink = new String(new char[256]).replace("\0", "a");
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,invalidLink, 11.00f,2,2,2,"Mudd", "");
            assertFalse(p.addProductToDatabase(true));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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
        st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");

        for(int i=0;i<=100;i++){
            prodArray[i] = new Product(i,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", -1,2,2,2,"Mudd", "");
            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(prodArray[i].getDateUploaded().getTime());
            st.executeUpdate("INSERT INTO product(id,imagepath, price, category, price_bought, description, date_upload,online_link,price_sold,product_condition,months_used,location,user_id) VALUES ("+prodArray[i].getId()+",'"+prodArray[i].getImagePath()+"',"+prodArray[i].getPrice()+","+ prodArray[i].getCategory()+","+prodArray[i].getPriceBought()+",'"+prodArray[i].getDescription()+"','"+product_timestamp+"','"+ prodArray[i].getOnlineLink()+"',"+prodArray[i].getSoldPrice()+","+prodArray[i].getCondition()+","+prodArray[i].getMonths()+",'"+prodArray[i].getLocation()+"', '"+prodArray[i].getUploadedBy()+"')");
        }
        rs = st.executeQuery("SELECT COUNT(*) FROM product WHERE user_id = '123456789' AND price_sold = -1;");
        if(rs.next()){
            numRows = rs.getInt(1);
            System.out.println(numRows);
        }
        if (numRows>99) {
            limitReached = true;
        }
        st.executeUpdate("DROP TABLE product;");
        conn.close();
        assertTrue(limitReached);
    }

    @Test
    public void checkUserConstructorEmail(){
        User u = new User(123,"test","user","testuser@gmail.com");
        assertEquals(u.getEmail(),"testuser@gmail.com");
    }
    @Test
    public void checkUserConstructorName(){
        User u = new User(123,"test","user","testuser@gmail.com");
        String fullName = u.getFirstName()+u.getLastName();
        assertEquals(fullName,"testuser");
    }
    @Test
    public void checkUserConstructorFBId(){
        User u = new User();
        u.setFbId(123);
        u.setEmail("testuser@gmail.com");
        u.setFirstName("test");
        u.setLastName("user");
        assertEquals(u.getFbId(),123);
    }

    @Test
    public void checkProductConstructor(){
        Product p = new Product();
        p.setCategory(1);
        p.setCondition(1);
        p.setId(200);
        p.setImagePath("path");
        p.setSoldPrice(12.3f);
        p.setPrice(12.3f);
        p.setDescription("description");
        p.setMonths(1);
        Date d = new Date();
        p.setDateUploaded(d);
        p.setDateSold(d);
        p.setPriceBought(12.3f);
        p.setUploadedBy("akshay");
        p.setLocation("mudd");
        p.setPaymentMethod("credit card");
        assertEquals(p.getLocation(),"mudd");
    }

    @Test
    public void checkProductValidUpdateTest() throws ClassNotFoundException {
        Date d1 = new Date();
        Date d2 = new Date();
        Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "");
        p.addProductToDatabase2(true);
        Product q = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"MUDD", "");
        assertEquals(true, q.updateProductInDatabase(true));
    }

    @Test
    public void checkProductInvalidUpdateTest() throws ClassNotFoundException {
        Date d1 = new Date();
        Date d2 = new Date();
        Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "");
        p.addProductToDatabase2(true);
        Product q = new Product(1234,"123456789","defaultImagePath", -12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"MUDD", "");
        assertEquals(false, q.updateProductInDatabase(true));
    }

    @Test
    public void validateMarkSold() throws ClassNotFoundException {
        try
        {
            Date d1 = new Date();
            Date d2 = new Date();
            float soldPrice = 6;
            Product p = new Product(1234,"123456789","defaultImagePath", 12.34f,"description",d1,d2, 25.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "");
            p.addProductToDatabase2(true);
            Date soldDate = new Date();
            java.sql.Timestamp product_sold_timestamp = new java.sql.Timestamp(soldDate.getTime());
            p.markAsSold(Float.toString(soldPrice),product_sold_timestamp,true);
            myDriver = "com.mysql.jdbc.Driver";
            myURL = "jdbc:mysql://localhost/mydatabase";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * from product where id=1234");
            float actualSoldPrice = 0;
            if(rs.next())
            {
                actualSoldPrice = rs.getFloat("price_sold");
            }
            assertEquals(soldPrice, actualSoldPrice, 0);
            st.executeUpdate("DROP TABLE product;");
        } catch(SQLException e){
            e.printStackTrace();
            assert(false);
        }
    }



    @Test
    public void checkPredictPrice() throws ClassNotFoundException, SQLException {
        myDriver = "com.mysql.jdbc.Driver";
        myURL = "jdbc:mysql://localhost/mydatabase";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myURL, "root", "");
        Statement st = conn.createStatement();
        st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");
        Date d1 = new Date();
        Date d2 = new Date();
        Product p = new Product(12,"123456789","defaultImagePath", 10.00f,"description",d1,d2, 20.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "Venmo");
        p.addProductToDatabase2(true);
        Product q = new Product(13,"123456789","defaultImagePath", 20.00f,"description",d1,d2, 40.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "Venmo");
        q.addProductToDatabase2(true);
        Product r = new Product(14,"123456789","defaultImagePath", 40.00f,"description",d1,d2, 80.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "Venmo");
        r.addProductToDatabase2(true);
        Product s = new Product(15,"123456789","defaultImagePath", 40.00f,"description",d1,d2, 80.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "Venmo");
        s.addProductToDatabase2(true);
        Product t = new Product(16,"123456789","defaultImagePath", 40.00f,"description",d1,d2, 80.00f,"http://amazon.com", 11.00f,2,2,2,"Library", "Venmo");
        t.addProductToDatabase2(true);


        ResultSet rs = st.executeQuery("SELECT * FROM product WHERE category=2 ORDER BY date_upload DESC LIMIT 5");
        int count = 0;
        double ratio = 0;
        double predictedPrice;
        double originalPrice=100.0;
        while(rs.next()){
            double priceBought = rs.getFloat("price_bought");
            double priceSold = rs.getFloat("price");
            ratio += priceSold/priceBought;
            count +=1;
        }
        if (count!=0)
            ratio/=count;
        predictedPrice = ratio*originalPrice;
        System.out.println(predictedPrice);
        st.executeUpdate("DROP TABLE product;");
        conn.close();
        assertTrue(50.0==predictedPrice);

    }
}