/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package TCP;

/**
 *
 * @author nsth
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import serializer.Serializer;

import models.Task;

public class TCPClient {
	
	public static void main(String args[]) {
		
		try {
            // IP address of the server,
            InetAddress serverAddress = InetAddress.getByName("localhost");            
            // Open a socket for communication.
            Socket socket = new Socket(serverAddress, 7896);
            
            // Create a data output stream (for sending messages)
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Client is connecting to server...");
            // Create Datainputstreeam (for receiving messages from server.
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            // Create an objectinputstream ( for receiving objects
         	ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()); 
            // Create ObjectOutputStream
         	ObjectOutputStream ous = new ObjectOutputStream(socket.getOutputStream());
         	
         	
         	//__________________________________________________________________________________
            String message = "GET";
            dos.writeUTF(message);
            System.out.println("Client: requesting " + message);
            
            // Blocking call,  
            String response = dis.readUTF();
                        
            System.out.println("Client: server response : " + response);         
            
            if(response.equals(message) ){
            	// request a Task object
	            String id = "approve-01";
	        	dos.writeUTF(id);
	        	System.out.println("Client: requesting Task object");

	        	Task t = (Task) ois.readObject(); // BLOCKING ?
	        	System.out.println("Client: Object received '" + t.name + "'");
            }else{
            	System.out.println("Client: the server did not acknowledge the request. Aborting");
            	
            }        
            
         	//__________________________________________________________________________________
            message = "POST";			// message
        	System.out.println("Client: requesting " + message);

            dos.writeUTF(message);		// send it
            response = dis.readUTF();	// BLOCKING           
            
        	Task t = new Task();
        	t.name = "Tut les circles";
        	t.id = "one more cup of coffe";
        	t.date = "15-09-2013";
        	t.description = "recondre";
        	t.status = "mais jai le plus grande maillot du monde";
        	t.attendants = "bjarne, lise, hans, jimmy";
        
            if(response.equals(message)){
            	System.out.println("Client: server accepted. Sending object");
            	ous.writeObject(t);
            }else{
            	System.out.println("Client: the server did not acknowledge the object. Aborting.");
            }
            
            response = dis.readUTF();
        	System.out.println("Client: server response : " + response);
            
        	// __________________________________________________________________________________
        	message = "PUT";
        	System.out.println("Client: requesting " + message);

            dos.writeUTF(message);		// send message
            response = dis.readUTF();	// BLOCKING           
        	
            t.description = "recondre les roix";
            
            if(response.equals(message)){
            	System.out.println("Client: server accepted. Sending object");
            	ous.writeObject(t);
            }else{
            	System.out.println("Client: the server did not acknowledge the object. Aborting.");
            }
            
            response = dis.readUTF();
        	System.out.println("Client: server response : " + response);
            
        	// ___________________________________________________________________________________
        	message = "DELETE";
        	System.out.println("Client: requesting " + message);

            dos.writeUTF(message);		// send it
            response = dis.readUTF();	// BLOCKING           
            
            if(response.equals(message)){
            	System.out.println("Client: server accepted. Sending parameter");
            	dos.writeUTF("Task 246.789.87");
            }else{
            	System.out.println("Client: the server did not acknowledge the request. Aborting.");
            }
            
            response = dis.readUTF();
        	System.out.println("Client: server response : " + response);

        	// ___________________________________________________________________________________
         	// QUIT. Note: the client should not close the connection. The server does that
        	dos.writeUTF("quit");
        	System.out.println("Client signed out.");
            
            
        } catch (IOException ex) {
            Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, ex);
            
            System.out.println("error message: " + ex.getMessage());
        } catch(ClassNotFoundException e){
        	Logger.getLogger(TCPClient.class.getName()).log(Level.SEVERE, null, e);
            
            System.out.println("error message: " + e.getMessage());
        } 
        
	}
}