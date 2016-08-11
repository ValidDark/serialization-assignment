package com.cooksys.serialization.assignment;

import com.cooksys.serialization.assignment.model.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main
{

	/**
	 * Creates a {@link Student} object using the given studentContactFile. The
	 * studentContactFile should be an XML file containing the marshaled form of
	 * a {@link Contact} object.
	 *
	 * @param studentContactFile
	 *            the XML file to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a {@link Student} object built using the {@link Contact} data in
	 *         the given file
	 * @throws JAXBException
	 */

	public static Student readStudent(File studentContactFile, JAXBContext jaxb)
	{
		Contact stuCon = new Contact();
		try
		{
			stuCon = (Contact) jaxb.createUnmarshaller().unmarshal(studentContactFile);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		Student stu = new Student();
		stu.setContact(stuCon);
		return stu;
	}

	/**
	 * Creates a list of {@link Student} objects using the given directory of
	 * student contact files.
	 *
	 * @param studentDirectory
	 *            the directory of student contact files to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a list of {@link Student} objects built using the contact files
	 *         in the given directory
	 * @throws JAXBException
	 */
	public static List<Student> readStudents(File studentDirectory, JAXBContext jaxb)
	{
		List<Student> stuList = new ArrayList<>();

		File[] listOfFiles = studentDirectory.listFiles();

		for (int i = 0; i < listOfFiles.length; i++)
		{
			if (listOfFiles[i].isFile())
			{
				File stuFile = new File(studentDirectory + "/" + listOfFiles[i].getName());
				stuList.add(readStudent(stuFile, jaxb));
			}
		}
		return stuList;
	}

	/**
	 * Creates an {@link Instructor} object using the given
	 * instructorContactFile. The instructorContactFile should be an XML file
	 * containing the marshaled form of a {@link Contact} object.
	 *
	 * @param instructorContactFile
	 *            the XML file to use
	 * @param jaxb
	 *            the JAXB context to use
	 * @return an {@link Instructor} object built using the {@link Contact} data
	 *         in the given file
	 * @throws JAXBException
	 */
	public static Instructor readInstructor(File instructorContactFile, JAXBContext jaxb)
	{
		Instructor ins = new Instructor();
		Contact insCon = new Contact();
		try
		{
			insCon = (Contact) jaxb.createUnmarshaller().unmarshal(instructorContactFile);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}

		ins.setContact(insCon);

		return ins;
	}

	/**
	 * Creates a {@link Session} object using the given rootDirectory. A
	 * {@link Session} root directory is named after the location of the
	 * {@link Session}, and contains a directory named after the start date of
	 * the {@link Session}. The start date directory in turn contains a
	 * directory named `students`, which contains contact files for the students
	 * in the session. The start date directory also contains an instructor
	 * contact file named `instructor.xml`.
	 *
	 * @param rootDirectory
	 *            the root directory of the session data, named after the
	 *            session location
	 * @param jaxb
	 *            the JAXB context to use
	 * @return a {@link Session} object built from the data in the given
	 *         directory
	 * @throws JAXBException
	 */
	public static Session readSession(File rootDirectory, JAXBContext jaxb)
	{
		JAXBContext con = null;
		try
		{
			con = JAXBContext.newInstance(Contact.class);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
		Session sess = new Session();
		File[] listOfFiles;

		if (rootDirectory.isDirectory())
		{
			sess.setLocation(rootDirectory.getName());
			listOfFiles = rootDirectory.listFiles();

			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isDirectory())
				{
					sess.setStartDate(listOfFiles[i].getName());
					rootDirectory = listOfFiles[i];
				}
			}
			listOfFiles = rootDirectory.listFiles();

			for (int i = 0; i < listOfFiles.length; i++)
			{
				if (listOfFiles[i].isFile())
				{
					File ins = new File(rootDirectory + "\\" + listOfFiles[i].getName());
					sess.setInstructor(readInstructor(ins, con));
					rootDirectory = new File(rootDirectory + "\\students\\");
				}
			}
			sess.setStudents(readStudents(rootDirectory, con));
		}
//		System.out.println(sess);
		return sess;
	}

	/**
	 * Writes a given session to a given XML file
	 *
	 * @param session
	 *            the session to write to the given file
	 * @param sessionFile
	 *            the file to which the session is to be written
	 * @param jaxb
	 *            the JAXB context to use
	 * @throws JAXBException
	 */
	public static void writeSession(Session session, File sessionFile, JAXBContext jaxb)
	{
		try
		{
			jaxb.createMarshaller().marshal(session, sessionFile);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Main Method Execution Steps: 1. Configure JAXB for the classes in the
	 * com.cooksys.serialization.assignment.model package 2. Read a session
	 * object from the <project-root>/input/memphis/ directory using the methods
	 * defined above 3. Write the session object to the
	 * <project-root>/output/session.xml file.
	 *
	 * JAXB Annotations and Configuration: You will have to add JAXB annotations
	 * to the classes in the com.cooksys.serialization.assignment.model package
	 *
	 * Check the XML files in the <project-root>/input/ directory to determine
	 * how to configure the {@link Contact} JAXB annotations
	 *
	 * The {@link Session} object should marshal to look like the following:
	 * <session location="..." start-date="..."> <instructor> <contact>...
	 * </contact> </instructor> <students> ...
	 * <student> <contact>...</contact> </student> ... </students> </session>
	 * 
	 * @throws JAXBException
	 */
	public static void main(String[] args)
	{
		JAXBContext sesContext;
		try
		{
			sesContext = JAXBContext.newInstance(Session.class);
			writeSession(readSession(new File("input/memphis"), sesContext), new File("output/session.xml"), sesContext);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
}