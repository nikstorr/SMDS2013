public class TCPServer {
	
	public static void main(String args[]) throws TaskNotFoundException {
		try {
	        Serializer serializer = new Serializer();
	        serializer.DeSerialize();
			
	        ServerSocket serverSocket = new ServerSocket(7896); // port 
	        System.out.println("Server is listening at port 7896");
	        
	        System.out.println("Tasks: " + serializer.allTasks.tasks.size());
	                
	        for(Task t : serializer.allTasks.tasks)
	        {
	        	System.out.println(t.id);
	        }
	        
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
                	   	
                	dos.writeUTF("Task with id: '" +t.id + "' saved");
                }
                
                //__________________________________________________________________________
                // Expect an id from clientS
                if(message.equals("DELETE")){
 
                	String id = dis.readUTF();
                	serializer.DeleteTask(id);
                	serializer.Serialize();
                	
                	dos.writeUTF("Task with id " +id+" deleted");
                }
                
                //__________________________________________________________________________
                // Expecting an object
                if(message.equals("PUT")){
                	
                	Task task = (Task)ois.readObject();
                	
                	serializer.updateTask(task);
                	                	
                	serializer.Serialize();
                	
                	dos.writeUTF("task with id '" +task.id+ "' updated");
                }

                // _________________________________________________________________________
                if(message.equals("quit")){
                	System.out.println("Server is closing............................");
                    
                    socket.close();         // Exiting                	
                    running = false;
                }
                
                System.out.println("...object persisted");
                
        	}
        } catch (IOException ex) {
            Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error message: " + ex.getMessage());
        } catch (ClassNotFoundException e){
        	Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, e);
            System.out.println("error message: " + e.getMessage());
        }
    }
} 