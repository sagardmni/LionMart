package controllers;

import play.mvc.*;
import play.db.jpa.*;
import views.html.*;
import play.data.FormFactory;
import javax.inject.Inject;
import java.sql.*;
import java.util.*;

public class Application extends Controller {

    @Inject
    FormFactory formFactory;

    public Result index() {
        return ok(index.render());
    }
    public Result login(){
        return ok(login.render());
    }

    public Result loginFail() {
        return ok(loginFail.render());
    }

    public Result loginSuccess() {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://localhost:3306/users";
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

        @Transactional
    public Result addPerson() throws ClassNotFoundException {
        Person person = formFactory.form(Person.class).bindFromRequest().get();
        boolean emailFormatCheckResult = checkEmailFormat(person.email);

        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://localhost:3306/users";
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "1234");
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO user_table (email, name, password)" +
                    "VALUES ('" + person.email+"','" + person.name+"','" + person.password + "')");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //JPA.em().persist(person);
        return redirect(routes.Application.index());
    }

    public boolean checkCredentials(String email, String pwd) {
        String myDriver = "com.mysql.jdbc.Driver";
        String myURL = "jdbc:mysql://localhost:3306/users";
        int numRows=0;

        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "1234");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM user_table WHERE email='"+ email +"' AND password='"+ pwd +"'");
            while(rs.next()) {
                numRows++;
            }
            System.out.println(numRows);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(numRows == 1){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkEmailFormat(String email) {
        if((email.indexOf('@') > 0 ) && (email.indexOf('.') < (email.length() - 1)) && (email.indexOf('.') > 0 )) {
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public Result loginSubmit() {
        ExistingPerson existingPerson = formFactory.form(ExistingPerson.class).bindFromRequest().get();
        boolean credentialsCheckResult = checkCredentials(existingPerson.email,existingPerson.password);

        if(credentialsCheckResult == true) {
            return redirect(routes.Application.loginSuccess());
        } else {
            return redirect(routes.Application.loginFail());
        }
    }
}