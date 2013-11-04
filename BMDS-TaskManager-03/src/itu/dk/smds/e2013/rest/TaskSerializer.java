package itu.dk.smds.e2013.rest;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

import java.io.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@XmlRootElement(name = "cal")
public class TaskSerializer
{
	@XmlElementWrapper(name = "tasks")
	@XmlElement(name = "task")
	private List<Task> tasks;
	
	private TaskSerializer(List<Task> tasks)
	{ this.tasks = tasks; }
	
	private TaskSerializer(){}
	
	public static List<Task> readFromXml() throws IOException
	{
		Object object = new Object();
		InputStream stream = object.getClass().getResourceAsStream("WEB-INF/lib/task-manager-xml.xml");
		if(stream == null)
			throw new IllegalArgumentException("null stream");
		TaskSerializer cal;
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(TaskSerializer.class);
			cal = (TaskSerializer)jaxbContext.createUnmarshaller().unmarshal(stream);
		}
		catch(JAXBException ex)
		{ throw new IOException("Problem deserializing from stream", ex); }
		return cal.tasks;
	}
	
	public static void writeToXml(List<Task> tasks) throws IOException
	{
		try
		{
			OutputStream stream = new FileOutputStream("WEB-INF/lib/task-manager-xml.xml");
			TaskSerializer cal = new TaskSerializer(tasks);
			JAXBContext jaxbContext = JAXBContext.newInstance(TaskSerializer.class);
			jaxbContext.createMarshaller().marshal(cal, stream);
		}
		catch(JAXBException ex)
		{ throw new IOException("Problem serializing to stream", ex); }
		
	}
}
