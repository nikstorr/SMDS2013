package models;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "task")
public class Task implements Serializable{
	
	
	public Task(){}
	
	@XmlAttribute
	public String id;
	@XmlAttribute
	public String name;
	@XmlAttribute
	public String date;
	@XmlAttribute
    public String status;
	
	public String description;
	public String attendants;
}
