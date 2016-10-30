package models;

/**
 * Created by akshay on 10/29/2016.
 */
public class User {

    private long fbId;
    private String firstName;
    private String lastName;
    private String email;
    private boolean type;

    public User() {
    }

    public User(long fbId, String firstName, String lastName, String email, boolean type) {
        this.fbId = fbId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.type = type;
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

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }
}
