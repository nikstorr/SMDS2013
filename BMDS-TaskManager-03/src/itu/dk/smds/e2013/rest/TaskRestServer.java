package itu.dk.smds.e2013.rest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import javax.xml.bind.JAXBElement;

@Path("/tasks")
public class TaskRestServer
{
	@GET
	@Path("get")
	@Produces(MediaType.TEXT_HTML)
	public String HtmlGet()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><title>get</title><body>Available tasks:\n");
		
		List<Task> tasks = readObjects();
		for(Task t : tasks)
			builder.append(t);
		if(tasks.isEmpty())
			builder.append("No tasks to list");
		
		builder.append("</body></html>");
		return builder.toString();
	}
	
	@POST
	@Path("post")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String HtmlPost(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("date") String date,
			@FormParam("status") String status,
			@FormParam("required") String required,
			@FormParam("description") String description,
			@FormParam("attendants") String attendants)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><title>post</title><body>posted task:\n");
		
		Task t = new Task(id, name, date, status, 
				required == "true" ? true : false, description, attendants);
		
		List<Task> tasks = readObjects();
		tasks.add(t);
		writeObjects(tasks);
		
		builder.append(t);
		
		builder.append("</body></html>");
		return builder.toString();
	}
	
	@PUT
	@Path("put")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String HtmlPut(@FormParam("id") String id,
			@FormParam("name") String name,
			@FormParam("date") String date,
			@FormParam("status") String status,
			@FormParam("required") String required,
			@FormParam("description") String description,
			@FormParam("attendants") String attendants)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><title>post</title><body>put task:\n");
		
		Task t = new Task(id, name, date, status, 
				required == "true" ? true : false, description, attendants);
		
		List<Task> tasks = readObjects();
		for(int i = 0; i < tasks.size(); i++)
		{
			Task ta = tasks.get(i);
			if(ta.getId() == t.getId())
			{
				tasks.remove(i);
				tasks.add(t);
				break;
			}
		}
		
		builder.append(t);
		
		builder.append("</body></html>");
		return builder.toString();
	}

	@DELETE
	@Path("delete")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String HtmlDelete(@FormParam("id") String id)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<html><title>post</title><body>deleted task:\n");
		
		List<Task> tasks = readObjects();
		for(int i = 0; i < tasks.size(); i++)
		{
			Task ta = tasks.get(i);
			if(ta.getId() == id)
			{
				builder.append(tasks.get(i));
				tasks.remove(i);
				break;
			}
		}
		
		builder.append("</body></html>");
		return builder.toString();
	}
	
	private static List<Task> readObjects()
	{
		
		List<Task> tasks;
		try
		{
			tasks = TaskSerializer.readFromXml();
		}
		catch(IOException | IllegalArgumentException ex)
		{
			System.out.println("Server exception " + ex);
			return new ArrayList<Task>();
		}
		return tasks;
	}
	
	private static void writeObjects(List<Task> tasks)
	{
		try
		{
			TaskSerializer.writeToXml(tasks);
		}
		catch(IOException | IllegalArgumentException ex)
		{
			System.out.println("Server exception " + ex);
		}
	}
}
