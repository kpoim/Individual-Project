package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AbstractDao {

  private final String URL = "jdbc:mysql://localhost:3306/privateschool?serverTimezone=EET";
  private final String USER = "root";
  private final String PASS = "1234";

  private Connection conn;

  protected Connection getConnection() {
	try {
	  conn = DriverManager.getConnection(URL, USER, PASS);
	} catch (SQLException ex) {
	  switch (ex.getErrorCode()) {
	  	case 0:
		  System.out.println("Could not connect to the database.");
		  break;
	  	case 1049:
		  System.out.println("Unknown database.");
		  break;
	  	case 1045:
		  System.out.println("Access denied.");
		  break;
	  	default:
		  break;
	  }
	}
	return conn;
  }

  protected void closeConnections(ResultSet rs, Statement stmt) {
	try {
	  rs.close();
	  stmt.close();
	  conn.close();
	} catch (SQLException | NullPointerException ex) {}
	
  }

  protected void closeConnections(Statement st) {
	try {
	  st.close();
	  conn.close();
	} catch (SQLException | NullPointerException ex) {
	}
  }
  
}
