package dao;

import entities.Student;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDao extends AbstractDao implements Dao<Student>{
  
  private Connection conn = null;
  
  private static final String INSERTSTUDENT = "INSERT INTO students(sfname, slname, sdob, stfees) VALUES(?,?,?,?)";
  private static final String FINDSTUDENTBYID = "SELECT * FROM students WHERE sid = ?";
  private static final String FINDALL = "SELECT * FROM student_full";
  private static final String CHECKIFEXISTS = "SELECT count(ID) FROM student_full "
	  + "WHERE first_name = ? AND last_name = ? AND date_of_birth = ? AND tuition_fees = ?";
  
  @Override
  public boolean create(Student e) {
	boolean created = false;
	conn = getConnection();
	PreparedStatement st = null;
	try {
	  st = conn.prepareStatement(INSERTSTUDENT);
	  st.setString(1, e.getFirstName());
	  st.setString(2, e.getLastName());
	  st.setDate(3, Date.valueOf(e.getDateOfBirth()));
	  st.setDouble(4, e.getTuitionFees());
	  int rs = st.executeUpdate();
	  if(rs > 0) created = true;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(st);
	}
	return created;
  }

  @Override
  public Student findById(int id) {
	Student student = null;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(FINDSTUDENTBYID);
	  st.setInt(1, id);
	  rs = st.executeQuery();
	  if(rs.next())
		student = new Student(
			rs.getInt("sid"),
			rs.getString("sfname"),
			rs.getString("slname"),
			rs.getDate("sdob").toLocalDate(),
			rs.getDouble("stfees")
		);
	} catch (SQLException ex) {
	} finally {
	  closeConnections(rs, st);
	}
	return student;
  }

  public List<Student> findAall() {
	List<Student> list = new ArrayList<>();
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(FINDALL);
	  rs = st.executeQuery();
	  while(rs.next()){
		list.add(new Student(
			rs.getInt("ID"),
			rs.getString("first_name"),
			rs.getString("last_name"),
			rs.getDate("date_of_birth").toLocalDate(),
			rs.getDouble("tuition_fees")
		));
	  }
	} catch (SQLException ex) {
	} finally {
	  closeConnections(rs, st);
	}
	return list;
  }

  @Override
  public boolean checkIfExists(Student e) {
	boolean exists = false;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(CHECKIFEXISTS);
	  st.setString(1, e.getFirstName());
	  st.setString(2, e.getLastName());
	  st.setDate(3, Date.valueOf(e.getDateOfBirth()));
	  st.setDouble(4, e.getTuitionFees());
	  rs = st.executeQuery();
	  if(rs.next())
		exists = rs.getInt(1)>0;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(rs, st);
	}
	return exists;
  }

}
