package smds2013.powerrangers.persistence;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "task")
public class Task implements Serializable {
	
    @XmlID
    @XmlAttribute
    public String id;
    
    @XmlAttribute
    public String name;
    @XmlAttribute
    public String date;
    @XmlAttribute
    public String status;
    @XmlAttribute
    public Boolean required;
    
    @XmlElement
    public String description;

    @XmlElementWrapper(name= "attendants")
    @XmlElement(name="attendant")
    public List<String> attendants;
    
    @XmlElementWrapper(name="roles")
    @XmlElement(name="role")
    public List<String> roles;
}



