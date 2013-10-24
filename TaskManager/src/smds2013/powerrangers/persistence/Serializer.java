package smds2013.powerrangers.persistence;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

public class Serializer {

	
    public static List<Task> DeserializeTaskCollection(String taskListXml) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(TaskCollection.class);
        StringReader reader = new StringReader(taskListXml);
        // un-marshall
        List<Task> tasklist = ((TaskCollection) jaxbContext.createUnmarshaller().unmarshal(reader)).tasks;
        return tasklist;
    }

    // we may not want this to return a anything?
    public static String SerializeTaskCollection(List<Task> taskcollection) throws JAXBException {

        JAXBContext jaxbContext = JAXBContext.newInstance(TaskCollection.class);
        TaskCollection tasks = new TaskCollection(taskcollection);
        StringWriter writer = new StringWriter();
        // marshall
        jaxbContext.createMarshaller().marshal(tasks, writer);
        return writer.toString();
    }
    
    public static void main(String[] args)
    {
    	List<Task> tasks = new ArrayList<Task>();
    	Task t1 = new Task();
    	t1.id = "45";
    	t1.name = "feed the dog";
    	t1.date = "today";
    	t1.required = false;
    	t1.description = "you need to feed the dog";
    	t1.status = "not done";
    	
    	t1.roles = new ArrayList<String>();
    	t1.roles.add("manager");
    	t1.roles.add("moderator");
    	t1.roles.add("user");
    	
    	t1.attendants = new ArrayList<String>();
    	t1.attendants.add("jim");
    	t1.attendants.add("jon");
    	t1.attendants.add("joe");
    	tasks.add(t1);
    	
    	Task t2 = new Task();
    	t2.id = "12";
    	t2.name = "jump into the air";
    	t2.date = "tomorrow";
    	t2.required = true;
    	t2.description = "dont come down again. stay up there.";
    	t2.status = "not done";
    	
    	t2.roles = new ArrayList<String>();
    	t2.roles.add("jumper");
    	
    	t2.attendants = new ArrayList<String>();
    	t2.attendants.add("ben");
    	t2.attendants.add("bill");
    	tasks.add(t2);
    	
    	try
    	{
    		System.out.println(SerializeTaskCollection(tasks));
    	}
    	catch(JAXBException ex)
    	{
    		System.out.println(ex);
    	}
    }
}

