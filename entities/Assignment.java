package entities;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class Assignment extends Entity {
  
  private String title;
  private String description;
  private LocalDate subDateTime;
  private Double oralMark;
  private Double totalMark;

  public Assignment() {}

  public Assignment(Map args) {
	Field[] fields = this.getClass().getDeclaredFields();
	for(Field field:fields){
	  try {
		field.set(this, args.get(field.getName()));
	  } catch (IllegalArgumentException | IllegalAccessException ex) {}
	}
  }

  public Assignment(Integer id, String title, String description, LocalDate subDateTime, Double oralMark, Double totalMark) {
	super(id);
	this.title = title;
	this.description = description;
	this.subDateTime = subDateTime;
	this.oralMark = oralMark;
	this.totalMark = totalMark;
  }

  public String getTitle() {
	return title;
  }

  public void setTitle(String title) {
	this.title = title;
  }

  public void setDescription(String description) {
	this.description = description;
  }

  public String getDescription() {
	return description;
  }

  public LocalDate getSubDateTime() {
	return subDateTime;
  }

  public Double getOralMark() {
	return oralMark;
  }

  public Double getTotalMark() {
	return totalMark;
  }

  public void setSubDateTime(LocalDate subDateTime) {
	this.subDateTime = subDateTime;
  }

  public void setOralMark(Double oralMark) {
	this.oralMark = oralMark;
  }

  public void setTotalMark(Double totalMark) {
	this.totalMark = totalMark;
  }

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 89 * hash + Objects.hashCode(this.title);
	hash = 89 * hash + Objects.hashCode(this.description);
	hash = 89 * hash + Objects.hashCode(this.subDateTime);
	hash = 89 * hash + Objects.hashCode(this.oralMark);
	hash = 89 * hash + Objects.hashCode(this.totalMark);
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
	final Assignment other = (Assignment) obj;
	if (!Objects.equals(this.title, other.title)) {
	  return false;
	}
	if (!Objects.equals(this.description, other.description)) {
	  return false;
	}
	if (!Objects.equals(this.subDateTime, other.subDateTime)) {
	  return false;
	}
	if (!Objects.equals(this.oralMark, other.oralMark)) {
	  return false;
	}
	if (!Objects.equals(this.totalMark, other.totalMark)) {
	  return false;
	}
	return true;
  }

  @Override
  public String toString() {
	return "Assignment{" + "title=" + title + ", description=" + description + ", subDateTime=" + subDateTime + ", oralMark=" + oralMark + ", totalMark=" + totalMark + '}';
  }
  
}
