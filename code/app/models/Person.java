package models;
import javax.persistence.*;
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public String email;
    public String name;
    public String password;
}