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

public class Application extends Controller {

    public Result index() {
        return ok(index.render());
    }

    public Result loginFail() {
        return ok(loginFail.render());
    }

    public Result loginSuccess() {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/users";
        int i=0;
        List<String> name_list = new ArrayList<>();
        List<String> location_list = new ArrayList<>();
        List<String> image_list = new ArrayList<>();
        List<Integer> cost_list = new ArrayList<>();
        List<Integer> years_list = new ArrayList<>();
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "1234");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM item_table");
            while(rs.next()) {
                name_list.add(rs.getString("name"));
                location_list.add(rs.getString("location"));
                cost_list.add(rs.getInt("cost"));
                years_list.add(rs.getInt("years"));
                image_list.add("images/"+rs.getString("image_name"));
                i++;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ok(loginSuccess.render(name_list,location_list,cost_list,years_list,image_list));
    }

    public Result contactSeller() {
        return ok(contact.render());
    }

    public Result postItem(){
        return ok(postItem.render());
    }

    public Result processItemForm(){
        return redirect(routes.Application.loginSuccess());
    }

    public Result processSoldItem(){
        return redirect(routes.Application.loginSuccess());
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
    public boolean addUser(User u) throws ClassNotFoundException {

        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/users";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS user (id VARCHAR(25) PRIMARY KEY, fname VARCHAR(30), lname VARCHAR(30), email VARCHAR(60))");
            st.executeUpdate("INSERT INTO user(id, fname, lname, email, type) VALUES ("+ u.getFbId()
                    +","+ u.getFirstName()+","+u.getFirstName()+","+u.getLastName()+","+u.getEmail()+")" );
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addProduct(Product p) throws ClassNotFoundException {

        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/users";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2), imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP,online_link VARCHAR(255),price_sold DECIMAL(8,2),productCondition TINYINT NOT NULL,months_used INT,user_id NOT NULL)");
            //TODO img path
            // TODO fb ID

            st.executeUpdate("INSERT INTO product(imagepath, price, category, price_bought, description, date_upload,online_link,price_sold,productCondition,months_used,user_id) VALUES ("+p.getImagePath()+","+p.getPrice()+","+ p.getPriceBought()+","+p.getDescription()+","+ p.getDateUploaded()+","+p.getDateSold()+","+p.getOnlineLink()+","+p.getPrice()+","+p.getCondition()+","+p.getMonths()+","+p.getUploadedBy()+")");
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
//        String myURL = "jdbc:mysql://localhost:3306/users";
//        int numRows=0;
//
//        try {
//            Class.forName(myDriver);
//            Connection conn = DriverManager.getConnection(myURL, "root", "1234");
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


    public static boolean checkIfUserExists(long id) throws ClassNotFoundException {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/users";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE IF NOT EXISTS product (id INT PRIMARY KEY, price DECIMAL(8,2),imagepath VARCHAR(100),category INT NOT NULL,price_bought DECIMAL(8,2) NOT NULL,description TEXT NOT NULL,date_upload TIMESTAMP,date_sold TIMESTAMP,online_link VARCHAR(255),price_sold DECIMAL(8,2),condition TINYINT NOTNULL,months_used INT,user_id NOT NULL)");
            ResultSet rs = st.executeQuery("SELECT COUNT(*) AS NOUSER FROM user WHERE id ="+ id +";");
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
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/users";
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

    public static ArrayList<Product> displayProducts(int pagenum) throws ClassNotFoundException {
        int start = pagenum*20;
        ArrayList<Product> displayList = new ArrayList<Product>();
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://lionmart.cvkcqiaoutkr.us-east-1.rds.amazonaws.com:3306/users";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "lionadmin", "lionlynx42");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM product LIMIT"+start+", 20 ORDER BY date_upload;");
            while(rs.next()){
                Product obj = new Product(rs.getLong("id"),rs.getLong("user_id"),rs.getString("imagepath"),rs.getFloat("price"),rs.getString("description"),rs.getDate("date_upload"),rs.getDate("date_sold"),  rs.getFloat("price_bought"),rs.getString("online_link"), rs.getFloat("price_sold"),rs.getInt("condition"),rs.getInt("months_used"),rs.getInt("category"));
                displayList.add(obj);
            }
            conn.close();
            return displayList;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


}