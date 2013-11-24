package com.example.testmobiletaskclient;

import javax.persistence.*;

@Entity
public class Task
{
	@Id
	private String id;
	
	private String name, date, status;
	private boolean required;
	
	public Task(String Id, String Name, String Date, String Status, boolean Required)
	{
		id = Id;
		name = Name;
		date = Date;
		status = Status;
		required = Required;
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
}