package entities;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class Trainer extends Entity {
  
  private String firstName;
  private String lastName;
  private String subject;

  public Trainer() {}

  public Trainer(Map args){// throws IllegalArgumentException, IllegalAccessException{
	Field[] fields = this.getClass().getDeclaredFields();
	for(Field field:fields){
	  try {
		field.set(this, args.get(field.getName()));
	  } catch (IllegalArgumentException | IllegalAccessException ex) {}
	}
  }
  
  public Trainer(Integer id, String firstName, String lastName, String subject) {
	super(id);
	this.firstName = firstName;
	this.lastName = lastName;
	this.subject = subject;
  }

  public String getFirstName() {
	return firstName;
  }

  public String getLastName() {
	return lastName;
  }

  public void setFirstName(String firstName) {
	this.firstName = firstName;
  }

  public void setLastName(String lastName) {
	this.lastName = lastName;
  }

  public String getSubject() {
	return subject;
  }

  public void setSubject(String subject) {
	this.subject = subject;
  }

  @Override
  public int hashCode() {
	int hash = 3;
	hash = 29 * hash + Objects.hashCode(this.firstName);
	hash = 29 * hash + Objects.hashCode(this.lastName);
	hash = 29 * hash + Objects.hashCode(this.subject);
	return hash;
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj) {
	  return true;
	}
	if (obj == null) {
	  return false;
	}
	if (getClass() != obj.getClass()) {
	  return false;
	}
	final Trainer other = (Trainer) obj;
	if (!Objects.equals(this.firstName, other.firstName)) {
	  return false;
	}
	if (!Objects.equals(this.lastName, other.lastName)) {
	  return false;
	}
	if (!Objects.equals(this.subject, other.subject)) {
	  return false;
	}
	return true;
  }  

  @Override
  public String toString() {
	return "Trainer{" + "firstName=" + firstName + ", lastName=" + lastName + ", subject=" + subject + '}';
  }
  
}
