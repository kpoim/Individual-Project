package dao;

import entities.Course;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CourseDao extends AbstractDao implements Dao<Course> {
  
  private Connection conn = null;
  
  private static final String INSERTCOURSE = "INSERT INTO courses(ctitle, cstream, ctype, csdate, cedate) VALUES(?,?,?,?,?)";
  private static final String FINDCOURSEBYID = "SELECT * FROM courses WHERE cid = ?";
  private static final String FINDALL = "SELECT * FROM course_full";
  private static final String CHECKIFEXISTS = "SELECT count(ID) FROM course_full "
	  + "WHERE title = ? AND stream = ? AND type = ? AND start_date = ? AND end_date = ?";

  @Override
  public boolean create(Course e) {
	boolean created = false;
	conn = getConnection();
	PreparedStatement st = null;
	try {
	  st = conn.prepareStatement(INSERTCOURSE);
	  st.setString(1, e.getTitle());
	  st.setString(2, e.getStream());
	  st.setString(3, e.getType());
	  st.setDate(4, Date.valueOf(e.getStart_date()));
	  st.setDate(5, Date.valueOf(e.getEnd_date()));
	  int rs = st.executeUpdate();
	  if(rs > 0) created = true;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(st);
	}
	return created;
  }

  @Override
  public boolean checkIfExists(Course e) {
	boolean exists = false;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(CHECKIFEXISTS);
	  st.setString(1, e.getTitle());
	  st.setString(2, e.getStream());
	  st.setString(3, e.getType());
	  st.setDate(4, Date.valueOf(e.getStart_date()));
	  st.setDate(5, Date.valueOf(e.getEnd_date()));
	  rs = st.executeQuery();
	  if(rs.next())
		exists = rs.getInt(1)>0;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(rs, st);
	}
	return exists;
  }

  @Override
  public Course findById(int id) {
	Course course = null;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(FINDCOURSEBYID);
	  st.setInt(1, id);
	  rs = st.executeQuery();
	  if(rs.next())
		course = new Course(
			rs.getInt("cid"),
			rs.getString("ctitle"),
			rs.getString("cstream"),
			rs.getString("ctype"),
			rs.getDate("csdate").toLocalDate(),
			rs.getDate("cedate").toLocalDate()
		);
	} catch (SQLException ex) {
	} finally {
	  closeConnections(rs, st);
	}
	return course;
  }

  public List<Course> findAall() {
	List<Course> list = new ArrayList<>();
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(FINDALL);
	  rs = st.executeQuery();
	  while(rs.next()){
		list.add(new Course(
			rs.getInt("ID"),
			rs.getString("title"),
			rs.getString("stream"),
			rs.getString("type"),
			rs.getDate("start_date").toLocalDate(),
			rs.getDate("end_date").toLocalDate()
		));
	  }
	} catch (SQLException ex) {
	} finally {
	  closeConnections(rs, st);
	}
	return list;
  }
  
  
  
}
