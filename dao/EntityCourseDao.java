package dao;

import entities.Course;
import entities.Entity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntityCourseDao extends AbstractDao{
  
  private Connection conn = null;
  private String entity;
  
  private String insertQuery = "INSERT INTO _placeholder1_course(_placeholder2_id, cid) VALUES(?,?)";

  public EntityCourseDao() {
  }

  public EntityCourseDao(String entity) {
	this.entity = entity;
	this.insertQuery = this.insertQuery.replace("_placeholder1_", entity);
	this.insertQuery = this.insertQuery.replace("_placeholder2_", entity.substring(0,1));
  }

  public String getEntity() {
	return entity;
  }
  
  public boolean create(Entity e, Course c) {
	if(entity == null) return false;
	boolean created = false;
	conn = getConnection();
	PreparedStatement st = null;
	try {
	  st = conn.prepareStatement(insertQuery);
	  st.setInt(1, e.getId());
	  st.setInt(2, c.getId());
	  int rs = st.executeUpdate();
	  if(rs > 0) created = true;
	} catch (SQLException ex) {
	} finally{
	  closeConnections(st);
	}
	return created;
  }  
  
}
