package project;

import dao.ACSDao;
import dao.Dao;
import dao.EntityCourseDao;
import entities.Assignment;
import entities.AssignmentCourseStudent;
import entities.Course;
import entities.Entity;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsoleApp {

  private java.util.Scanner scanner = Utilities.getScanner();
  private Set<String> actions;
  private HashMap<String, ArrayList<Object>> entities;
  private Map<String, Class<?>> classEntities;
  private Map<Course, Map<String, List<Object>>> course;
  private HashMap<String, Class<?>> daos;
  private View view;

  public ConsoleApp() {
  }

  public ConsoleApp(HashSet<String> actions, HashMap<String, ArrayList<Object>> entities, HashMap<String, Class<?>> classEntities, HashMap<String, Class<?>> daos) {
	System.out.println("Welcome to the private school app.\n");
	this.course = new HashMap<>();
	this.actions = actions;
	this.entities = entities;
	this.classEntities = classEntities;
	this.view = new View();
	this.daos = daos;
  }

  /**
   * Where it all begins.<br>Gets input and passes it down for analysis.<br>
   */
  public void init() {
	LinkedList<String> inputList = new LinkedList<>();
	System.out.println("What would you like to do?");
	Utilities.expectInput();
	while (analyzeInput(inputList)) {
	  inputList = Utilities.getInput();
	}
  }

  /**
   * Accepts a list which contains the words the user entered.<br>
   * If the list contains the word "exit", the method returns false and the program
   * stops running.<br>
   * It extracts the first value of the list and checks if it is a valid action.<br>
   * If it is invalid returns to get a new input.<br>
   * If it is valid pass on the value and the rest of the list.<br>
   *
   * @param list The input the user entered split by ' '.<br>
   * @return true to continue executing, false to exit the program.<br>
   */
  private boolean analyzeInput(LinkedList<String> list) {
	if (list.isEmpty()) {
	  return true;
	} else if (list.contains("exit")) {
	  return false;
	} else {
	  String input = list.removeFirst();
	  if (this.actions.contains(input)) {
		performAction(input, list);
		System.out.println("\nWhat would you like to do next?");
	  } else {
		System.out.println("I didn't get that.");
		System.out.println("Pick one of the following:");
		System.out.println("  add: create new entities");
		System.out.println("  print: print entities and other custom views");
		System.out.println("  assign: link entities to courses");
		System.out.println("  grade: add grades and submission dates to the assignments of the students");
		System.out.println("  clear: clear the screen");
		System.out.println("  exit: say goodbye");
		System.out.println("So what would you like to do?");
	  }
	  Utilities.expectInput();
	  return true;
	}
  }

  /**
   * Accepts the validated action string and the rest of the input as a list.<br>
   * Creates a string with an entity name and passes it to the method<br>
   * the input represents.<br>
   *
   * @param action The validated action name.<br>
   * @param list The rest of the input.<br>
   * @return true or false. Return value is not used at the moment.<br>
   */
  private boolean performAction(String action, LinkedList<String> list) {
	String entity = pickEntity(list, action);
	switch (action) {
	  case "add":
		add(entity, true);
		break;
	  case "refactor":
		refactor(list);
		break;
	  case "assign":
		assign(entity, null);
		break;
	  case "print":
		print(entity, list);
		break;
	  case "grade":
		grade();
		break;
	  case "clear":
		System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"
			+ "\n\n\n\n\n\n\n\n\n\n\n\n\n");
		;
		break;
	}
	return true;
  }

  /**
   * Accepts the input list and the action string which needs the entity we are
   * looking for.<br>
   * Checks the first item of the list if it is a valid entity name.<br>
   * If it is valid it returns, otherwise it asks for input until it gets a valid one
   * and then returns it.<br>
   *
   * @param list What has remained of the input. Input without the action.
   * @param action The validated action string.
   * @return A valid entity name.
   */
  private String pickEntity(LinkedList<String> list, String action) {
	String view = null;
	if (list != null) {
	  view = Utilities.findViewInList(list);
	}
	if (action.equals("print") && view != null && view.length() > 10) {
	  return "";
	}
	if (action.equals("clear") || action.contains("grade")) {
	  return "";
	}
	String str = null;
	if (list != null && !list.isEmpty()) {
	  str = checkIfItIsSimilar(list.getFirst(), action.equals("assign"));
	}
	if (str != null) {
	  return str;
	}
	do {
	  System.out.println(action + " what?");
	  System.out.println((action.equals("assign") ? "" : "Course, ").concat("Student, Assignment or Trainer?"));
	  str = checkIfItIsSimilar(Utilities.getInput(null, false, null), action.equals("assign"));
	} while (str == null);
	return str;
  }

  /**
   * Accept a string and a boolean value that is true when the caller is the "assign"
   * action.<br>
   * It checks the value of the string to similar values and returns the entity
   * string name.<br>
   *
   * @param str A string to check if it is similar to an entity name.<br>
   * @param callingMethodIsAssign That.
   * @return The entity name if it finds one or null.
   */
  private String checkIfItIsSimilar(String str, boolean callingMethodIsAssign) {
	switch (str) {
	  case "s":
	  case "st":
	  case "student":
	  case "students":
		return "student";
	  case "t":
	  case "tr":
	  case "trainer":
	  case "trainers":
		return "trainer";
	  case "course":
	  case "courses": {
		if (callingMethodIsAssign) {
		  System.out.println("Can't assign course to course");
		}
		return callingMethodIsAssign ? null : "course";
	  }
	  case "a":
	  case "as":
	  case "assignment":
	  case "assignments":
		return "assignment";
	  default:
		return null;
	}
  }

  /**
   * The method at first asks the user if he wants to enter the data manually.<br>
   * Then it creates a Contructor object for the class the entity represents.<br>
   * Creates new instances of the entity as many as the user requested if he <br>
   * opted for synthetic data or one for manual input and adds them to the<br>
   * database using the appropriate DAO object.
   *
   * @param entity The entity name.<br>
   * @param first Is it the first time in a row we try to add an entity?<br>
   * @return true or false. Return value not used at the moment.<br>
   */
  private boolean add(String entity, boolean first) {
	if (first) {
	  System.out.println("Do you want to enter the information manually?");
	} else {
	  System.out.println("Manually?");
	}
	Utilities.expectInput();
	boolean enterManually = Utilities.convertYesOrNoToBoolean();
	Class<?> theClass = this.classEntities.get(entity);
	Constructor<?> constructor = null;
	try {
	  constructor = this.classEntities.get(entity).getConstructor(Map.class);
	} catch (NoSuchMethodException | SecurityException ex) {
	}
	int amount = 1;
	if (!enterManually) {
	  String message = "How many " + entity + "s would you like to add?\n";
	  amount = Utilities.getInput(0, (int) Byte.MAX_VALUE, message);
	}
	Object obj = null;
	Object constructorArgument;
	while (amount-- > 0) {
	  constructorArgument = Data.getDataFor(theClass, enterManually);
	  if (constructorArgument == null) {
		break;
	  }
	  try {
		obj = constructor.newInstance(constructorArgument);
	  } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException ex) {
	  }

	  if (Data.listContains(obj)) {
		System.out.println("This " + entity + " exists.");
	  } else {
		Dao dao = null;
		try {
		  dao = (Dao) this.daos.get(entity).newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
		}
		dao.create(obj);
	  }
	}
	if (enterManually) {
	  System.out.println("Do you want to add more " + entity + "s?");
	  Utilities.expectInput();
	  if (Utilities.convertYesOrNoToBoolean()) {
		add(entity, false);
	  }
	}
	return true;
  }

  /**
   * Accepts an entity name and if it is "course" it print the list of courses.<br>
   * Else it asks for further input to create a customized view and then calls<br>
   * the proper method.<br>
   *
   * @param entity The entity to create a view for.<br>
   * @param list The rest of the input.
   */
  private void print(String entity, List list) {
	String view = Utilities.findViewInList(list);
	if (view != null && Utilities.getViews().get(view).size() != 1) {
	  view = view.concat(Utilities.getViewVersion(view));
	  this.view.print(view);
	  return;
	}
	if (entity.equalsIgnoreCase("course")) {
	  printAll(entity);
	} else {
	  List<String> availableActions = new ArrayList();
	  switch (entity) {
		case "student": {
		  availableActions.add("1. all");
		  availableActions.add("2. per course");
		  availableActions.add("3. submission date");
		  availableActions.add("4. with multiple courses");
		  break;
		}
		case "trainer": {
		  availableActions.add("1. all");
		  availableActions.add("2. per course");
		  break;
		}
		case "assignment": {
		  availableActions.add("1. all");
		  availableActions.add("2. per course");
		  availableActions.add("3. per student per course");
		  break;
		}
	  }
	  String message = availableActions.stream().reduce("", (res, item) -> res.concat(item).concat("\n"));
	  availableActions = availableActions.stream().map(item -> item.substring(3)).collect(Collectors.toList());
	  if (message.length() > 0) {
		message = message.substring(0, message.length() - 1).concat("\n");
	  }

	  Integer numberOfActions = availableActions.size();
	  while (numberOfActions > 0) {
		availableActions.add(numberOfActions.toString());
		numberOfActions--;
	  }
	  String input = Utilities.getInput(availableActions, false, message);
	  createViewAndPrint(entity, input);
	}
  }

  private void createViewAndPrint(String entity, String input) {
	String view = "";
	if (input.equals("all") || input.equals("1")) {
	  view = entity.concat("_full");
	} else if (input.equals("submission date") || (entity.equals("student") && input.equals("3"))) {
	  printStudentsSubDate();
	  return;
	} else if (input.equals("with multiple courses") || input.equals("4")) {
	  view = "studentsWithManyCourses_FULL";
	} else {
	  if (input.equals("per course") || input.equals("2")) {
		view = entity.concat("percourse");
		view = view.concat(Utilities.getViewVersion(view));
	  } else if (input.equals("per student per course") || (entity.equals("assignment") && input.equals("3"))) {
		view = "assignmentperstudentpercourse";
		view = view.concat(Utilities.getViewVersion(view));
	  }
	}
	this.view.print(view);
  }

  private void printAll(String entity) {
	String view = entity.concat("_full");
	new View(view).print();
  }

  private void printStudentsSubDate() {
	String input;
	do {
	  System.out.println("Enter date:");
	  input = Data.validateDateFormat(scanner.nextLine());
	  if (input == null) {
		System.out.println("Invalid input.");
	  }
	} while (input == null);
	LocalDate date = LocalDate.parse(input);
	LocalDate monday = Utilities.getMonday(date);
	LocalDate friday = Utilities.getFriday(date);
	view.studentsSubmitThisWeek(monday, friday);
  }

  /**
   * Accepts the entity name and the Course object we must assign the entity to.<br>
   * Prompts the user to create a new course if he hasn't create any courses yes.<br>
   * Then checks if there are any entries of that entity that can be added.<br>
   * If there are any available entries it asks the user to pick one of them and adds
   * it.<br>
   * At last it asks the user if he wants to keep assigning entities to that
   * course.<br>
   *
   * @param entity The entity we want to add to a Course.<br>
   * @param course The Course object that acts as a key in the course map.<br>
   */
  private void assign(String entity, Course course) {
	Map<String, ArrayList<Object>> map = view.getMapFromRS("course_full", null, null);
	if (map == null || !View.mapIsNotEmpty(map)) {
//	if(this.entities.get("course").isEmpty()){
	  System.out.println("You need to create at least one course to perform this action");
	  System.out.println("Do you want to create a course now?");
	  Utilities.expectInput();
	  if (Utilities.convertYesOrNoToBoolean()) {
		add("course", true);
	  } else {
		return;
	  }
	}

	if (course == null) {
	  course = getCourseToAssignTo();
	}

	Map<String, ArrayList<Object>> entitiesMap = view.getMapFromRS(entity.concat("_full"), null, null);
	if (!View.mapIsNotEmpty(entitiesMap)) {
	  System.out.println("You need to add some " + entity + "s first.");
	  return;
	}
	List<Object> entityIDs = new ArrayList(entitiesMap.get("ID"));
	Map<String, ArrayList<Object>> entityPerCourseMap = view.getMapFromRS(entity.concat("course"), null, null);
	List<Object> entityInCourseIDs = new ArrayList();
	entityInCourseIDs.forEach(item -> System.out.println(item + " - " + item.getClass().getSimpleName()));
	if (View.mapIsNotEmpty(entityPerCourseMap)) {
	  for (int i = 0; i < entityPerCourseMap.get("cid").size(); i++) {
		if (Integer.parseInt(entityPerCourseMap.get("cid").get(i).toString()) == course.getId()) {
		  System.out.println("COURSE CONTAINS: " + entityPerCourseMap.get(entity.substring(0, 1).concat("id")).get(i));
		  entityInCourseIDs.add(entityPerCourseMap.get(entity.substring(0, 1).concat("id")).get(i));
		}
	  }
	}

	ArrayList<Object> tempList = new ArrayList<>(entityIDs);

	tempList.removeAll(entityInCourseIDs);

	entityInCourseIDs.forEach(System.out::println);

	if (entityIDs.isEmpty()) {
	  System.out.println("You need to create some " + entity + "s first.");
	} else if (tempList.isEmpty()) {
	  System.out.println("All available " + entity + "s have been added to this course.");
	} else {
	  Entity entry = getEntryFromList(entity);
	  if (entry == null) {
		return;
	  }
	  if (entityInCourseIDs.contains(Long.parseLong(entry.getId().toString()))) {
		System.out.println("This " + entity + " already exists in the course.");
	  } else {
		if (entity.equals("assignment") && !Utilities.isInDateRange(((Assignment) entry).getSubDateTime(), course.getStart_date(), course.getEnd_date())) {
		  System.out.println("Assignment submission date is outside of the course's scope.");
		} else {
		  EntityCourseDao dao = new EntityCourseDao(entity);
		  dao.create(entry, course);
		}
	  }
	}

	System.out.println("Keep assigning to the same course?");
	Utilities.expectInput();
	if (Utilities.convertYesOrNoToBoolean()) {
	  System.out.println("Keep assigning " + entity + "s?");
	  Utilities.expectInput();
	  if (Utilities.convertYesOrNoToBoolean()) {
		assign(entity, course);
	  } else {
		assign(pickEntity(null, "assign"), course);
	  }
	}
  }

  /**
   * This method searches the entities map for an entry in the 'entity' list.<br>
   * Gives the option to the user to search by name / title or index.<br>
   *
   * @param entity The entity of the entry we seek.<br>
   * @return An entry of the entities lists.<br>
   */
  private Entity getEntryFromList(String entity) {
	Map<String, ArrayList<Object>> map = view.getMapFromRS(entity.concat("_full"), null, null);
	view.printResults(map);
	List<String> list = new ArrayList<>();
	map.get("ID").forEach(id -> list.add(id.toString()));
	Object id = Utilities.getInput(list, false, "Enter the ID of the " + entity + "\n");
	int index = map.get("ID").indexOf(Long.parseLong(id.toString()));

	Constructor<?> constructor = Utilities.getConstructorWithMostParameteres(this.classEntities.get(entity));
	Class<?>[] types = constructor.getParameterTypes();
	Object[] args = new Object[constructor.getParameterCount()];

	for (int i = 0; i < types.length; i++) {
	  try {
		Constructor<?> con = Utilities.getStringConstructor(types[i]);
		if (con != null) {
		  Object[] arr = new Object[]{map.get(map.get("names").get(i).toString()).get(index).toString()};
		  args[i] = con.newInstance(arr);
		} else {
		  Method method = null;
		  try {
			try {
			  method = types[i].getDeclaredMethod("parse", new Class<?>[]{String.class});
			} catch (Exception ex) {
			}
			if (method == null) {
			  method = types[i].getDeclaredMethod("parse", new Class<?>[]{CharSequence.class});
			}
		  } catch (NoSuchMethodException ex) {
			System.out.println("Can't convert '"
				+ map.get(map.get("names").get(i).toString()).get(index).toString()
				+ "' to a " + types[i].getSimpleName() + " object."
				+ "\nThere is neither a constructor(String) nor a parse(String) method");
			return null;
		  } catch (SecurityException ex) {
		  }
		  if (method != null) {
			args[i] = method.invoke(types[i], map.get(map.get("names").get(i).toString()).get(index).toString());
		  }
		}
	  } catch (IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {}
	}
	Object obj = null;
	try {
	  obj = constructor.newInstance(args);
	  if (obj == null) {
		return null;
	  }
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
	}
	Entity newEntity = (Entity) obj;
	return newEntity;
  }

  /**
   * This method asks for input and if it matches a course title it returns that
   * Course<br>
   *
   * @return An Integer indicating a course ID.<br>
   */
  private Course getCourseToAssignTo() {
	String message = "Pick a course to begin:\n";
	String input;
	List<String> courseIDs = new ArrayList<>();
	Integer course = null;
	Map<String, ArrayList<Object>> map = view.getMapFromRS("course_full", null, null);
	do {
	  printAll("course");
	  input = Utilities.getInput(null, false, message);
	  if (View.mapIsNotEmpty(map)) {
		for (int i = 0; i < map.get("title").size(); i++) {
		  if (((String) map.get("title").get(i)).equalsIgnoreCase(input)) {
			courseIDs.add(map.get("ID").get(i).toString());
		  }
		}
		if (courseIDs.size() > 1) {
		  message = "Many courses found with that name.\nPick one by its ID.\n";
		  view.print("course_full", "title", input);
		  course = Integer.parseInt(Utilities.getInput(courseIDs, false, message)) - 1;
		} else if (courseIDs.isEmpty()) {
		  System.out.println("No such course.");
		} else {
		  course = Integer.parseInt(courseIDs.get(0)) - 1;
		}
	  }
	} while (course == null);
	return new Course(
		course + 1,
		(String) map.get("title").get(course),
		(String) map.get("stream").get(course),
		(String) map.get("type").get(course),
		((Date) map.get("start_date").get(course)).toLocalDate(),
		((Date) map.get("end_date").get(course)).toLocalDate()
	);
  }

  private void refactor(LinkedList<String> list) {
	System.out.println("Not implemented yet. Look out for v0.3");
  }

  private void grade() {
	Integer studentID = pickStudent();
	if (studentID == -1) {
	  System.out.println("There aren't any students that attend a course with assignments.\nYou need to assign some first.");
	  return;
	}
	Integer courseID = pickCourse(studentID);
	Integer assignmentID = pickAssignment(studentID, courseID);
	ACSDao dao = new ACSDao();
	AssignmentCourseStudent acs = dao.findById(assignmentID, courseID, studentID);

	LocalDate subdate = (LocalDate) Data.getInputForField("the assignment's", "submissionDate", LocalDate.class, acs.getCourse());

	Double oralMark = 0.0;
	if (acs.getAssignment().getOralMark() != 0) {
	  oralMark = (Double) Data.getInputForField("the assignment's", "oralMark", Double.class, acs.getAssignment().getOralMark());
	}

	Double totalMark = 0.0;
	if (acs.getAssignment().getTotalMark() > acs.getAssignment().getOralMark()) {
	  totalMark = (Double) Data.getInputForField("the assignment's", "totalMark", Double.class, acs.getAssignment().getTotalMark());
	  while (totalMark < oralMark) {
		System.out.println("Total mark can't be less than the oral mark.");
		totalMark = (Double) Data.getInputForField("the assignment's", "totalMark", Double.class, acs.getAssignment().getTotalMark());
	  }
	} else {
	  totalMark = oralMark;
	}

	acs.setSubDate(subdate);
	acs.setOralMark(oralMark);
	acs.setTotalMark(totalMark);

	if (dao.update(acs)) {
	  System.out.println("Grades and submission date were updated.");
	}
  }

  private Integer pickStudent() {
	String studentsWithAssignments = "withAssignments_students";
	Map<String, ArrayList<Object>> map = view.getMapFromRS(studentsWithAssignments, null, null);
	view.printResults(map);

	String message = "Pick a student\n";
	ArrayList<String> studentIdPool = new ArrayList<>();
	if (!View.mapIsNotEmpty(map)) {
	  return -1;
	}
	map.get("ID").forEach(item -> studentIdPool.add(item.toString()));
	Integer studentID = Integer.parseInt(Utilities.getInput(studentIdPool, false, message));
	return studentID;
  }

  private Integer pickCourse(Integer studentID) {
	String query
		= "SELECT c.* "
		+ "FROM withAssignments_students s, withAssignments_courses c, "
		+ "assignmentperstudentpercourse_Full acs "
		+ "WHERE acs.sid = s.id "
		+ "AND acs.cid = c.id "
		+ "AND s.id = " + studentID
		+ " GROUP BY c.id";
	Map<String, ArrayList<Object>> map = view.getMapFromRS(query, null, null);
	view.printResults(map);

	String message = "Pick a course\n";
	ArrayList<String> courseIdPool = new ArrayList<>();
	map.get("ID").forEach(item -> courseIdPool.add(item.toString()));
	Integer courseID = Integer.parseInt(Utilities.getInput(courseIdPool, false, message));
	return courseID;
  }

  private Integer pickAssignment(Integer studentID, Integer courseID) {
	String query
		= "SELECT a.*"
		+ "FROM withAssignments_students s, withAssignments_courses c,"
		+ "assignment_full a, assignmentperstudentpercourse_Full acs "
		+ "WHERE acs.sid = s.id "
		+ "AND acs.cid = c.id "
		+ "AND acs.aid = a.id "
		+ "AND s.id = " + studentID
		+ " AND c.id = " + courseID;
	Map<String, ArrayList<Object>> map = view.getMapFromRS(query, null, null);
	view.printResults(map);

	String message = "Pick the assignment to add grades\n";
	ArrayList<String> assignmentIdPool = new ArrayList<>();
	map.get("ID").forEach(item -> assignmentIdPool.add(item.toString()));
	Integer assignmentID = Integer.parseInt(Utilities.getInput(assignmentIdPool, false, message));
	return assignmentID;
  }

}
