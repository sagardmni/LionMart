package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ExistingPerson {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public String email;
  public String password;
}
