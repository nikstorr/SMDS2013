/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security.server;

import dk.itu.smds.e2013.security.DESEncryptionHelper;
import dk.itu.smds.e2013.security.Utilities;
import dk.itu.smds.e2013.serialization.common.Task;
import dk.itu.smds.e2013.serialization.common.TaskManager;
import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.JAXBContext;

/**
 *
 * @author rao
 */
public class TaskManagerServer {

	// shared key (tokenserver and server)
    private static String Server_Key_Passcode = "Don't reveal the Secret";
    // shared key (client and server)
    private static String Client_Service_key_Passcode = "yeah man n all dat";
    
    private static int serverPort = 8010;
    
    private static final String Encoding_Format = "UTF8";
    private static BufferedReader in;
    
    private static ServerSocket serverSocket = null;
    private static InetAddress serverAddress;
    
    private static DataInputStream dis;
    private static DataOutputStream dos;
    private static Socket socket = null;
    
    private static ObjectOutputStream oos;

    private static String[] roleMap = {"nsth"};
    
    // timestamp format
    private static final SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static int nonce;
        
    public static void main(String args[]) throws Exception {
    	serverAddress = InetAddress.getByName("localhost");
    	
//    	roleMap[0] = "nsth";
    	    	
        // hook on to console input ..
        in = new BufferedReader(new InputStreamReader(System.in));

        // read tasks from storage
        FileInputStream stream = new FileInputStream("src/resources/task-manager-revised.xml");
        JAXBContext jaxbContext = JAXBContext.newInstance(TaskManager.class);
        
        // de-serialize tasks
        TaskManager taskManager = (TaskManager) jaxbContext.createUnmarshaller().unmarshal(stream);

        System.out.println("Taskmanager loaded with :" + taskManager.tasks.size() + " tasks!");

        try {
            serverSocket = new ServerSocket(serverPort);
            System.out.println("Server started at: " + serverPort);

            while (true) {
            	
                socket = serverSocket.accept(); // blocking call
                // create input channel
                dis = new DataInputStream(socket.getInputStream());
                // wait for input
                String request = dis.readUTF(); // blocking call
                System.out.println("Received client Request: " + request);

                String serverTokenPlain;

                try {
                	// decrypt token  {role,timestamp,principle,clientkey}Kst
                    serverTokenPlain = decryptServerToken(request);
                    
                } catch (Exception ex) {

                    System.out.println(ex.getMessage());
                    writeToClient("Failed to decrypt server token! Your request can not be processed!");
                    continue;
                }

                // authenticate
                String[] tokenArray = serverTokenPlain.split(";");
                System.out.println("Request decrypted: role[" +tokenArray[0]+"] timestamp[" + tokenArray[1]+ "] principle[" +tokenArray[2]+"] sessionKey["+tokenArray[3] +"]");
                
                // authenticate client_____________________________________________________________
                String response;
                if(authenticate(tokenArray))
                {
                    response = encryptNonseResponse("yes");
                }else{
                    response = encryptNonseResponse("no");
                }
                                
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF(response);
                dos.flush();                              
                              
                // Get client's execute command ___________________________________________________________
                request = dis.readUTF(); // blocking call
                System.out.println("Received client Request: " + request);
                
                // split command into a nonce and a task id
                String[] execute = validateTaskCommand(request);
     
                String alteredNonce = execute[0];
                
                if(Integer.parseInt(alteredNonce)-1 == nonce){
                	System.out.println("THE ALTERED NONCE IS CORRECT");
                }
                
                String taskid = execute[1];
                
                Task requestedtask = GetTask(taskManager, taskid);

                if (requestedtask == null) {
                    writeToClient("Task with Id:" + taskid + " can not be found in task manager!");
                    continue;
                }

                //if (!requestedtask.role.contains(tokenArray[0])) {
                if (!matchRolemappings(requestedtask.role, tokenArray[0])) {
                    writeToClient("The client is not authorized to execute task with Id:" + taskid + " due to role mismatch!");
                    continue;
                }

                // Finally, if everything goes well update the task.
                requestedtask.status = "executed";
                
                System.out.println("The task with Id:" + taskid + " executed successfully!");
                
                // writeToClient("The task with Id:" + taskid + " executed successfully!");
                System.in.read();
                
            }

        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

    // the altered nonce and a task id from the client
    private static String[] validateTaskCommand(String command) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
    	
    	// decrypt message ____________________________________________________
        byte[] base64DecodedBytes = Utilities.getBase64DecodedBytes(command);
        
        // Get the decrypted server token bytes.
        byte[] decryptedMessage = DESEncryptionHelper.decryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
                base64DecodedBytes);
        
        // Convert these bytes to String to get server token in base64 encoding.
        String message64format = Utilities.bytes2String(decryptedMessage);
        
        String[] request = message64format.split(",");
        
        // decrypt nonce _______________________________________________________
        byte[] nonceBase64DecodedBytes = Utilities.getBase64DecodedBytes(request[0]);
        
        // Get the decrypted server token bytes.
        byte[] decryptedNonce = DESEncryptionHelper.decryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
                nonceBase64DecodedBytes);
        
        // Convert these bytes to String to get server token in base64 encoding.
        String nonce64format = Utilities.bytes2String(decryptedNonce);
              
        
        // decrypt task id _____________________________________________________
        byte[] taskBase64DecodedBytes = Utilities.getBase64DecodedBytes(request[1]);
        
        // Get the decrypted server token bytes.
        byte[] decryptedTask = DESEncryptionHelper.decryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
                taskBase64DecodedBytes);
        
        // Convert these bytes to String to get server token in base64 encoding.
        String task64format = Utilities.bytes2String(decryptedTask);
        
        // ____________________________________________________________________
        
        String[] output = {nonce64format, task64format};
        System.out.println("altered nonce: " + nonce64format + ", task id: " + task64format);
        return output;
    }
    
    
    
    private static String encryptNonseResponse(String reply) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException
    {
    
    	Random ran = new Random();
    	String output;
    	
    	if(reply.equals("no")){
    		output = "not recognized";
    	}else{
	    	// Nonce
        	nonce = ran.nextInt(500) +1;
  			output = Integer.toString(nonce);
    	}
    	    	
    	byte[] encryptNonce = DESEncryptionHelper.encryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
                output.getBytes(Encoding_Format));

    	String out = Utilities.getBase64EncodedString(encryptNonce);
    	System.out.println("The nonce is " +out);
    	
    	return out;
    }
    
    
    
    private static Boolean authenticate(String[] ticket) throws IOException{
    	
        // we expect an array of 4 items
        if (ticket.length != 4) {
            writeToClient("Invalid server token! The Format of server token should be [role];[timestamp];[principle];[key]");
            return false;
        }
        // a timestamp must be valid
        if (!validateTimestamp(ticket[1])) {
            writeToClient("Timestamp for server token expired! The client request can not be processed!");
            return false;
        }
        // compare access-rights table to principle in ticket
        if(!ticket[2].equals(roleMap[0])){
        	writeToClient("Client not found in role map! The client request can not be processed!");
        	return false;
        }

        
        // the session key must be recognized
//        if( Client_Service_key_Passcode.equals(ta[3]))
//        {
//        	writeToClient("session key not recognized! The client request can not be processed");
//        	return false;
//        } 
        
        
        
        // the server should have an access rights table of its own
        // in order to validate a client against a session key.
        // A hashtable or dictionary could keep these session key username pairs.
        
        
        
        
        
        return true;

    }
    
    
    
    private static void writeToClient(String message) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(message);
        dos.flush();
    }

    private static String decryptServerToken(String serverToken) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {
        // transform ANSCII into bytes
        byte[] base64DecodedBytes = Utilities.getBase64DecodedBytes(serverToken);
        // decrypt bytes
        byte[] decryptMessageBytes = DESEncryptionHelper.decryptMessage(Server_Key_Passcode.getBytes(Encoding_Format), base64DecodedBytes);
        // plain text
        String serverTokenPlain = Utilities.bytes2String(decryptMessageBytes);
        
        return serverTokenPlain;
    }

    private static boolean validateTimestamp(String timestamp) {
        try {
            Date expiryDate = formatted.parse(timestamp);
            Date now = Calendar.getInstance().getTime();

            if (expiryDate.compareTo(now) < 0) {
                return false;
            }
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    private static Task GetTask(TaskManager taskManager, String taskid) {
        ListIterator<Task> listIterator = taskManager.tasks.listIterator();

        while (listIterator.hasNext()) {
            Task nextTask = listIterator.next();

            if (nextTask.id.equals(taskid)) {

                return nextTask;
            }
        }
        return null;
    }

    private static String getCurrentDateTime() {

        Date now = Calendar.getInstance().getTime();
        return formatted.format(now);
    }

    private static boolean matchRolemappings(String taskRoles, String userRoles) {
        String[] taskRoleArray = taskRoles.split(",");
        String[] userRoleArray = userRoles.split(",");

        for (int index = 0; index < taskRoleArray.length; index++) {
            for (int index2 = 0; index2 < userRoleArray.length; index2++) {

                if (taskRoleArray[index].trim().equals(userRoleArray[index2].trim())) {
                    return true;
                }
            }
        }
        return false;
    }
}
