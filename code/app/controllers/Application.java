package controllers;

import models.ExistingPerson;
import play.mvc.*;
import play.db.jpa.*;
import views.html.*;
import models.Person;
import play.data.FormFactory;
import javax.inject.Inject;
import java.sql.*;
import java.util.List;

import static play.libs.Json.*;

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
        return ok(loginSuccess.render());
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