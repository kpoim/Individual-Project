CREATE DATABASE privateschool DEFAULT CHAR SET utf8mb4;
USE privateschool;

CREATE TABLE Courses(
	cid INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    ctitle VARCHAR(30) NOT NULL,
    cstream VARCHAR(15) NOT NULL,
    ctype VARCHAR(15) DEFAULT 'full-time',
    csdate DATE NOT NULL,
    cedate DATE NOT NULL
);

CREATE TABLE Students(
	sid INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    sfname VARCHAR(15) NOT NULL,
    slname VARCHAR(15) NOT NULL,
    sdob DATE NOT NULL,
    stfees DECIMAL(7,2) DEFAULT 0
);

CREATE TABLE Trainers(
	tid INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    tfname VARCHAR(15) NOT NULL,
    tlname VARCHAR(15) NOT NULL,
    tsubj VARCHAR(30) NOT NULL
);

CREATE TABLE Assignments(
	aid INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    atitle VARCHAR(30) NOT NULL,
    adesc VARCHAR(100) DEFAULT '',
    asubdate DATE DEFAULT '1970-01-01',
    aomark DECIMAL(5,2) DEFAULT 0,
    atmark DECIMAL(5,2) DEFAULT 0
);

CREATE TABLE studentcourse(
	cid INT UNSIGNED NOT NULL,
	sid INT UNSIGNED NOT NULL,
    CONSTRAINT pk_sc
		PRIMARY KEY (sid, cid),
	CONSTRAINT fk_sc_course
		FOREIGN KEY (cid)
		REFERENCES Courses(cid),
	CONSTRAINT fk_sc_student
		FOREIGN KEY (sid)
		REFERENCES Students(sid)
);

CREATE TABLE trainercourse(
	cid INT UNSIGNED NOT NULL,
	tid INT UNSIGNED NOT NULL,
    CONSTRAINT pk_tc
		PRIMARY KEY (tid, cid),
	CONSTRAINT fk_tc_course
		FOREIGN KEY (cid)
		REFERENCES Courses(cid),
	CONSTRAINT fk_tc_trainer
		FOREIGN KEY (tid)
		REFERENCES Trainers(tid)
);

CREATE TABLE assignmentcourse(
	cid INT UNSIGNED NOT NULL,
	aid INT UNSIGNED NOT NULL,
    CONSTRAINT pk_ac
		PRIMARY KEY (aid, cid),
	CONSTRAINT fk_ac_course
		FOREIGN KEY (cid)
		REFERENCES Courses(cid),
	CONSTRAINT fk_ac_assignment
		FOREIGN KEY (aid)
		REFERENCES Assignments(aid)
);

CREATE TABLE assignmentcoursestudent(
    acssubdate DATE DEFAULT '1970-01-01',
    acsomark DECIMAL(5,2) DEFAULT 0,
    acstmark DECIMAL(5,2) DEFAULT 0,
	aid INT UNSIGNED NOT NULL,
	cid INT UNSIGNED NOT NULL,
    sid INT UNSIGNED NOT NULL,
    CONSTRAINT pk_acs
		PRIMARY KEY (aid, cid, sid),
    CONSTRAINT fk_acs_assignments
		FOREIGN KEY (aid)
        REFERENCES Assignments(aid),
    CONSTRAINT fk_acs_courses
		FOREIGN KEY (cid)
        REFERENCES Courses(cid),
    CONSTRAINT fk_acs_students
		FOREIGN KEY (sid)
        REFERENCES Students(sid)
);


# VIEWS

CREATE VIEW studentPerCourse_FULL
AS (
	SELECT s.*, c.*
	FROM studentcourse sc, students s, courses c
	WHERE sc.sid = s.sid
	AND sc.cid = c.cid
    ORDER BY c.ctitle
);

CREATE VIEW studentPerCourse_SIMPLE
AS (
	SELECT sfname firstName, slname lastName, ctitle courseTitle, cstream stream, ctype type
	FROM studentcourse sc, students s, courses c
	WHERE sc.sid = s.sid
	AND sc.cid = c.cid
    ORDER BY courseTitle
);

CREATE VIEW trainerPerCourse_FULL
AS(
	SELECT t.*, c.*
	FROM trainercourse tc, trainers t, courses c
	WHERE tc.tid = t.tid
	AND tc.cid = c.cid
	ORDER BY c.ctitle
);

CREATE VIEW trainerPerCourse_SIMPLE
AS(
	SELECT tfname firstName, tlname lastName, tsubj 'subject', ctitle courseTitle, cstream stream
	FROM trainercourse tc, trainers t, courses c
	WHERE tc.tid = t.tid
	AND tc.cid = c.cid
	ORDER BY courseTitle
);
    
CREATE VIEW assignmentPerCourse_FULL
AS (
	SELECT a.*, c.*
    FROM assignmentcourse ac, assignments a, courses c
    WHERE ac.aid = a.aid
    AND ac.cid = c.cid
    ORDER BY c.ctitle
);

    
CREATE VIEW assignmentPerCourse_SIMPLE
AS (
	SELECT atitle title, asubdate submissionDate, ctitle courseTitle, cstream stream
    FROM assignmentcourse ac, assignments a, courses c
    WHERE ac.aid = a.aid
    AND ac.cid = c.cid
    ORDER BY courseTitle
);

CREATE VIEW assignmentPerStudentPerCourse_FULL
AS(
	SELECT a.*, acssubdate, s.*, c.*
    FROM assignmentcoursestudent acs, assignmentcourse ac, assignments a, courses c, students s
    WHERE acs.aid = ac.aid
    AND acs.cid = ac.cid
    AND acs.sid = s.sid
    AND ac.aid = a.aid
    AND ac.cid = c.cid
    ORDER BY a.atitle, c.ctitle, s.slname
);

CREATE VIEW assignmentPerStudentPerCourse_SIMPLE
AS(
	SELECT sfname firstName, slname lastName, atitle assignment, datediff(asubdate, acssubdate)>=0 Submitted_in_time, -- acssubdate submissionDate, 
        concat(acstmark, ' / ', atmark) totalMark, ctitle courseTitle, cstream stream
    FROM assignmentcoursestudent acs, assignmentcourse ac, assignments a, courses c, students s
    WHERE acs.aid = ac.aid
    AND acs.cid = ac.cid
    AND acs.sid = s.sid
    AND ac.aid = a.aid
    AND ac.cid = c.cid
    ORDER BY assignment, courseTitle, lastName
);

CREATE VIEW withAssignments_courses
AS (
	SELECT  c.* FROM courses c, assignments a, assignmentcourse ac
	WHERE ac.aid = a.aid
	AND ac.cid = c.cid
	GROUP BY c.cid
);
    
CREATE VIEW withAssignments_students
AS (
	SELECT s.* FROM students s, withAssignments_courses c, studentcourse sc
	WHERE sc.sid = s.sid
	AND sc.cid = c.cid
	GROUP BY s.sid
);


# TRIGGERS
# THESE TRIGGERS INSERT NEW ENTRIES IN THE ASSIGNMENT PER STUDENT PER COURSE TABLE
# AFTER A NEW ROW WAS ADDED IN THE RESPECTIVE TABLE.

delimiter $$
CREATE TRIGGER after_studentcourse_insert
AFTER INSERT
ON studentcourse
FOR EACH ROW
BEGIN
	DECLARE courseID INT DEFAULT NEW.cid;
    DECLARE studentID INT DEFAULT NEW.sid;
    DECLARE assignmentID INT DEFAULT 0;
	DECLARE finished INT DEFAULT 0;
    DECLARE assignmentsCursor
    CURSOR FOR (SELECT aid FROM assignmentcourse WHERE cid = NEW.cid);
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;
    OPEN assignmentsCursor;
    myLoop: LOOP
		FETCH assignmentsCursor INTO assignmentID;
        IF finished = 1 THEN
			CLOSE assignmentsCursor;
            LEAVE myLoop;
		END IF;
        INSERT INTO assignmentcoursestudent(aid, cid, sid)
        VALUES (assignmentID, courseID, studentID);
	END LOOP;
END $$
delimiter ;


delimiter $$
CREATE TRIGGER after_assignmentcourse_insert
AFTER INSERT
ON assignmentcourse
FOR EACH ROW
BEGIN
	DECLARE courseID INT DEFAULT NEW.cid;
    DECLARE assignmentID INT DEFAULT NEW.aid;
    DECLARE studentID INT DEFAULT 0;
	DECLARE finished INT DEFAULT 0;
    DECLARE studentsCursor
    CURSOR FOR (SELECT sid FROM studentcourse WHERE cid = NEW.cid);
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;
    OPEN studentsCursor;
    myLoop: LOOP
		FETCH studentsCursor INTO studentID;
        IF finished = 1 THEN
			CLOSE studentsCursor;
            LEAVE myLoop;
		END IF;
        INSERT INTO assignmentcoursestudent(aid, cid, sid)
        VALUES (assignmentID, courseID, studentID);
	END LOOP;
END $$
delimiter ;

# OUTPUT

# PRINT ALL STUDENTS
CREATE VIEW student_FULL
AS (
	SELECT sid ID, sfname first_name, slname last_name, sdob date_of_birth, stfees tuition_fees
    FROM students
);


# PRINT ALL TRAINERS
CREATE VIEW trainer_FULL
AS (
	SELECT tid ID, tfname first_name, tlname last_name, tsubj 'subject'
    FROM trainers
);


# PRINT ALL ASSIGNMENTS
CREATE VIEW assignment_FULL
AS (
	SELECT aid ID, atitle title, adesc 'description', asubdate submission_date, aomark oral_mark, atmark total_mark
    FROM assignments
);


# PRINT ALL COURSES
CREATE VIEW course_FULL
AS (
	SELECT cid ID, ctitle title, cstream stream, ctype 'type', csdate start_date, cedate end_date
    FROM courses
);


# PRINT STUDENTS PER COURSE
CREATE VIEW studentPerCourse_EXTENDED
AS (
	SELECT sfname firstName, slname lastName, sdob dateOfBirth, stfees tuitionFees,
		ctitle courseTitle, cstream stream, ctype type, csdate startDate, cedate endDate
	FROM studentcourse sc, students s, courses c
	WHERE sc.sid = s.sid
	AND sc.cid = c.cid
    ORDER BY courseTitle
);


# PRINT TRAINERS PER COURSE
CREATE VIEW trainerPerCourse_EXTENDED
AS(
	SELECT tfname firstName, tlname lastName, tsubj 'subject',
		ctitle courseTitle, cstream stream, ctype type, csdate startDate, cedate endDate
	FROM trainercourse tc, trainers t, courses c
	WHERE tc.tid = t.tid
	AND tc.cid = c.cid
	ORDER BY courseTitle
);


# PRINT ASSIGNMENTS PER COURSE    
CREATE VIEW assignmentPerCourse_EXTENDED
AS (
	SELECT atitle title, adesc 'description', asubdate submissionDate, aomark maxOralMark, atmark maxTotalMark,
		ctitle courseTitle, cstream stream, ctype type, csdate startDate, cedate endDate
    FROM assignmentcourse ac, assignments a, courses c
    WHERE ac.aid = a.aid
    AND ac.cid = c.cid
    ORDER BY courseTitle
);


# PRINT ASSIGNMENTS PER STUDENT PER COURSE
CREATE VIEW assignmentPerStudentPerCourse_EXTENDED
AS(
	SELECT sfname firstName, slname lastName, sdob dateOfBirth, stfees tuitionFees,
        atitle assignment, adesc 'description', asubdate submission_deadline, acssubdate submissionDate, concat(acsomark, ' / ', aomark) oralMark,
		concat(acstmark, ' / ', atmark) totalMark,
		ctitle courseTitle, cstream stream, ctype type, csdate startDate, cedate endDate
    FROM assignmentcoursestudent acs, assignmentcourse ac, assignments a, courses c, students s
    WHERE acs.aid = ac.aid
    AND acs.cid = ac.cid
    AND acs.sid = s.sid
    AND ac.aid = a.aid
    AND ac.cid = c.cid
    ORDER BY assignment, courseTitle, lastName
);

-- STUDENTS THAT BELONG TO MORE THAN ONE COURSES
CREATE VIEW studentsWithManyCourses_FULL
AS (
	SELECT s.sid ID, s.sfname first_name, s.slname last_name, s.sdob date_of_birth, s.stfees tuition_fees, count(*) courses
	FROM students s, studentcourse sc
	WHERE s.sid = sc.sid
	GROUP BY s.sid
	HAVING courses > 1
);

# POPULATING MAIN ENTITIES

INSERT INTO students (sfname, slname, sdob, stfees)
VALUES 
	('Giorgos', 'Alexandris', 19900419, 1200.00),
    ('Dimitris', 'Aggelou', '1988-01-30', 1200.00),
    ('Maria', 'Georgiou', '1992-03-22', 1100.00),
    ('Sofia', 'Dimitriadi', '1995-09-02', 1000.00),
    ('Periklis', 'Dimou', '1983-11-14', 1250.00),
    ('Dimitra', 'Karagianni', '1979-05-10', 1350.00),
    ('Sotiris', 'Panagiotidis', '1998-02-01', 1000.00),
    ('Nikos', 'Sarafis', '1989-12-10', 1200.00),
    ('Eleni', 'Margariti', '1980-08-29', 1100.00),
    ('Alexandros', 'Hatzis', '1994-03-09', 1000.00),
    ('Konstantinos', 'Poimenidis', '1991-03-28', 999.99);

INSERT INTO trainers (tfname, tlname, tsubj)
VALUES
	('Konstantinos', 'Grigoriou', 'Mathematician'),
    ('Georgia', 'Papanikou', 'Software engineer'),
    ('Vasilis', 'Prodromou', 'Front end developer'),
    ('Giannis', 'Pappas', 'Back end developer'),
    ('Niki', 'Stamatiadi', 'Graphics Designer');
    
INSERT INTO courses (ctitle, cstream, ctype, csdate, cedate)
VALUES
	('Programming 101', 'Java', 'full-time', '2019-10-14', '2019-12-13'),
    ('Intro to OOP', 'C#', 'full-time', '2019-10-21', '2019-12-27'),
    ('HTML and CSS', 'Nodejs', 'summer-class', '2020-07-06', '2020-09-04');
    
INSERT INTO assignments (atitle, adesc, asubdate, aomark, atmark)
VALUES
	('Project 1', 'Create a weather app', '2019-11-27', 30, 100),
    ('Project 2', 'Create a Tic-Tac-Toe game', '2019-11-28', 10, 20),
    ('Project 3', 'Create a hello world Reactjs application', '2019-12-03', 3, 10),
    ('Project 4', 'Create a scientific calculator', '2020-09-04', 100, 100),
    ('Project 5', 'Create a weather app', '2020-09-01', 0, 100);
    
    
# POPULATING JOINT TABLES

INSERT INTO studentcourse(cid,sid)
VALUES
	(1, 1), (2, 2), (3, 3), (1, 4), (2, 5), (3, 6), (1, 7), (2, 8), (3, 9), (1, 10),
	(2, 1), (3, 2), (1, 3), (2, 4), (3, 5), (1, 6), (2, 7), (3, 8), (1, 9), (2, 10), (3, 11);

INSERT INTO trainercourse(cid,tid)
VALUES
	(1, 1), (2, 2), (3, 3), (1, 4), (2, 5), (3, 1), (1, 2), (2, 3), (3, 4), (1, 5);
    
INSERT INTO assignmentcourse(cid, aid)
VALUES
	(1, 1), (1, 2), (1, 3), (2, 1), (2, 2), (2, 3), (3, 4), (3, 5);

# assignment per course per student table is populated automatically
# using AFTER INSERT triggers on studentcourse and assignmentcourse tables.
# default values are set for the fields oral mark, total mark and submission date.