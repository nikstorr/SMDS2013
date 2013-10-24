package smds2013.powerrangers.persistence;


import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "tasks")
public class TaskCollection implements Serializable {

//    @XmlElementWrapper(name = "users")
//    @XmlElement(name = "user")
//    public List<User> users;
    
    @XmlElementWrapper(name = "tasks")    
    @XmlElement(name = "task")
    public List<Task> tasks;
}



