package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import models.Task;

@XmlRootElement(name = "taskmanager")
public class TaskManager implements Serializable{

	@XmlElementWrapper(name = "tasks")
    @XmlElement(name = "task")
    public List<Task> tasks;
}
