package project;

import dao.AssignmentDao;
import dao.CourseDao;
import dao.StudentDao;
import dao.TrainerDao;
import entities.Student;
import entities.Assignment;
import entities.Trainer;
import entities.Course;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Scanner;

public class Data {
  
  private static Scanner scanner = Utilities.getScanner();
  private static HashMap<String, ArrayList<Object>> entities;
  private static HashSet<String> actions;
  private static HashMap<String, Class<?>> classEntities;
  private static HashMap<String, Class<?>> daos;
  
  
  public static void createDataStructures(){
	if(entities == null){
	  entities = new HashMap<>();
	  entities.put("student", new ArrayList<>());
	  entities.put("trainer", new ArrayList<>());
	  entities.put("course", new ArrayList<>());
	  entities.put("assignment", new ArrayList<>());
	}
	if(actions == null){
	  actions = new HashSet();
	  actions.add("add");
	  actions.add("refactor");
	  actions.add("assign");
	  actions.add("print");
	  actions.add("grade students");
	  actions.add("grade");
	  actions.add("clear");
	}
	if(classEntities == null){
	  classEntities = new HashMap<>();
	  classEntities.put("student", Student.class);
	  classEntities.put("trainer", Trainer.class);
	  classEntities.put("course", Course.class);
	  classEntities.put("assignment", Assignment.class);
	}
	if(daos == null){
	  daos = new HashMap<>();
	  daos.put("student", StudentDao.class);
	  daos.put("course", CourseDao.class);
	  daos.put("trainer", TrainerDao.class);
	  daos.put("assignment", AssignmentDao.class);
	}
  }

  public static HashMap<String, ArrayList<Object>> getEntities() {
	return entities;
  }

  public static HashSet<String> getActions() {
	return actions;
  }

  public static HashMap<String, Class<?>> getClassEntities() {
	return classEntities;
  }

  public static HashMap<String, Class<?>> getDaos() {
	return daos;
  }
  
  
  
  /**
   * A method that gets the fields of a Class, generates values for each the fields,<br>
   * places them in a map and returns the map.
   * @param type
   * represents the class that we need to get values for.<br>
   * @param manually
   * <b>true</b>: the user will be asked for input.<br>
   * <b>false</b>: synthetic data will be generated.<br>
   * @return
   * a hashMap that maps each field of the class to a value
   */
  public static Map getDataFor(Class<?> type, boolean manually){
	Field[] fields = type.getDeclaredFields();
	Map<String, Object> argsMap = new HashMap<>();
	if(type.getSimpleName().toLowerCase().equals("course") && SyntheticData.courseTitle.isEmpty())
	  return null;
	
	for (Field field : fields) {
	  String tempFieldName = field.getName();
	  if(manually){
		if(type.getSimpleName().equals("Course") && tempFieldName.equals("end_date"))
		  argsMap.put(tempFieldName,
			  getInputForField(type.getSimpleName(), tempFieldName, field.getType(), argsMap.get("start_date")));
		else{
		  Object obj = null;
		  if(type.getSimpleName().equals("Assignment") && tempFieldName.equals("totalMark")){
			obj = -((Double)argsMap.get("oralMark"));
		  }
		  argsMap.put(tempFieldName, getInputForField(type.getSimpleName(), tempFieldName, field.getType(), obj));
		}
	  }
	  else if(type.getSimpleName().equals("Assignment") && tempFieldName.equals("title")){	
		argsMap.put(tempFieldName, generate("assignmentTitle"));
	  } else if(type.getSimpleName().equals("Course") && tempFieldName.equals("end_date")){
		argsMap.put(tempFieldName, generate(tempFieldName, argsMap.get("start_date")));
	  }
	  else 
		argsMap.put(tempFieldName, generate(tempFieldName));
	}
	return argsMap;
  }

  
  /**
   * This method gets input from the user and passes that input along with the rest<br>
   * of its arguements for validation.
   * @param className
   * The name of the Class that holds the field we want to get a value for.
   * Currently if it is null it means that the method was called from a field refactor method.<br>
   * @param fieldName
   * The name of the field we want to get a value for.<br>
   * @param type
   * The class type of the <b>field</b> we want to get a value for.<br>
   * @return 
   * The value that will be assigned to the field.
   */
  public static Object getInputForField(String className, String fieldName, Class<?> type, Object obj) {
	if(className != null)
	  System.out.printf("Enter %s %s\n", className, Utilities.getTextFromCamelCase(fieldName));
	Utilities.expectInput();
	return validateInputTypeAndCast(scanner.nextLine(), type, className, fieldName, obj);
  }
  
  
  /**
   * All the input provided is of type String, so if the type of the field is not
   * String, it tries to cast the correct type inside a try block. If it fails it
   * will ask for new input to try again. If it succeeds it sends the input for
   * value validatation and receives a String. If the String is empty then the value
   * is valid, else the String is printed.
   * @param input
   * The user provided input we need to validate.<br>
   * @param type
   * The class type of the field we want to validate the input for.<br>
   * @param className
   * The name of the Class that holds the field we want to validate the input for.
   * Provided in case two classes use the same name for a field but require
   * a different validation process.<br>
   * @param fieldName
   * The name of the field we want to validate the input for.<br>
   * @return
   * Returns the input if it passes the validation checks.<br>
   * Otherwise it asks for new input until it gets some that checks out.<br>
   */
  private static Object validateInputTypeAndCast
	(String input, Class<?> type, String className, String fieldName, Object ObjectToCheckAgainst){
	try {
	  switch (type.getSimpleName()) {
	  	case "String":
		  {
			String check = inputValueIsValid(className, fieldName, input, null);
			if(check.equals(""))
			  return input;
			else
			  System.out.println(check);
			break;
		  }
	  	case "LocalDate":
		  {
			String validatedDateString = validateDateFormat(input);
			if(validatedDateString == null) break;
			LocalDate date = LocalDate.parse(validatedDateString);
			String check = inputValueIsValid(className, fieldName, date, ObjectToCheckAgainst);
			if(check.equals(""))
			  return date;
			else
			  System.out.println(check);
			break;
		  }
	  	default:
		  {
			Object obj = type.getConstructor(String.class).newInstance(input);
			String check = inputValueIsValid(className, fieldName, obj, ObjectToCheckAgainst);
			if(check.equals(""))
			  return obj;
			else
			  System.out.println(check);
			break;
		  }
	  }
	} catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
//	  ex.printStackTrace();
	  System.out.println("Invalid input.");
	}
	System.out.println("Try again.");
	return getInputForField(className, fieldName, type, ObjectToCheckAgainst);
  }

  /**
   * Accepts a string that represents a field of our four basic entities and calls<br>
   * the appropriate method for that field.<br>
   * @param className
   * The name of the Class that holds the field we want to get a value for.
   * Provided in case two classes use the same name for a field but require
   * a different validation process.<br>
   * @param fieldName
   * The name of the field we want to validate the value for.<br>
   * @param input
   * The user provided input we need to validate.<br>
   * @return
   * A message to be displayed if the input doed not validate, otherwise an empty string.<br>
   */
  private static String inputValueIsValid(String className, String fieldName, Object input, Object ObjectToCheckAgainst) {
	if(input == null) return null;
	switch(fieldName){
	  case "dateOfBirth": return validateBirthDate(input);
	  case "start_date":
	  case "end_date":
	  case "submissionDate":
	  case "subDateTime": return validateDate(input, ObjectToCheckAgainst);
	  case "oralMark":
	  case "totalMark": return validateMark(input, ObjectToCheckAgainst);
	  case "tuitionFees": return validateFees(input);
	  default: return validateString(input);
	}
  }
  
  
  /**
   * Accepts a string that represents a field name and returns synthetic data.<br>
   * @param type String representing the name of a field.
   * @return 
   * Object containing synthetic data.
   */
  private static Object generate(String type){
	return generate(type, null);
  }
  private static Object generate(String type, Object objectToCheckAgainst){
	switch(type){
	  case "firstName": return SyntheticData.getRandom(SyntheticData.firstNames);
	  case "lastName": return SyntheticData.getRandom(SyntheticData.lastNames);
	  case "title": return SyntheticData.getAndRemove(SyntheticData.courseTitle);
	  case "stream": return SyntheticData.getRandom(SyntheticData.stream);
	  case "subject": return SyntheticData.getRandom(SyntheticData.subject);
	  case "type": return SyntheticData.getRandom(SyntheticData.type);
	  case "assignmentTitle": return SyntheticData.getRandom(SyntheticData.assignmentTitle);
	  case "description": return SyntheticData.getRandom(SyntheticData.description);
	  case "start_date":
	  case "end_date":
	  case "subDateTime": return SyntheticData.getRandomDate(objectToCheckAgainst);
	  case "dateOfBirth": return SyntheticData.getRandomBirthDate();
	  case "oralMark": return SyntheticData.getRandomOralMark();
	  case "totalMark": return SyntheticData.getRandomTotalMark();
	  case "tuitionFees": return SyntheticData.getRandomTuitionFees();
	  default: return null;
	}
  }
  
  
  /**
   * Accepts an object representing a date and checks if it is not in
   * the past or in weekend.<br>
   * @param input Object that is converted to LocalDate.<br>
   * @return 
   * An error message if it fails, otherwise an empty string.<br>
   */
  private static String validateDate(Object input, Object obj){
	LocalDate date = (LocalDate) input;
	LocalDate date2 = null;
	if(obj instanceof Course){
	  Course course = (Course) obj;
	  if(date.isBefore(course.getStart_date()))
		return "Submitting an assignment before the course kicks off?\nDoes not seem right.";
	  else if(date.isAfter(course.getEnd_date())){
		System.out.println("The assignment was turned in after the deadline.");
	  }
	  return "";
	}
	if(obj != null){
	  try {
		date2 = (LocalDate) obj;
	  } catch (Exception e) {}
	}
	if(date2 != null && date.isBefore(date2))
	  return "Can't end something before it even starts.";
	if(date.isBefore(LocalDate.now()))
	  return "Did you invent a time machine to go back in time?";
	if(date.getDayOfWeek().getValue() > 5)
	  return "People are suppposed to rest on weekends, remember?";
	return "";
  }

  /**
   * Accepts an object representing a date and checks if it between 1940 - 2002.<br>
   * @param input Object that is converted to LocalDate.<br>
   * @return 
   * An error message if it fails, otherwise an empty string.<br>
   */
  private static String validateBirthDate(Object input){
	LocalDate date = (LocalDate) input;
	if(date.getYear() > 2002)
	  return "Too young, go back to school.";
	else if(date.getYear() < 1940)
	  return "No offense but I feel you are a bit too old.";
	else
	  return "";
  }

  /**
   * Accepts an object representing a mark and checks if it between 0 - 100.<br>
   * @param input Object that represents a mark.<br>
   * @return 
   * An error message if it fails, otherwise an empty string.<br>
   */
  private static String validateMark(Object input, Object obj) {
	Double mark = (double) input;
	if(obj != null){
	  Double limit = (Double) obj;
	  if(limit <= 0){
		limit = -limit;
		if(mark<limit){
		  return "Total mark can't be less than the oral mark.";
		}
	  }else{
		if(mark > limit){
		  return "Mark can't be greater than " + limit;
		}
		if(mark<0)
		  return "Don't be mean.";
	  }
	}
	if(mark<0)
	  return "Let's keep the marks to the non negative range.";
	return "";
  }
  
//    private static String validateMark(Object input) {
//	  return validateMark(input, null);
//	}
  

  /**
   * Accepts an object representing a mark and checks if it greater than 0.<br>
   * @param input Object that represents a double value.<br>
   * @return 
   * An error message if it fails, otherwise an empty string.<br>
   */
  private static String validateFees(Object input) {
	return ((double)input) > 0 ? "" : "You may apply for a scholarship if you want";
  }

  /**
   * Accepts an object representing a string and checks if it is not empty.<br>
   * @param input Object that represents a double value.<br>
   * @return 
   * An error message if it fails, otherwise an empty string.<br>
   */
  private static String validateString(Object input) {
	return ((String)input).length()>0 ? "" : "Can't accept an empty string";
  }
  
  /**
   * Accepts an Object and assigns it to a string(str2).<br>
   * Creates a new string(str1) that contains the input stripped from whitespaces 
   * and numbers.<br>
   * The characters of that string(str1) are now considered delimiters
   * and are added to a HashSet.<br>
   * Next it replaces each occurance of each delimiter in the string with '-'.<br>
   * Next it creates two formatters and checks the input against them.<br>
   * If it input fits the date format it returns a LocalDate object,
   * otherwise null.<br>
   * @param input Object that we will check if it can be converted to a LocalDate.
   * @return 
   * LocalDate object or null
   */
  public static String validateDateFormat(Object input){
	String str2 = (String) input;
	String str = ((String)input).replaceAll("[\\s\\d+]+", "");
	Set<String> set = new HashSet<>();
	Arrays.asList(str.split("")).forEach(set::add);
	for(String delimiter:set)
	  str2 = str2.replaceAll(delimiter, "-");
	List<DateTimeFormatter> formatters = new ArrayList();
	formatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	formatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	for(DateTimeFormatter formatter: formatters){
	  try {
		LocalDate date = LocalDate.parse((String)str2, formatter);
		return date.toString();
	  } catch (Exception e) {}
	}
	return null;
  }

  public static HashMap hardCodedStructure() {
	HashMap structure = new HashMap();
	Set<String> entities = new HashSet();
	entities.add("student");
	entities.add("trainer");
	entities.add("course");
	entities.add("assignment");
	structure.put("add", entities);
	structure.put("refactor", entities);
	return structure;
  }

  /**
   * This method checks if a list contains an Object, checking if the string<br>
   * representation of the objects.<br>
   * @param list The list of objects to check against.<br>
   * @param object The object we are looking for.<br>
   * @return true or false<br>
   */
  private static boolean listContains(List<Object> list, Object object) {
	String str = getId(object);
	for (Object item : list) {
	  if (str.equals(getId(item))) {
		return true;
	  }
	}
	return false;
  }
  
  public static boolean listContains(Object object){
//	String query = "SELECT * FROM ".concat(object.getClass().getSimpleName()).concat("_full");
//	Map map = new View().getMapFromRS(query);
//	map.forEach((key, value) -> {
//	  System.out.println("\n\nKEY: " + key + " - VALUE: " + value);
//	});
	
	switch(object.getClass().getSimpleName().toLowerCase()){
	  case "student":
		return new StudentDao().checkIfExists(((Student)object));
	  case "course":
		return new CourseDao().checkIfExists(((Course)object));
	  case "assignment":
		return new AssignmentDao().checkIfExists(((Assignment)object));
	  case "trainer":
		return new TrainerDao().checkIfExists(((Trainer)object));
		
	}
	return true;
  }

  /**
   * Accepts an Object and returns a custom string representation.
   * @param obj The object to create a string from its fields.
   * @return A string containing concatenated fields of the input object.
   */
  private static String getId(Object obj) {
	if (obj instanceof Student) {
	  return (((Student) obj).getFirstName().concat(((Student) obj).getLastName()).concat(((Student) obj).getDateOfBirth().toString())).toLowerCase();
	}
	if (obj instanceof Course) {
	  return (((Course) obj).getTitle().concat(((Course) obj).getStream()).concat(((Course) obj).getType())).toLowerCase();
	}
	if (obj instanceof Trainer) {
	  return (((Trainer) obj).getFirstName().concat(((Trainer) obj).getLastName())).toLowerCase();
	}
	if (obj instanceof Assignment) {
	  return (((Assignment) obj).getTitle().concat(((Assignment) obj).getDescription())).toLowerCase();
	}
	return "";
  }

  /**
   * Checks if two objects are equal using a custom string representation.<br>
   * @param obj1 Object 1<br>
   * @param obj2 Object 2<br>
   * @return true or false<br>
   */
  public static boolean checkEquality(Object obj1, Object obj2) {
	return getId(obj1).equals(getId(obj2));
  }

  static void changeDate(Object entry, LocalDate start_date, LocalDate end_date) {	
	LocalDate date;
	do {
	  System.out.println("The submission date is out of the range of the course.");
	  System.out.println("Please enter a new submission date for the assignment.");
	  Utilities.expectInput();
	  date = (LocalDate) validateInputTypeAndCast(scanner.nextLine(), LocalDate.class, null, "subDateTime", null);
	} while (!Utilities.isInDateRange(date, start_date, end_date));
	((Assignment)entry).setSubDateTime(date);
  }

}

