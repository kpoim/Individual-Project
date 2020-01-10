package dao;

import entities.Assignment;
import entities.AssignmentCourseStudent;
import entities.Course;
import entities.Student;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ACSDao extends AbstractDao {
  
  private Connection conn = null;
  
  private static final String UPDATEACS = "UPDATE assignmentcoursestudent SET acssubdate = ?, acsomark = ?, acstmark = ? WHERE sid = ? AND cid = ? AND aid = ?";
  private static final String FINDACSBYID = "SELECT acs.* FROM assignmentcoursestudent acs, assignments a WHERE acs.aid = a.aid AND acs.aid = ? AND acs.cid = ? AND acs.sid = ?";
  
  public boolean update(AssignmentCourseStudent acs){
	boolean updated = false;
	conn = getConnection();
	PreparedStatement st = null;
	try {
	  st = conn.prepareStatement(UPDATEACS);
	  st.setDate(1, Date.valueOf(acs.getSubDate()));
	  st.setDouble(2, acs.getOralMark());
	  st.setDouble(3, acs.getTotalMark());
	  st.setInt(4, acs.getStudent().getId());
	  st.setInt(5, acs.getCourse().getId());
	  st.setInt(6, acs.getAssignment().getId());
	  int rs = st.executeUpdate();
	  if(rs > 0) updated = true;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(st);
	}
	return updated;
  }
  
  
  public AssignmentCourseStudent findById(int assignmentID, int courseID, int studentID) {
	AssignmentCourseStudent acs = null;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(FINDACSBYID);
	  st.setInt(1, assignmentID);
	  st.setInt(2, courseID);
	  st.setInt(3, studentID);
	  rs = st.executeQuery();
	  Assignment assignment = null;
	  Course course = null;
	  Student student = null;
	  if(rs.next()){
		assignment = new AssignmentDao().findById(rs.getInt("aid"));
		course = new CourseDao().findById(rs.getInt("cid"));
		student = new StudentDao().findById(rs.getInt("sid"));
		acs = new AssignmentCourseStudent(
			rs.getDate("acssubdate").toLocalDate(),
			rs.getDouble("acsomark"),
			rs.getDouble("acstmark"),
			assignment,
			course,
			student
		);
	  }
	} catch (SQLException ex) {
	} finally {
	  closeConnections(rs, st);
	}
	return acs;
  }

  
}
