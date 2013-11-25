package serializer;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.AllPermission;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import models.Task;
import models.TaskManager;

public class Serializer {

	public TaskManager allTasks;
	
//	public static void main(String[] args)
//	{
//		Serializer s = new Serializer();
//		
//		try {
//			s.DeSerialize();
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		
//		Tasks tasks = s.allTasks;
//		System.out.println("There are: " + tasks.tasks.size() + " tasks");
//		
//		for(Task t : tasks.tasks){
//			System.out.println(t.name);
//		}
		
//		Task t1 = new Task();
//		t1.name = "I just added a new task";
//		t1.id = "id";
//		t1.date = "14-09-2013";
//		t1.description = "Test task";
//		t1.status = "Not done yet";
//		t1.attendants = "All my good friends";
//		
//		s.allTasks.tasks.add(t1);
//		s.Serialize();
		

//		System.out.println(System.getProperty("java.class.path")); 
//		
//		try {
//			System.in.read();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
		
	public void Serialize()
	{
		try{
			
	        StringWriter writer = new StringWriter();
	        
            // create an instance context class, to serialize/deserialize.
            JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);
            // a marshaller to convert objects into a XML tree. Takes a Task instance and a string writer
	        jaxbContext.createMarshaller().marshal(allTasks, writer); 
		        
	        //System.out.println("\nPrinting serialized TaskManager Xml before saving into file!");
	        //System.out.println(writer.toString());       
	        
	        // lock object?
	        Object obj = new Object();        
	        
	        // Get path to the TaskManager-xml.xml using relative path.
        	String path = obj.getClass().getResource("/resources/task-manager-xml.xml").getPath();
        		        
	        // Finally save the Xml back to the file.
	        SaveFile(writer.toString(), path);
	
		} catch (JAXBException ex) {
            Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex){
        	Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
        }	
		
	}
	
	public void DeSerialize() throws UnsupportedEncodingException
	{
        try {
        	// A Lock object?
        	Object obj = new Object();
        	        	
        	InputStream stream = obj.getClass().getResourceAsStream("/resources/task-manager-xml.xml");
        	        	
            // create an instance context class, to serialize/deserialize.
            JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);

            // deserialize task-manager xml into java objects.
            allTasks = (TaskManager) jaxbContext.createUnmarshaller().unmarshal(stream);            
                 
        } catch (JAXBException ex) {
            Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

	
	
	
	
	
	
	
	
	
	public void DeleteTask(String id){
	    for(int i = 0; i <= (allTasks.tasks.size()-1); i++)
	    {
	    	if(allTasks.tasks.get(i).id.equals(id)){
	    		allTasks.tasks.remove(i);
	    	}
	    }		
	}
	
	public void DeleteTask(Task t){
		allTasks.tasks.remove(t);
	}
	
	public void updateTask(Task t){
		for(int i = 0; i <= allTasks.tasks.size()-1; i++){
			if(allTasks.tasks.get(i).equals(t)){
				DeleteTask(allTasks.tasks.get(i));
				allTasks.tasks.add(t);
			}
		}
	}
	
	public List<Task> GetAllTasks()
	{
		return allTasks.tasks;	
	} 

	public Task GetTask(String id) {
		
		Task task = null;
	    for(int i = 0; i <= (allTasks.tasks.size()-1); i++)
	    {
	    	// System.out.println(allTasks.tasks.get(i).id);
	    	
	    	if(allTasks.tasks.get(i).id.equals(id)){
	            task = allTasks.tasks.get(i);
	            
	    	}
//	    	else{
//	    		throw new TaskNotFoundException("Task with id: " + id + " not found");
//		    }
	    }
	    return task;
 	}
	
	// Custom exception
	public class TaskNotFoundException extends Exception {

		  public TaskNotFoundException(String message){
		     super(message);
		  }
	}

	
    private void PrintTasksObject(Task task) {

        try {
            StringWriter writer = new StringWriter();
            // create a context object for Task Class
            JAXBContext jaxbContext = JAXBContext.newInstance(Task.class);

            // Call marshal method to serialize Task object into Xml
            jaxbContext.createMarshaller().marshal(task, writer);

            System.out.println(writer.toString());

        } catch (JAXBException ex) {
            Logger.getLogger(Serializer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
    private void SaveFile(String xml, String path) throws IOException {
        File file = new File(path);
        // create a bufferedwriter to write Xml
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(xml);
        output.close();
    }

}
