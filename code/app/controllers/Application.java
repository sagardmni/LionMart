package controllers;

import models.Product;
import models.User;
import play.mvc.*;
import play.db.jpa.*;
import views.html.*;
import play.data.FormFactory;
import javax.inject.Inject;
import java.sql.*;
import java.util.*;
import java.util.Date;
import play.data.DynamicForm;
import play.data.Form;

public class Application extends Controller {

    public Result index() {
        return ok(main.render());
    }

    public Result loginFail() {
        return ok(loginFail.render());
    }

//    public Result loginSuccess() {
//        String myDriver = "com.mysql.jdbc.Driver";
//        String myURL = "jdbc:mysql://localhost:3306/users";
//        int i=0;
//        List<String> name_list = new ArrayList<>();
//        List<String> location_list = new ArrayList<>();
//        List<String> image_list = new ArrayList<>();
//        List<Integer> cost_list = new ArrayList<>();
//        List<Integer> years_list = new ArrayList<>();
//        try {
//            Class.forName(myDriver);
//            Connection conn = DriverManager.getConnection(myURL, "root", "1234");
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery("SELECT * FROM item_table");
//            while(rs.next()) {
//                name_list.add(rs.getString("name"));
//                location_list.add(rs.getString("location"));
//                cost_list.add(rs.getInt("cost"));
//                years_list.add(rs.getInt("years"));
//                image_list.add("images/"+rs.getString("image_name"));
//                i++;
//            }
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        return ok(loginSuccess.render(name_list,location_list,cost_list,years_list,image_list));
//    }

    public Result contactSeller() {
        return ok(contact.render());
    }

    public Result postItem(){
        return ok(postItem.render());
    }

    public Result processItemForm(){
        //Process form here
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";

        try{
            Date date = new Date();
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select count(*) from product");
            rs.next();
            String imagePath = dynamicForm.get("item_picture");
            Product p = new Product(rs.getInt(1)+1,2,imagePath,Float.valueOf(dynamicForm.get("price")),dynamicForm.get("item_description"),date,date, 25,dynamicForm.get("item_link"), 0,Integer.parseInt(dynamicForm.get("item_condition")),Integer.parseInt(dynamicForm.get("item_months")),Integer.parseInt(dynamicForm.get("item_category")),dynamicForm.get("item_location"));
        addProduct(p);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return redirect(routes.Application.displayProducts(0));
    }

    public Result processSoldItem(){
        return redirect(routes.Application.displayProducts(0));
    }

    public Result viewItem(){
        return ok(viewItem.render());
    }

    public Result editItem(){
        return ok(editItem.render());
    }

    public Result markSold(){
        return ok(markSold.render());
    }

    public Result showUser(){
        return ok(user.render());
    }

    @Transactional
    public Result addUser(String fbID, String fbName, String fbEmail) throws ClassNotFoundException {
        if(checkIfUserExists(fbID)){
            return redirect(routes.Application.displayProducts(0));
        }
        String[] nameSplit = fbName.split(" ");
        String fname = nameSplit[0];
        String lname = nameSplit[1];
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), email VARCHAR(60))");

            st.executeUpdate("INSERT INTO user(id, fname, lname, email) VALUES ('"+ fbID +"','"+ fname+"','"+lname+"','"+fbEmail+"')");
            conn.close();
            return redirect(routes.Application.displayProducts(0));
        } catch (SQLException e) {
            e.printStackTrace();
            return ok(main.render());
        }

    }

    public boolean addProduct(Product p) throws ClassNotFoundException {

        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:00',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id INT NOT NULL)");
            //TODO img path
            // TODO fb ID
            ResultSet rs = st.executeQuery("SELECT * from product");
            int rowcount=0;
            if (rs.last()) {
                rowcount = rs.getRow();
            }
            java.sql.Timestamp product_timestamp = new java.sql.Timestamp(p.getDateUploaded().getTime());
            st.executeUpdate("INSERT INTO product(id,imagepath, price, category, price_bought, description, date_upload,online_link,price_sold,product_condition,months_used,location,user_id) VALUES ("+p.getId()+",'"+p.getImagePath()+"',"+p.getPrice()+","+ p.getCategory()+","+p.getPriceBought()+",'"+p.getDescription()+"','"+product_timestamp+"','"+ p.getOnlineLink()+"',"+p.getSoldPrice()+","+p.getCondition()+","+p.getMonths()+",'"+p.getLocation()+"',"+p.getUploadedBy()+")");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Result addPerson(){
        return ok(index.render());
    }


//    public boolean checkCredentials(String email, String pwd) {
//        String myDriver = "com.mysql.jdbc.Driver";
//        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/users";
//        int numRows=0;
//
//        try {
//            Class.forName(myDriver);
//            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
//            Statement st = conn.createStatement();
//            ResultSet rs = st.executeQuery("SELECT * FROM user_table WHERE email='"+ email +"' AND password='"+ pwd +"'");
//            while(rs.next()) {
//                numRows++;
//            }
//            System.out.println(numRows);
//            conn.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        if(numRows == 1){
//            return true;
//        } else {
//            return false;
//        }
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





    public static boolean checkLimitForUser(long id) throws ClassNotFoundException {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery("SELECT COUNT(*) products WHERE user_id = "+id+" AND price_sold=-1;");
            while(rs.next()){
                if (rs.getInt("NOUSER")>100) {
                    return false;
                }
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    public void displayProducts() throws ClassNotFoundException {
        displayProducts(0);
    }

    public Result displayProducts(int pagenum) throws ClassNotFoundException {
        int start = pagenum*20;
        ArrayList<Product> displayList = new ArrayList<Product>();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/lionmart?zeroDateTimeBehavior=convertToNull";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP DEFAULT '1970-01-01 00:00:01',online_link VARCHAR(255),price_sold DECIMAL(8,2),product_condition TINYINT NOT NULL,months_used INT,location VARCHAR(255) NOT NULL, user_id INT NOT NULL)");
            ResultSet rs = st.executeQuery("SELECT * FROM product ORDER BY date_upload");//LIMIT"+start+", 20 ORDER BY date_upload;");

            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getLong("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("product_condition"),rs.getInt("months_used"),rs.getInt("category"),rs.getString("location"));
                displayList.add(obj);
            }
            conn.close();
            return ok(loginSuccess.render(displayList));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}