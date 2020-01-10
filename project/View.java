package project;

import dao.AbstractDao;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class View extends AbstractDao {

  static boolean mapIsNotEmpty(Map map) {
	if(map == null) return false;
	return map.keySet().size() > (map.containsKey("names") ? 1 : 0);
  }
  
  private String view;

  public View() {
  }

  public View(String view) {
	this.view = view;
  }

  public String getView() {
	return view;
  }

  public void setView(String view) {
	this.view = view;
  }

  @Override
  public int hashCode() {
	int hash = 7;
	hash = 71 * hash + Objects.hashCode(this.view);
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
	final View other = (View) obj;
	if (!Objects.equals(this.view, other.view)) {
	  return false;
	}
	return true;
  }
  
  public boolean print(){
	return print(view, null, null);
  }
  
  public boolean print(String view){
	this.setView(view);
	return print();
  }
  
  public boolean print(String view, String fieldName, String value){
//	String query = "SELECT * FROM " + view;
	Map results = getMapFromRS(view, fieldName, value);
	if(results == null)
	  return false;
//	else if(results.keySet().size()<2){
//	  System.out.println("No match");
//	  return false;
//	}
	printResults(results);
	return true;
  }

  public Map getMapFromRS(String view, String fieldName, String value){
	String query;
	query = (view.split(" ").length == 1) ? "SELECT * FROM " + view : view;
	if(fieldName != null){
	  query = query.concat(" WHERE ").concat(fieldName).concat(" = '").concat(value).concat("'");
	}
	Connection conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	Map map = null;
	try {
	  st = conn.prepareStatement(query);
	  rs = st.executeQuery();
	  if(rs != null)
		map = convertRS(rs);
	} catch (SQLException e) {
	} finally {
	  closeConnections(rs, st);
	}
	return map;
  }
  
  private Map convertRS(ResultSet rs){
	Map<String, ArrayList<Object>> map = new HashMap();
	map.put("names", new ArrayList<>());
	try {
	  int length = rs.getMetaData().getColumnCount();
	  while(rs.next()){
		for(int i = 1; i <= length; i++){
		  String colName = rs.getMetaData().getColumnName(i);
		  if(!map.containsKey(colName)){
			map.put(colName, new ArrayList());
			map.get("names").add(colName);
		  }
		  map.get(colName).add(rs.getObject(i));
		}
	  }
	  
	} catch (SQLException e) {
	}
	return map;
  }
  
  public void printResults(Map<String, ArrayList<Object>> map){
	List<Object> colNames = new ArrayList<>(map.get("names"));
	List<Integer> spaces = new ArrayList<>();
	if(map.keySet().size()<2){
	  System.out.println("Nothing found.");
	  return;
	}
	int rows = map.get((String)colNames.get(0)).size();
	int cols = colNames.size();

	colNames.forEach(col -> spaces.add(col.toString().length()));
  
// Get maximum length per column
	for(int i = 0; i < rows; i++){
	  for(int y = 0; y < cols; y++){
		int length = map.get((String)colNames.get(y)).get(i).toString().length();
		if(spaces.get(y) < length) spaces.set(y, length);
	  }
	}
	

//	Print the first three rows
//	+-------+-------+----
//	| fname | lname | ...
//	+-------+-------+----
//	
	for(int i = 1; i <= 3; i++){
	  StringBuilder sb = new StringBuilder();
	  String columnDivider = i == 2 ? "|" : "+";
	  String placeholder = i == 2 ? " " : "-";
	  for(int y = 0; y < cols; y++){
		sb.append(columnDivider).append(placeholder);
		  if(i==2)sb.append(colNames.get(y));
		  for(int k = 0; k < spaces.get(y) - (i == 2 ? colNames.get(y).toString().length() : 0); k++)
			sb.append(placeholder);
		sb.append(placeholder);
	  }
	  sb.append(i == 2 ? "|" : "+");
	  System.out.println(sb.toString());
	}
	

//	Print the data
	for(int i = 0; i < rows; i++){
	  StringBuilder sb = new StringBuilder();
	  for(int y = 0; y < cols; y++){
		String item = map.get((String)colNames.get(y)).get(i).toString();
		  sb.append("| ");
		  sb.append(item);
		  for(int k = 0; k < spaces.get(y) - item.length(); k++)
			sb.append(" ");
		  sb.append(" ");
	  }
	  sb.append("|");
	  System.out.println(sb.toString());
	}
	
//  Print the bottom line of the table
	StringBuilder sb = new StringBuilder();
	for(int y = 0; y < cols; y++){
	  sb.append("+");
	  for(int k = 0; k < spaces.get(y) + 2; k++)
		sb.append("-");
	}
	sb.append("+");
	System.out.println(sb.toString());
  }
  
  public void studentsSubmitThisWeek(LocalDate monday, LocalDate friday){
	String query = "SELECT s.* "
		+ "FROM assignmentPerStudentPerCourse_full acs, student_full s "
		+ "WHERE acs.sid = s.ID "
		+ "AND asubdate BETWEEN ? AND ? "
		+ "GROUP BY ID "
		+ "ORDER BY ID;";
	
	Connection conn = getConnection();
	PreparedStatement st = null;
	ResultSet rs = null;
	Map map = null;
	try {
	  st = conn.prepareStatement(query);
	  st.setDate(1, Date.valueOf(monday));
	  st.setDate(2, Date.valueOf(friday));
	  rs = st.executeQuery();
	  if(rs != null)
		map = convertRS(rs);
	} catch (SQLException e) {
	} finally {
	  closeConnections(rs, st);
	}
	if(mapIsNotEmpty(map)){
	  printResults(map);
	} else System.out.println("Nobody needs to submit an assignment that week.");
  }

  @Override
  public String toString() {
	return "View{" + "view=" + view + '}';
  }
  
}
