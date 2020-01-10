package project;

import java.time.LocalDate;
import java.util.*;

public class SyntheticData{
  
  public static ArrayList<String> firstNames = new ArrayList();
  public static ArrayList<String> lastNames = new ArrayList();
  public static ArrayList<String> courseTitle = new ArrayList();
  public static ArrayList<String> stream = new ArrayList();
  public static ArrayList<String> type = new ArrayList();
  public static ArrayList<String> subject = new ArrayList();
  public static ArrayList<String> assignmentTitle = new ArrayList();
  public static ArrayList<String> description = new ArrayList();
  
  public static Double getRandomTuitionFees(){
	return Math.floor(Math.random() * 1000) + 1000;
  }
  
  public static Double getRandomOralMark(){
	return Math.floor(Math.random() * 30);
  }
  
  public static Double getRandomTotalMark(){
	return Math.floor(Math.random() * 100 + 30);
  }
  
  public static LocalDate getRandomDate(Object objectToCheckAgainst){
	LocalDate date = null;
	if(objectToCheckAgainst != null){
	  try {
		date = (LocalDate) objectToCheckAgainst;
	  } catch (Exception e) {}
	}
	return getUpcomingDate(date);
  }
  
  public static LocalDate getRandomBirthDate(){
	return getBirthDate();
  }
  
  public static Object getRandom(List<String> list){
	int randomIndex = (int) (Math.random() * (list.size()));
	return list.get(randomIndex);
  }
  
  public static Object getAndRemove(List<String> list){
	return list.remove(0);
  }
  
  public static void populate(){
	firstNames.add("Konstantinos");
	firstNames.add("Dimitris");
	firstNames.add("Giorgos");
	firstNames.add("Giannis");
	firstNames.add("Vasilis");
	firstNames.add("Grigoris");
	firstNames.add("Nikos");
	firstNames.add("Pavlos");
	firstNames.add("Maria");
	firstNames.add("Dimitra");
	firstNames.add("Eleni");
	firstNames.add("Sofia");
	firstNames.add("Konstantina");
	firstNames.add("Georgia");
	firstNames.add("Vasiliki");
	
	lastNames.add("Diakos");
	lastNames.add("Diamantopoulos");
	lastNames.add("Dimitriadis");
	lastNames.add("Filippou");
	lastNames.add("Konstantinou");
	lastNames.add("Hatzis");
	lastNames.add("Laskaris");
	lastNames.add("Kyriakoy");
	lastNames.add("Pavlidis");
	lastNames.add("Papadopoulos");
	lastNames.add("Pappas");
	lastNames.add("Petrakis");
	lastNames.add("Sideris");
	lastNames.add("Vasilakis");
	lastNames.add("Theodoropoulos");
	
	courseTitle.add("Programming 101");
	courseTitle.add("Intro to OOP");
	courseTitle.add("Back end Advanced");
	courseTitle.add("HTML and CSS");
	courseTitle.add("Intro to Javascript");
	courseTitle.add("Javascript Frameworks");
	
	stream.add("Java");
	stream.add("C#");
	stream.add("PHP");
	stream.add("Nodejs");
	stream.add("Python");
	stream.add("Ruby");
	
	type.add("Full Time");
	type.add("Part Time");
	type.add("Summer school");
	
	subject.add("Maths");
	subject.add("CS");
	subject.add("Designer");
	subject.add("Backend Developer");
	subject.add("Frontend Developer");
	
	assignmentTitle.add("Project 1");
	assignmentTitle.add("Project 2");
	assignmentTitle.add("Project 3");
	assignmentTitle.add("Project 4");
	assignmentTitle.add("Project 5");
	
	description.add("Create an application to manage the inventory");
	description.add("Create a hello world Reactjs application");
	description.add("Create a scientific calculator");
	description.add("Create a weather app");
	description.add("Create a Tic-Tac-Toe game");
	description.add("Create a markdown previewer");
  }


  public static LocalDate getUpcomingDate(LocalDate date) {
	if(date == null){
	  LocalDate date2 = LocalDate.ofYearDay(2020, (int) (Math.random() * 120 + 1));
	  return date2.getDayOfWeek().getValue() < 6 ? date2 : date2.minusDays(2);
	} else {
	  return date.plusDays((Math.round(Math.random()*10))*7+7);
	}
  }

  public static LocalDate getBirthDate() {
	int year = (int) (Math.random() * 22 + 1980);
	int day = (int) (Math.random() * 365 + 1);
	return LocalDate.ofYearDay(year, day);
  }
}
