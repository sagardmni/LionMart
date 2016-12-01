package controllers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeCreator;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.util.RawValue;

import models.Product;
import models.User;
import play.mvc.*;
import play.db.jpa.*;
import scala.util.parsing.json.JSONObject;
import scala.util.parsing.json.JSONObject$;
import views.html.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.Date;
import play.data.DynamicForm;
import play.data.Form;

public class Application extends Controller {

    private String currentFbID = "-1";

    public Result index(){
        return redirect(routes.Application.main(0,0));
    }
    public Result main(int pagenum, int category) {
        ArrayList<Product> displayList = new ArrayList<Product>();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            int offset = pagenum*20;
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
            ResultSet rs;
            if (category == 0)
                rs = st.executeQuery("SELECT * FROM product ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            else
                rs = st.executeQuery("SELECT * FROM product where category=" + category +" ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"));
                displayList.add(obj);
            }
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ok(main.render(displayList, category, pagenum));
    }

    public Result contactSeller() {
        return ok(contact.render());
    }

    public Result postItem(){
        return ok(postItem.render(0));
    }

    public Result displayProductLimitError(){
        return ok(productLimit.render());
    }

    public Result processEditItemForm(long productID) throws ClassNotFoundException {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";

        try{
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
//            ResultSet rs = st.executeUpdate("update product set ");
//            int maxID = 0;
//            if(rs.next()) {
//                maxID = rs.getInt(1);
//            }
//            String imagePath = dynamicForm.get("item_picture");
//            Product p = new Product(maxID+1,currentFbID,imagePath,Float.valueOf(dynamicForm.get("price")),dynamicForm.get("item_description"),date,date, Float.valueOf(dynamicForm.get("original_price")),dynamicForm.get("item_link"), -1,Integer.parseInt(dynamicForm.get("item_condition")),Integer.parseInt(dynamicForm.get("item_months")),Integer.parseInt(dynamicForm.get("item_category")),dynamicForm.get("item_location"));
//            p.addProductToDatabase();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return redirect(routes.Application.displayProducts(0,0));
    }

    public Result processItemForm() throws ClassNotFoundException {
        if(checkLimitForUser(currentFbID)){
            return displayProductLimitError();
        }

        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";

        try{
            Date date = new Date();
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select max(id) from product");
            int maxID = 0;
            if(rs.next()) {
                maxID = rs.getInt(1);
            }
            String imagePath = dynamicForm.get("item_picture");
            Product p = new Product(maxID+1,currentFbID,imagePath,Float.valueOf(dynamicForm.get("price")),dynamicForm.get("item_description"),date,date, Float.valueOf(dynamicForm.get("original_price")),dynamicForm.get("item_link"), -1,Integer.parseInt(dynamicForm.get("item_condition")),Integer.parseInt(dynamicForm.get("item_months")),Integer.parseInt(dynamicForm.get("item_category")),dynamicForm.get("item_location"));
            p.addProductToDatabase();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return redirect(routes.Application.displayProducts(0,0));
    }



    @BodyParser.Of(play.mvc.BodyParser.Json.class)
    public Result predictPrice() throws ClassNotFoundException{
        JsonNode x = request().body().asJson();
        int category = x.get("category").intValue();
        int condition = x.get("condition").intValue();
        int months = x.get("months").intValue();
        System.out.println(category+","+condition+","+months);
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        double ratio = 0;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM product WHERE category="+ category + " ORDER BY date_upload DESC LIMIT 5");
            int count = 0;
            while(rs.next()){
                float priceBought = rs.getFloat("price_bought");
                float priceSold = rs.getFloat("price");
                ratio += priceBought/priceSold;
                count +=1;
            }
            if (count!=0)
                ratio/=count;
            double roundOff = Math.round(ratio * 100.0) / 100.0;
            ratio = roundOff;
            conn.close();
            System.out.println(ratio);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(-1);
        }
        org.json.simple.JSONObject ab = new org.json.simple.JSONObject();
        ab.put("value",Double.toString(ratio));
        return ok(ab.toString());
    }

    public Result processSoldItem(){
        return redirect(routes.Application.displayProducts(0,0));
    }

    public Result viewItem(){
        return ok(viewItem.render());
    }

    public Result editItem() throws ClassNotFoundException{
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int productID = Integer.parseInt(dynamicForm.get("button"));
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        Product currentProduct = null;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2) DEFAULT -1,product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
            ResultSet rs = st.executeQuery("SELECT * FROM product WHERE id="+ productID);
            if(rs.next()){
                currentProduct = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"));
            }
            conn.close();
            return ok(editItem.render(currentProduct));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Result markSold(){
        return ok(markSold.render());
    }

    public Result showUser() throws ClassNotFoundException {
        ArrayList<models.Product> userProductList = new ArrayList<models.Product>();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        User thisUser = null;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM user WHERE id='"+ currentFbID + "'");
            if(rs.next()){
                thisUser = new User(rs.getLong("id"),rs.getString("fname"),rs.getString("lname"),rs.getString("email"));
            }

            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2) DEFAULT -1,product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
            rs = st.executeQuery("SELECT * FROM product WHERE user_id='"+ currentFbID+"' ORDER BY date_upload DESC");

            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"));
                userProductList.add(obj);
            }
            conn.close();
            return ok(user.render(userProductList, thisUser));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public Result addUser(String fbID, String fbName, String fbEmail) throws ClassNotFoundException {
//    <<<<<<<<<  SHIFTED TO CLASS >>>>>>
//        String myDriver = "com.mysql.jdbc.Driver";
//        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
//        try {
//            Class.forName(myDriver);
//            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
//            Statement st = conn.createStatement();
//            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), email VARCHAR(60))");
//
//            st.executeUpdate("INSERT INTO user(id, fname, lname, email) VALUES ('"+ fbID +"','"+ fname+"','"+lname+"','"+fbEmail+"')");
//            conn.close();
//            return redirect(routes.Application.displayProducts(0));
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return redirect(routes.Application.main(0));
//        }
        currentFbID = fbID;
        if(checkIfUserExists(fbID)){
            return redirect(routes.Application.displayProducts(0,0));
        }
        String[] nameSplit = fbName.split(" ");
        String fname = nameSplit[0];
        String lname = nameSplit[1];
        User newUser = new User(Long.parseLong(fbID),fname,lname,fbEmail);
        if(newUser.addToDatabase()){
            return redirect(routes.Application.displayProducts(0,0));
        }
        else{
            return redirect(routes.Application.main(0,0));
        }
    }

//    <<<<<<<<<  SHIFTED TO CLASS >>>>>>
//    public boolean addProduct(Product p) throws ClassNotFoundException {
//
//
//
//        String myDriver = "com.mysql.jdbc.Driver";
//        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
//
//        try {
//            Class.forName(myDriver);
//            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
//            Statement st = conn.createStatement();
//            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:00',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
//            //TODO img path
//            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(p.getDateUploaded().getTime());
//            st.executeUpdate("INSERT INTO product(id,imagepath, price, category, price_bought, description, date_upload,online_link,price_sold,product_condition,months_used,location,user_id) VALUES ("+p.getId()+",'"+p.getImagePath()+"',"+p.getPrice()+","+ p.getCategory()+","+p.getPriceBought()+",'"+p.getDescription()+"','"+product_timestamp+"','"+ p.getOnlineLink()+"',"+p.getSoldPrice()+","+p.getCondition()+","+p.getMonths()+",'"+p.getLocation()+"', '"+p.getUploadedBy()+"')");
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//        return true;
//    }

    public static boolean checkIfUserExists(String id) throws ClassNotFoundException {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), email VARCHAR(60))");
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS NOUSER FROM user WHERE id ='"+ id +"';");
            while(rs.next()){
                if (rs.getInt("NOUSER")==1) {
                    return true;
                }
                return false;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean checkLimitForUser(String id) throws ClassNotFoundException {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) from product WHERE user_id = '" + id + "' AND price_sold = -1 ;");
            if (rs.next()) {
                if (rs.getInt(1) > 99) {
                    return true;
                }
                return false;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public Result displayProducts(int pagenum, int category) throws ClassNotFoundException {
        ArrayList<Product> displayList = new ArrayList<Product>();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            int offset = pagenum*20;
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2) DEFAULT -1,product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL)");
            ResultSet rs;
            if(category == 0)
                rs = st.executeQuery("SELECT * FROM product WHERE user_id!='"+ currentFbID+"' ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            else
                rs = st.executeQuery("SELECT * FROM product WHERE category=" +category+ " and user_id!='"+ currentFbID+"' ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"));
                displayList.add(obj);
            }
            conn.close();
            return ok(home.render(displayList,category,pagenum));

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}