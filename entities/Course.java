package entities;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

public class Course extends Entity{
  
  private String title;
  private String stream;
  private String type;
  private LocalDate start_date;
  private LocalDate end_date;

  public Course() {}

  public Course(Map args) {
	Field[] fields = this.getClass().getDeclaredFields();
	for(Field field:fields){
	  try {
		field.set(this, args.get(field.getName()));
	  } catch (IllegalArgumentException | IllegalAccessException ex) {}
	}
  }
  
  public Course(Integer id, String title, String stream, String type, LocalDate start_date, LocalDate end_date) {
	super(id);
	this.title = title;
	this.stream = stream;
	this.type = type;
	this.start_date = start_date;
	this.end_date = end_date;
  }

  public String getTitle() {
	return title;
  }

  public String getStream() {
	return stream;
  }

  public String getType() {
	return type;
  }

  public void setTitle(String title) {
	this.title = title;
  }

  public void setStream(String stream) {
	this.stream = stream;
  }

  public void setType(String type) {
	this.type = type;
  }

  public void setStart_date(LocalDate start_date) {
	this.start_date = start_date;
  }

  public void setEnd_date(LocalDate end_date) {
	this.end_date = end_date;
  }

  public LocalDate getStart_date() {
	return start_date;
  }

  public LocalDate getEnd_date() {
	return end_date;
  }

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 67 * hash + Objects.hashCode(this.title);
	hash = 67 * hash + Objects.hashCode(this.stream);
	hash = 67 * hash + Objects.hashCode(this.type);
	hash = 67 * hash + Objects.hashCode(this.start_date);
	hash = 67 * hash + Objects.hashCode(this.end_date);
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
	final Course other = (Course) obj;
	if (!Objects.equals(this.title, other.title)) {
	  return false;
	}
	if (!Objects.equals(this.stream, other.stream)) {
	  return false;
	}
	if (!Objects.equals(this.type, other.type)) {
	  return false;
	}
	if (!Objects.equals(this.start_date, other.start_date)) {
	  return false;
	}
	if (!Objects.equals(this.end_date, other.end_date)) {
	  return false;
	}
	return true;
  }

  @Override
  public String toString() {
	return "Course{" + "title=" + title + ", stream=" + stream + ", type=" + type + ", start_date=" + start_date + ", end_date=" + end_date + '}';
  }
  
  
}
