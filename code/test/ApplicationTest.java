import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import controllers.Application;
import play.db.Database;
import play.db.Databases;

import com.google.common.collect.ImmutableMap;

import java.sql.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;

/**
 *
 * Simple (JUnit) tests that can call all parts of a play app.
 * If you are interested in mocking a whole application, see the wiki for more details.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApplicationTest {
    String myDriver;
    String myURL;
    String name1,name2;

    @Test
    public void t2RetrieveCheck() {
        myDriver = "com.mysql.jdbc.Driver";
        myURL = "jdbc:mysql://localhost/mydatabase";
        ResultSet rs = null;
        try {

            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "");
            Statement st = conn.createStatement();
            st.executeUpdate("CREATE TABLE  user_table (email VARCHAR(50) PRIMARY KEY, name VARCHAR (50), password VARCHAR(12))");
            st.executeUpdate("INSERT INTO user_table (email, name, password)" +
                    "VALUES (\'ak@gm.com\', \'akshay kumar\',\'W&rY69\')");
            rs = st.executeQuery("SELECT name FROM user_table WHERE email=\'ak@gm.com\'");
            while(rs.next()) {
                name1 = rs.getString("name");
                System.out.println(rs.toString());
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals("akshay kumar", name1);
    }

    @Test
    public void t3DeleteCheck(){
        myDriver = "com.mysql.jdbc.Driver";
        myURL = "jdbc:mysql://localhost/mydatabase";
        ResultSet rs = null;
        try {
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myURL, "root", "");
            Statement st = conn.createStatement();
            st.executeUpdate("DELETE FROM user_table WHERE email=\'ak@gm.com\'");
            rs = st.executeQuery("SELECT name FROM user_table WHERE email=\'ak@gm.com\'");
            while(rs.next()) {
                name2 = rs.getString("name");
                System.out.println(rs.toString());
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(name2, null);
    }

    @Test
    public void t1EmailFormatCheck(){
        Application a = new Application();
        assertEquals(true,a.checkEmailFormat("abcd@xyz.com"));
        assertEquals(false,a.checkEmailFormat("abcd@xyz"));
        assertEquals(false,a.checkEmailFormat("@xyz"));
        assertEquals(false,a.checkEmailFormat("ab@xyz."));
    }
}