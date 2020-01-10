package entities;

import java.time.LocalDate;
import java.util.Objects;

public class AssignmentCourseStudent extends Entity{
  
  private LocalDate subDate;
  private Double oralMark;
  private Double totalMark;
  private Assignment assignment;
  private Course course;
  private Student student;

  public AssignmentCourseStudent() {
  }

  public AssignmentCourseStudent(LocalDate subDate, Double oralMark, Double totalMark, Assignment assignment, Course course, Student student) {
	this.subDate = subDate;
	this.oralMark = oralMark;
	this.totalMark = totalMark;
	this.assignment = assignment;
	this.course = course;
	this.student = student;
  }

  public AssignmentCourseStudent(LocalDate subDate, Double oralMark, Double totalMark, Assignment a, Course c, Student student, Integer ID) {
	super(ID);
	this.subDate = subDate;
	this.oralMark = oralMark;
	this.totalMark = totalMark;
	this.assignment = a;
	this.course = c;
	this.student = student;
  }

  public LocalDate getSubDate() {
	return subDate;
  }

  public Double getOralMark() {
	return oralMark;
  }

  public Double getTotalMark() {
	return totalMark;
  }

  public Assignment getAssignment() {
	return assignment;
  }

  public Course getCourse() {
	return course;
  }

  public Student getStudent() {
	return student;
  }

  public void setSubDate(LocalDate subDate) {
	this.subDate = subDate;
  }

  public void setOralMark(Double oralMark) {
	this.oralMark = oralMark;
  }

  public void setTotalMark(Double totalMark) {
	this.totalMark = totalMark;
  }

  @Override
  public int hashCode() {
	int hash = 5;
	hash = 79 * hash + Objects.hashCode(this.assignment);
	hash = 79 * hash + Objects.hashCode(this.course);
	hash = 79 * hash + Objects.hashCode(this.student);
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
	final AssignmentCourseStudent other = (AssignmentCourseStudent) obj;
	if (!Objects.equals(this.assignment, other.assignment)) {
	  return false;
	}
	if (!Objects.equals(this.course, other.course)) {
	  return false;
	}
	if (!Objects.equals(this.student, other.student)) {
	  return false;
	}
	return true;
  }

  @Override
  public String toString() {
	return "AssignmentCourseStudent{" + "subDate=" + subDate + ", oralMark=" + oralMark + ", totalMark=" + totalMark + ", AssignmentID=" + assignment.getId() + ", CourseID=" + course.getId() + ", StudentID=" + student.getId() + '}';
  }
  
}
