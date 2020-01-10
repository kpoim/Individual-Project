package project;

import dao.AbstractDao;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Utilities extends AbstractDao {

  private static java.util.Scanner scanner = getScanner();
  private static HashMap<String, HashSet<String>> views = new HashMap<>();

  /**
   * This method accepts input from the keyboard, trims whitespaces,<br>
   * replaces multiple spaces with one, splits it matching a whitespace,<br>
   * places the strings in a linked list and returns it.<br>
   *
   * @return A linked list containing the words the user entered.<br>
   */
  public static LinkedList<String> getInput() {
	LinkedList<String> inputList = new LinkedList();
	String[] arr = scanner.nextLine().trim().replaceAll("\\s{2,}", " ").split(" ");
	for (String item : arr) {
	  inputList.add(item.toLowerCase());
	}
	while (inputList.isEmpty()) {
	  inputList = getInput();
	}
	return inputList;
  }

  /**
   * This method gets an integer from the the keyboard and if it is within the
   * given<br>
   * range it is returned.<br>
   *
   * @param begin The inclusive lower end of the range.<br>
   * @param end The inclusive upper end of the value.<br>
   * @param message The String that will be displayed when the method starts
   * executing.<br>
   * Input will be accepted in the same line the message<br>
   * was entered. Must add "\n" at the end of the message to change line.<br>
   * @return An integer within the given range.<br>
   */
  public static Integer getInput(Integer begin, Integer end, String message) {
	if (message != null) {
	  System.out.print(message);
	  Utilities.expectInput();
	}
	do {
	  while (!scanner.hasNextInt()) {
		scanner.nextLine();
		System.out.println("Invalid input.\nType a value between " + begin + " - " + end);
		Utilities.expectInput();
	  }
	  int temp = scanner.nextInt();
	  scanner.nextLine();
	  if (temp >= begin && temp <= end) {
		return temp;
	  }
	  System.out.println("Invalid input.\nType a value between " + begin + " - " + end);
	  Utilities.expectInput();
	} while (true);
  }

  /**
   * This method gets a string from the keyboard, checks if is contained<br>
   * in the set, if the set is not null and returns it lowercased.<br>
   *
   * @param stringPool The set containing all the valid values.<br>
   * Null to accept any value.<br>
   * @param acceptEmpty True to accept as valid an empty string, false otherwise.<br>
   * @param message The String that will be displayed when the method starts
   * executing.<br>
   * @return A string contained in the given set, otherwise any string given.<br>
   */
  public static String getInput(List<String> stringPool, boolean acceptEmpty, String message) {
	String input;
	if (message != null) {
	  System.out.print(message);
	}
	do {
	  Utilities.expectInput();
	  input = scanner.nextLine();
	  if (stringPool == null || stringPool.isEmpty() || stringPool.contains(input.toLowerCase()) || (acceptEmpty && input.isEmpty())) {
		return input.toLowerCase();
	  }
	  System.out.println("Invalid input.");
	} while (true);
  }

  /**
   *
   * @return The scanner created in the scanner Class.<br>
   */
  public static java.util.Scanner getScanner() {
	return Scanner.getScanner();
  }

  /**
   * Prints all the items in a list if the list is not empty.<br>
   *
   * @param list
   */
  public static void printArrayList(List list) {
	list = (ArrayList) list;
	if (list.isEmpty()) {
	  System.out.println("ArrayList is empty.");
	  return;
	}
	for (Object item : list) {
	  System.out.println(item);
	}
  }

  /**
   * Accepts a string and returns it as a single camelCased word.<br>
   */
  public static String getCamelCaseFromText(String str) {
	List<String> list = new ArrayList<>(Arrays.asList(str.split(" ")));
	for (int i = 1; i < list.size(); i++) {
	  list.set(i, getBackWithFirstLetterCapital(list.get(i)));
	}
	return String.join("", list);
  }

  /**
   * Accepts a string and returns it with the first letter<br>
   * of the first word capitalized.<br>
   */
  private static String getBackWithFirstLetterCapital(String str) {
	return str.substring(0, 1).toUpperCase().concat(str.substring(1));
  }

  /**
   * Accepts a camelCased word, splits it to single words and returns them
   * lowercased.<br>
   */
  public static String getTextFromCamelCase(String str) {
	List<String> list = new ArrayList<>(Arrays.asList(str.split("")));
	for (int i = 1; i < list.size(); i++) {
	  if (!list.get(i).equals(list.get(i).toLowerCase())) {
		list.set(i, list.get(i).toLowerCase());
		list.add(i, " ");
		i++;
	  }
	}
	return String.join("", list);
  }

  /**
   * Accepts an input from the keyboard and converts it to boolean.<br>
   *
   * @return true or false<br>
   */
  public static boolean convertYesOrNoToBoolean() {
	String response;
	do {
	  response = scanner.nextLine();
	  if (response.toLowerCase().equals("yes")) {
		return true;
	  } else if (response.toLowerCase().equals("no")) {
		return false;
	  }
	  System.out.println("A simple yes or no would do.");
	  Utilities.expectInput();
	} while (true);
  }

  /**
   * Used to indicate an input from a user is expected.<br>
   */
  public static void expectInput() {
	System.out.print("--> ");
  }

  /**
   * This methid accepts a date and returns the monday of that week.<br>
   *
   * @param date A LocaLDate object.<br>
   * @return A LocalDate object that points to a Monday.<br>
   */
  public static LocalDate getMonday(LocalDate date) {
	return date.minusDays(date.getDayOfWeek().getValue() - 1);
  }

  /**
   * This methid accepts a date and returns the Friday of that week.<br>
   *
   * @param date A LocaLDate object.<br>
   * @return A LocalDate object that points to a Friday.<br>
   */
  public static LocalDate getFriday(LocalDate date) {
	return date.plusDays(5 - date.getDayOfWeek().getValue());
  }

  /**
   * Checks if a given date is within a range.
   *
   * @param date The date to check.<br>
   * @param monday The inclusive lower end of the range.<br>
   * @param friday The inclusive upper end of the range.<br>
   * @return true or false<br>
   */
  public static boolean isInDateRange(LocalDate date, LocalDate monday, LocalDate friday) {
	return (date.isAfter(monday) && date.isBefore(friday))
		|| date.isEqual(monday) || date.isEqual(friday);
  }

  public static HashMap<String, HashSet<String>> getViews() {
	if (!views.isEmpty()) {
	  return views;
	}
	Utilities instance = new Utilities();
	Connection conn = instance.getConnection();
	if (conn == null) {
	  return null;
	}
	PreparedStatement st = null;
	ResultSet rs = null;
	try {
	  st = conn.prepareStatement("SHOW FULL TABLES WHERE TABLE_TYPE LIKE 'VIEW'");
	  rs = st.executeQuery();
	  while (rs.next()) {
		String[] view = rs.getString("Tables_in_privateschool").split("_");
		if (!views.keySet().contains(view[0])) {
		  views.put(view[0], new HashSet());
		}
		views.get(view[0]).add(view[1]);
	  }
	} catch (SQLException e) {
	}
	return views;
  }

  static String findViewInList(List<String> list) {
	Set<String> tempSet3 = new HashSet<>();
	list.forEach(item -> Arrays.asList(item.split("per"))
		.forEach(listItem -> tempSet3.add(listItem)));

	Set<String> tempSet2 = tempSet3
		.stream()
		.filter(item -> !item.isEmpty())
		.map(item -> item.endsWith("s")
		? item.substring(0, item.length() - 1)
		: item)
		.collect(Collectors.toSet());
	Set<String> tempSet;
	String key = null;

	for (String item : views.keySet()) {
	  tempSet = new HashSet<>();
	  tempSet.addAll(Arrays.asList(item.split("per")));
	  if (tempSet.size() == tempSet2.size() && tempSet.removeAll(tempSet2) && tempSet.size() == 0) {
		key = item;
		break;
	  } else {
		key = null;
	  }
	}
	return key;
  }

  static String getViewVersion(String view) {
	return "_".concat(getInput(new ArrayList<String>(views.get(view)), false, "Simple, extended or full version?\n"));
  }

  public static Constructor<?> getConstructorWithMostParameteres(Class<?> className) {
	Constructor<?>[] constructors = className.getDeclaredConstructors();
	if (constructors.length > 0) {
	  Constructor<?> constructor = constructors[0];
	  for (Constructor c : constructors) {
		if (c.getParameterCount() > constructor.getParameterCount()) {
		  constructor = c;
		}
	  }
	  return constructor;
	}
	return null;
  }

  public static Constructor<?> getStringConstructor(Class<?> className) {
	Constructor<?>[] argsConstructors = className.getDeclaredConstructors();
	for (Constructor c : argsConstructors) {
	  if (c.getParameterCount() == 1 && c.getParameterTypes()[0].getSimpleName().toLowerCase().equals("string")) {
		return c;
	  }
	}
	return null;
  }

}

class Scanner {

  private static java.util.Scanner scanner;

  private Scanner() {
  }

  public static java.util.Scanner getScanner() {
	if (scanner == null) {
	  scanner = new java.util.Scanner(System.in);
	}
	return scanner;
  }
}
