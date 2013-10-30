/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import models.Task;

import serializer.Serializer;
import serializer.Serializer.TaskNotFoundException;

/**
 *
 * @author nsth
 */
public class TCPServer {
	
	public static void main(String args[]) throws TaskNotFoundException {
		try {
	        
			// First, we desialize the XML, so all Task objects are present.
			Serializer serializer = new Serializer();
			serializer.DeSerialize();
			
	        ServerSocket serverSocket = new ServerSocket(7896); // port 
	        System.out.println("Server is listening at port 7896");
	        Socket socket = serverSocket.accept();				// wait for client 
	        System.out.println("Server: A client is connected");
	        
	        // Create an inputstream on the socket
	        DataInputStream dis = new DataInputStream(socket.getInputStream());
            // create a dataoutputstream
	        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
    		// create objectoutputstream
	        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        // 
	        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	        //
	        boolean running = true;
            while(running){
            	System.out.println("\nServer: Waiting for client command");
            	String message = dis.readUTF(); // blocking call
    	        System.out.println("Server: client command received : " + message);
            	// accept client request by returning the message
                dos.writeUTF(message);    
                
                //_______________________________________________________________________
                // GET method needs an id parameter to know which Task object to return. SO we wait for that to come from the client
                if(message.equals("GET")){
                	System.out.println("Server: Waiting for parameter");
                    
                	String id = dis.readUTF(); // BLOCKING. waiting for an id
                	
                	System.out.println("Server: parameter  received : '" + id + "'... Looking for object");
                    // get the Task object requested by the client               	                    
                	Task task = serializer.GetTask(id);
                	                	                	
                	if(task != null){
                		System.out.println("...Object found '" + task.name + "'...Sending object");
	                    oos.writeObject(task);
                	}else{
                		dos.writeUTF("No Task with id " + id + " was found");
                	}
                }
                
                //__________________________________________________________________________
                // POST expect an object from the client
                if(message.equals("POST")){
                    
                	Task t = (Task) ois.readObject();
                	
                	System.out.println("Server: object received '" + t.name +"', persisting object...");
                	
                	serializer.allTasks.tasks.add(t);
                	serializer.Serialize();
                	
                	System.out.println("...object persisted");
                    
                	dos.writeUTF("Task saved");
                }
                
                //__________________________________________________________________________
                // Expect an id from clientS
                if(message.equals("DELETE")){
 
                	String id = dis.readUTF();
                	serializer.DeleteTask(id);
                	
                	dos.writeUTF("Task with id " +id+" deleted");
                }
                //__________________________________________________________________________
                // Expecting an object
                if(message.equals("PUT")){
                	Task task = (Task)ois.readObject();
                	serializer.updateTask(task);
                	
                	dos.writeUTF("task with id " +task.id+ " updated");
                }
                // _________________________________________________________________________
                if(message.equals("quit")){
                	System.out.println("Server is closing............................");
                    
                	running = false;
                }
                
            	// Persist after all state changes
            	serializer.Serialize();

        	}
         socket.close(); // Exiting
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error message: " + ex.getMessage());
        } catch (ClassNotFoundException e){
        	Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("error message: " + e.getMessage());
        }
    }
}