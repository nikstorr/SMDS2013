package itu.dk.smds.e2013.rest;

import java.io.*;
import java.util.Arrays;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "task")
public class Task implements Serializable
{
	static final long serialVersionUID = 8752384l;
	
	@XmlAttribute
	private String id, name;
	
	@XmlAttribute
	private String date;
	
	@XmlAttribute
	private String status;
	
	@XmlAttribute
	private boolean required;
	
	@XmlElement
	private String description, attendants;
	
	private Task(){}
	
	public Task(String id, String name, String date, String status, boolean required, String description, String attendants)
	{
		this.id = id;
		this.name = name;
		this.date = date;
		this.status = status;
		this.required = required;
		this.description = description;
		this.attendants = attendants;
	}
	
	public String getId()
	{ return id; }
	
	public String getName()
	{ return name; }
	
	public String getDate()
	{ return date; }
	
	public String getStatus()
	{ return status; }
	
	public boolean getRequired()
	{ return required; }
	
	public String getDescription()
	{ return description; }
	
	public String getAttendants()
	{ return attendants; }
	
	@Override
	public String toString()
	{
		return String.format("id=%s\nname='%s'\ndate=%s\nstatus='%s'\nrequired=%b\n" + 
				"description\n   %s\nattendants\n   %s\n", 
				id, name, date, status.toString(), required, description, attendants);	
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof Task))
			return false;
		Task t = (Task)o;
		return t.getId().equals(id) && t.getName().equals(name);
	}
}
 