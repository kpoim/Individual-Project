package dao;

import entities.Assignment;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignmentDao extends AbstractDao implements Dao<Assignment> {
  
  private Connection conn = null;
  
  private static final String INSERTASSIGNMENT = "INSERT INTO assignments(atitle, adesc, asubdate, aomark, atmark) VALUES(?,?,?,?,?)";
  private static final String FINDASSIGNMENTBYID = "SELECT * FROM assignments WHERE aid = ?";
  private static final String FINDALL = "SELECT * FROM assignment_full";
  private static final String CHECKIFEXISTS = "SELECT count(ID) FROM assignment_full "
	  + "WHERE title = ? AND description = ? AND submission_date = ? AND oral_mark = ? AND total_mark = ?";

  @Override
  public boolean create(Assignment e) {
	boolean created = false;
	conn = getConnection();
	PreparedStatement st = null;
	try {
	  st = conn.prepareStatement(INSERTASSIGNMENT);
	  st.setString(1, e.getTitle());
	  st.setString(2, e.getDescription());
	  st.setDate(3, Date.valueOf(e.getSubDateTime()));
	  st.setDouble(4, e.getOralMark());
	  st.setDouble(5, e.getTotalMark());
	  int rs = st.executeUpdate();
	  if(rs > 0) created = true;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(st);
	}
	return created;
  }

  @Override
  public boolean checkIfExists(Assignment e) {
	boolean exists = false;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(CHECKIFEXISTS);
	  st.setString(1, e.getTitle());
	  st.setString(2, e.getDescription());
	  st.setDate(3, Date.valueOf(e.getSubDateTime()));
	  st.setDouble(4, e.getOralMark());
	  st.setDouble(5, e.getTotalMark());
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
  public Assignment findById(int id) {
	Assignment assignment = null;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(FINDASSIGNMENTBYID);
	  st.setInt(1, id);
	  rs = st.executeQuery();
	  if(rs.next())
		assignment = new Assignment(
			rs.getInt("aid"),
			rs.getString("atitle"),
			rs.getString("adesc"),
			rs.getDate("asubdate").toLocalDate(),
			rs.getDouble("aomark"),
			rs.getDouble("atmark")
		);
	} catch (SQLException ex) {
	} finally {
	  closeConnections(rs, st);
	}
	return assignment;
  }
  
  
  
}
