package dao;

import entities.Trainer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainerDao extends AbstractDao implements Dao<Trainer>{
  
  private Connection conn = null;
  
  private static final String INSERTTRAINER = "INSERT INTO trainers(tfname, tlname, tsubj) VALUES(?,?,?)";
  private static final String FINDTRAINERBYID = "SELECT * FROM trainers WHERE tid = ?";
  private static final String FINDALL = "SELECT * FROM trainer_full";
  private static final String CHECKIFEXISTS = "SELECT count(ID) FROM trainer_full "
	  + "WHERE first_name = ? AND last_name = ? AND subject = ?";

  @Override
  public boolean create(Trainer e) {
	boolean created = false;
	conn = getConnection();
	PreparedStatement st = null;
	try {
	  st = conn.prepareStatement(INSERTTRAINER);
	  st.setString(1, e.getFirstName());
	  st.setString(2, e.getLastName());
	  st.setString(3, e.getSubject());
	  int rs = st.executeUpdate();
	  if(rs > 0) created = true;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(st);
	}
	return created;
  }

  @Override
  public boolean checkIfExists(Trainer e) {
	boolean exists = false;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(CHECKIFEXISTS);
	  st.setString(1, e.getFirstName());
	  st.setString(2, e.getLastName());
	  st.setString(3, e.getSubject());
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
  public Trainer findById(int id) {
	Trainer trainer = null;
	conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement(FINDTRAINERBYID);
	  st.setInt(1, id);
	  rs = st.executeQuery();
		while (rs.next()) {
		  for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
			System.out.print(" " + rs.getMetaData().getColumnName(i) + "=" + rs.getObject(i));
		  }
		  System.out.println();
		}
	  if(rs.next())
		trainer = new Trainer(
			rs.getInt("tid"),
			rs.getString("tfname"),
			rs.getString("tlname"),
			rs.getString("tsubj")
		);
	} catch (SQLException ex) {
	} finally {
	  closeConnections(rs, st);
	}
	return trainer;
  }

  
}
