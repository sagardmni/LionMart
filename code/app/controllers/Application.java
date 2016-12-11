package controllers;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import models.Product;
import models.User;
import play.data.DynamicForm;
import play.data.Form;
import play.db.jpa.Transactional;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.editItem;
import views.html.home;
import views.html.main;
import views.html.markSold;
import views.html.postItem;
import views.html.productLimit;
import views.html.user;

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
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");
            ResultSet rs;
            if (category == 0)
                rs = st.executeQuery("SELECT * FROM product WHERE price_sold =-1 ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            else
                rs = st.executeQuery("SELECT * FROM product where price_sold =-1 AND category=" + category +" ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"), rs.getString("payment_method"));
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

    public Result postItem(){
        return ok(postItem.render(0));
    }

    public Result displayProductLimitError(){
        return ok(productLimit.render());
    }

    public Result processEditItemForm(long productID) throws ClassNotFoundException {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        Date date = new Date();
        Product p = new Product(productID,currentFbID,"chair.jpg",Float.valueOf(dynamicForm.get("price")),dynamicForm.get("item_description"),date,date, Float.valueOf(dynamicForm.get("original_price")),dynamicForm.get("item_link"), -1,Integer.parseInt(dynamicForm.get("item_condition")),Integer.parseInt(dynamicForm.get("item_months")),Integer.parseInt(dynamicForm.get("item_category")),dynamicForm.get("item_location"), "");
        p.updateProductInDatabase();
        return redirect(routes.Application.displayProducts(0,0));
    }

    public Result processItemForm() throws ClassNotFoundException {
        if(checkLimitForUser(currentFbID)){
            return displayProductLimitError();
        }

        Http.MultipartFormData dynamicForm = request().body().asMultipartFormData();
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
            String fileName; // Static analysis improvement
            String contentType;
            String fileNameSave; // Static analysis improvement
            fileNameSave = null;
            Http.MultipartFormData.FilePart picture = dynamicForm.getFile("item_picture");
            File file; // Static analysis improvement
            if (picture != null) {
                fileName = picture.getFilename();
                contentType = picture.getContentType();
                file = (File) picture.getFile();

                if(!(contentType.equals("image/png") || !(contentType.equals("image/jpeg")))){
                    flash("error", "Not an image");
                }
                String checkDots = "";
                checkDots = checkDots.concat(fileName);
                int numDots = checkDots.length() - checkDots.replace(".", "").length();
                if(numDots!=1){
                    flash("error", "Not an image");
                }
                //System.out.println(fileName);
                String extension = fileName.split("\\.")[1];
                Path source = Paths.get(file.getAbsolutePath());
                char separator = File.separatorChar;
                fileNameSave = (maxID+1)+"."+extension;
                Path target = Paths.get(Paths.get("").toAbsolutePath().toString()+separator+"app"+separator+"assets"+separator+"images"+separator+fileNameSave);
                Files.move(source,target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                flash("error", "Missing file");
            }

            String[] itemDescription = (String[])dynamicForm.asFormUrlEncoded().get("item_description");
            String[] itemLink = (String[])dynamicForm.asFormUrlEncoded().get("item_link");
            String[] itemLocation = (String[])dynamicForm.asFormUrlEncoded().get("item_location");
            String[] itemConditionArr = (String[])dynamicForm.asFormUrlEncoded().get("item_condition");
            int itemCondition = Integer.parseInt(itemConditionArr[0]);
            String[] itemMonthsArr = (String[])dynamicForm.asFormUrlEncoded().get("item_months");
            int itemMonths = Integer.parseInt(itemMonthsArr[0]);
            String[] itemCategoryArr = (String[])dynamicForm.asFormUrlEncoded().get("item_category");
            int itemCategory = Integer.parseInt(itemCategoryArr[0]);

            String[] paymentMethod = (String[])dynamicForm.asFormUrlEncoded().get("payment_method");

            String[] priceArr = (String[])dynamicForm.asFormUrlEncoded().get("price");
            Float setPrice = Float.parseFloat(priceArr[0]);
            String[] oriPriceArr = (String[])dynamicForm.asFormUrlEncoded().get("original_price");
            Float oriPrice = Float.parseFloat(oriPriceArr[0]);
            Product p = new Product(maxID+1,currentFbID,fileNameSave,setPrice,itemDescription[0],date,date, oriPrice,itemLink[0], -1, itemCondition, itemMonths, itemCategory,itemLocation[0], paymentMethod[0]);
            p.addProductToDatabase();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return redirect(routes.Application.displayProducts(0,0));
    }





    @BodyParser.Of(play.mvc.BodyParser.Json.class)
    public Result predictPrice() throws ClassNotFoundException{
        System.out.println("Got here");
        JsonNode x = request().body().asJson();
        int category = x.get("category").intValue();
        int condition = x.get("condition").intValue();
        int months = x.get("months").intValue();
        int originalPrice = x.get("originalPrice").intValue();
        System.out.println(category+","+condition+","+months);
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        double ratio = 0;
        double predictedPrice = 0;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM product WHERE category="+ category + " ORDER BY date_upload DESC LIMIT 5");
            int count = 0;
            while(rs.next()){
                float priceBought = rs.getFloat("price_bought");
                float priceSold = rs.getFloat("price");
                ratio += priceSold/priceBought;
                count +=1;
            }
            if (count!=0)
                ratio/=count;
            predictedPrice = ratio*originalPrice;
            predictedPrice = Math.round(predictedPrice*100.0) / 100.0;
            conn.close();
            System.out.println(predictedPrice);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(-1);
        }
        org.json.simple.JSONObject ab = new org.json.simple.JSONObject();
        ab.put("value",Double.toString(predictedPrice));
        return ok(ab.toString());
    }

    public Result processSoldItem(long productID) throws ClassNotFoundException, SQLException {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String price_sold = dynamicForm.get("price_sold");
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        Class.forName(myDriver);
        Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
        Statement st = conn.createStatement();
        st.executeUpdate("UPDATE product SET price_sold = "+price_sold+" WHERE id="+productID);
        return redirect(routes.Application.displayProducts(0,0));
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
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");
            ResultSet rs = st.executeQuery("SELECT * FROM product WHERE id="+ productID);
            if(rs.next()){
                currentProduct = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"), rs.getString("payment_method"));
            }
            conn.close();
            return ok(editItem.render(currentProduct));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Result markSold() throws ClassNotFoundException {
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        int productID = Integer.parseInt(dynamicForm.get("button"));
        System.out.println(productID);
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        Product currentProduct = null;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");
            ResultSet rs = st.executeQuery("SELECT * FROM product WHERE id="+ productID);
            if(rs.next()){
                currentProduct = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"), rs.getString("payment_method"));
            }
            conn.close();
            return ok(markSold.render(currentProduct));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

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

            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");
            rs = st.executeQuery("SELECT * FROM product WHERE price_sold = -1 AND user_id='"+ currentFbID+"' ORDER BY date_upload DESC");

            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"), rs.getString("payment_method"));
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
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id VARCHAR(25) NOT NULL, payment_method VARCHAR(255))");
            ResultSet rs;
            if(category == 0)
                rs = st.executeQuery("SELECT * FROM product WHERE price_sold = -1 AND user_id!='"+ currentFbID+"' ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            else
                rs = st.executeQuery("SELECT * FROM product WHERE price_sold = -1 AND category=" +category+ " and user_id!='"+ currentFbID+"' ORDER BY date_upload DESC LIMIT 20 OFFSET "+Integer.toString(offset));
            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getString("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"), rs.getString("payment_method"));
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