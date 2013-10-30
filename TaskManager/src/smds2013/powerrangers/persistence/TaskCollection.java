package smds2013.powerrangers.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "taskmanager")
public class TaskCollection implements Serializable {
    
	private static final long serialVersionUID = 1L;
	@XmlElementWrapper(name = "tasks")    
    @XmlElement(name = "task")
    public List<Task> tasks;
    
    public TaskCollection()
    {
    	tasks = new ArrayList<Task>();
    }
    
    public TaskCollection(List<Task> list) {
        this.tasks = list;
    }
}



