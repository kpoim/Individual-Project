package entities;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class Student extends Entity{
  
  private String firstName;
  private String lastName;
  private LocalDate dateOfBirth;
  private Double tuitionFees;

  public Student() {}
  
  public Student(Map args) {
	Field[] fields = this.getClass().getDeclaredFields();
	for(Field field:fields){
	  try {
		field.set(this, args.get(field.getName()));
	  } catch (IllegalArgumentException | IllegalAccessException ex) {}
	}
  }

  public Student(Integer id, String firstName, String lastName, LocalDate dateOfBirth, Double tuitionFees) {
	super(id);
	this.firstName = firstName;
	this.lastName = lastName;
	this.dateOfBirth = dateOfBirth;
	this.tuitionFees = tuitionFees;
  }

  public void setFirstName(String firstName) {
	this.firstName = firstName;
  }

  public void setLastName(String lastName) {
	this.lastName = lastName;
  }

  public void setDateOfBirth(LocalDate dateOfBirth) {
	this.dateOfBirth = dateOfBirth;
  }

  public void setTuitionFees(Double tuitionFees) {
	this.tuitionFees = tuitionFees;
  }

  public String getFirstName() {
	return firstName;
  }

  public String getLastName() {
	return lastName;
  }

  public LocalDate getDateOfBirth() {
	return dateOfBirth;
  }

  public Double getTuitionFees() {
	return tuitionFees;
  }

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 71 * hash + Objects.hashCode(this.firstName);
	hash = 71 * hash + Objects.hashCode(this.lastName);
	hash = 71 * hash + Objects.hashCode(this.dateOfBirth);
	hash = 71 * hash + (int) (Double.doubleToLongBits(this.tuitionFees) ^ (Double.doubleToLongBits(this.tuitionFees) >>> 32));
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
	final Student other = (Student) obj;
	if (Double.doubleToLongBits(this.tuitionFees) != Double.doubleToLongBits(other.tuitionFees)) {
	  return false;
	}
	if (!Objects.equals(this.firstName, other.firstName)) {
	  return false;
	}
	if (!Objects.equals(this.lastName, other.lastName)) {
	  return false;
	}
	if (!Objects.equals(this.dateOfBirth, other.dateOfBirth)) {
	  return false;
	}
	return true;
  }

  @Override
  public String toString() {
	return "Student{" + "firstName=" + firstName + ", lastName=" + lastName + ", dateOfBirth=" + dateOfBirth + ", tuitionFees=" + tuitionFees + '}';
  }  
  
}
