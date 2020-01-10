package project;

import java.util.Scanner;
	/**
	 * <b>NOTE-1:</b> The javadocs are not up to date, they might be inaccurate.<br><br>
	 * <b>NOTE-2:</b> In order for the program to run the database must have certain
	 * views created.<br>
	 * These view definitions are in the privateSchoolDBOutput.sql file.
	 */

public class MainClass {
  
  public static void main(String[] args) {
	try (Scanner scanner = Utilities.getScanner()) {
	  Data.createDataStructures();
	  SyntheticData.populate();
	  if(Utilities.getViews() == null) return;
	  
	  ConsoleApp app = new ConsoleApp(
		  Data.getActions(),
		  Data.getEntities(),
		  Data.getClassEntities(),
		  Data.getDaos()
	  );
	  
	  app.init();
	}
  }
  
}
