/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security.client;

import dk.itu.smds.e2013.security.DESEncryptionHelper;
import dk.itu.smds.e2013.security.RoleBasedToken;
import dk.itu.smds.e2013.security.Utilities;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author rao
 */
public class TaskManagerClient {

	// shared key (client and tokenservice)
    private static String Client_TokenService_Key_Passcode = "Topmost Secret";
    // shared key (client and server)
    private static String Client_Service_key_Passcode;
    
    private static final String Encoding_Format = "UTF8";
    private static BufferedReader in;

    private static int tokenServerPort = 8008;
    
    private static int clientPort = 8009;
    private static int serverPort = 8010;

    private static InetAddress serverAddress;
    private static Socket socket;

    public static void main(String args[]) throws Exception {
    	
    	serverAddress = InetAddress.getByName("localhost");    
        
        ObjectInputStream ois;    ObjectOutputStream oos;
    
        // hook on to console input ..
        in = new BufferedReader(new InputStreamReader(System.in));
        
        // A ticket to ride ________________________________________________________________
        // Contact the authentication service (the token service) and receive a token for
        // communicating with the server  {{role,timestamp}Kts}Kct
        RoleBasedToken roleBasedToken = getRoleBasedToken();
        
        if (!roleBasedToken.result) { // failed to obtain token

            System.out.println("Client failed to obtain Role based security token. Error message: "
                    + roleBasedToken.errorMessage
                    + " Enter 'try' to try once again or enter 'exit' to stop the client.");

            String option = in.readLine();

            if (option.toLowerCase().equals("try")) {
                roleBasedToken = getRoleBasedToken();
            } else {
                return;
            }

        }
        
        // If we fail to get role token second time also, then exit.
        if (!roleBasedToken.result) {
            System.out.println("Failed to get Role based security token second time also. Error message: "
                    + roleBasedToken.errorMessage
                    + "The client program will be stopped!");
            
            return;
        }

        // else...
        // decrypt the ticket _____________________________________________________________
        // extract the server token. An encrypted role-based token plus a session key, 
        // for communication with the task server.
        
        String serverToken64format;
        
        try {
        	// the server ticket is {{role,timestamp,principle, Ksc}Ktc, Ksc}Ktc 
        	// first we split the ticket into an encrypted token and a session key: [ {role,timestamp,principle,Ksc}Ktc ] and [ Ksc ]
        	// We send the ticket to the server and...   we keep the session key!
        	serverToken64format = getServerToken(roleBasedToken.token);
        	
        } catch (Exception ex) {
        	// 
            System.out.println("Failed to decrypt the token received from token service and server token could not be extracted!. Error message: "
                    + ex.getMessage()
                    + " The client program will be stopped!");
            return;
        }

        System.out.println("Server token and key extracted successfully from ticket: " + serverToken64format);
        System.out.println("session key is: " + roleBasedToken.key);
        System.out.println("Token is: " + roleBasedToken.token);
                
        // remember the session key, (well, it is in the RoleBasedToken)
        Client_Service_key_Passcode = roleBasedToken.key;
                       
        // now that we got a token for communicating with the task server. __________________________
        while (true) {

            System.out.println("Contacting task server...");
            String token = serverToken64format; // token is still encrypted

            // open a stream to the task server
            Socket serversocket = new Socket(serverAddress, serverPort);
            // send the encrypted token 
            DataOutputStream dos = new DataOutputStream(serversocket.getOutputStream());
            dos.writeUTF(token);
            dos.flush();
            
            // task server got the token and we expect a reply in the shape of a nonce. 
            DataInputStream dis = new DataInputStream(serversocket.getInputStream());
            String reply =  dis.readUTF(); // blocking call
            
            System.out.println("Message received from task server: " + reply);
            System.out.println("Decrypted message from task server ( nonce ): " + decryptServerReply(reply));
            
            String nonce = decryptServerReply(reply);
            
            if(nonce.equals("not recognized"))
            {
            	System.out.println("(the nonce) reply from task server: " + nonce);
            	// try again
            	dos.writeUTF(token);
            	dos.flush();
            }
            
            // ... success. A nonce received. We can return a resource command:   [altered nonce , task id}
            
            // alter nonce
        	int nonceresponce = Integer.parseInt(nonce) -1;
        	String N = Integer.toString(nonceresponce);
            
        	//String encryptedTaskCommand = command(encryptNonce(nonce), encryptTask("qualify-for-examine"));
                    	
        	String encryptedTaskCommand = encrypt( encrypt(N) +","+ encrypt("qualify-for-examine") );
                        
            dos.writeUTF(encryptedTaskCommand);
            dos.flush();      
            
            serversocket.close();
            System.in.read();
        }
    }
    
//    private static String command(String encryptedNonce, String encryptedTask) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
//    	
//    	String output = encryptedNonce +","+ encryptedTask;
//    	
//    	// First encrypt the username token with encryption key shared between Client and token service
//        byte[] encryptTokenBytes = DESEncryptionHelper.encryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
//                output.getBytes(Encoding_Format));
//        
//        return Utilities.getBase64EncodedString(encryptTokenBytes);
//    }
//    
//    // a nonce and a task id
//    private static String encryptTask(String id) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
//    	
//    	String output = id;
//    	// First encrypt the username token with encryption key shared between Client and token service
//        byte[] encryptTokenBytes = DESEncryptionHelper.encryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
//                output.getBytes(Encoding_Format));
//        
//        return Utilities.getBase64EncodedString(encryptTokenBytes);
//    }
//    
//    private static String encryptNonce(String nonce) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
//    	
//    	int nonceresponce = Integer.parseInt(nonce) -1;
//    	String N = Integer.toString(nonceresponce);
//    	
//        // First encrypt the username token with encryption key shared between Client and token service
//        byte[] encryptTokenBytes = DESEncryptionHelper.encryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
//                N.getBytes(Encoding_Format));
//        
//        return Utilities.getBase64EncodedString(encryptTokenBytes);
//    }

      
    // generic encrypter
    private static String encrypt(String input) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException{
    	    	
        // First encrypt the username token with encryption key shared between Client and token service
        byte[] encryptTokenBytes = DESEncryptionHelper.encryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
                input.getBytes(Encoding_Format));
        
        return Utilities.getBase64EncodedString(encryptTokenBytes);
    }
    
      
    
    
    
    
    
    
    // Decrypt the ticket for communicating with the server {role,timestamp,principle,Ksc}Kst
    // and keep the key
    private static String getServerToken(String tokenFromTokenService) throws IOException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException {

        // decrypt the token from token server.
        byte[] base64DecodedBytes = Utilities.getBase64DecodedBytes(tokenFromTokenService);
        
        // Get the decrypted server token bytes.
        byte[] decryptedBytes = DESEncryptionHelper.decryptMessage(Client_TokenService_Key_Passcode.getBytes(Encoding_Format),
                base64DecodedBytes);
        
        // Convert these bytes to String to get server token in base64 encoding.
        String serverToken64format = Utilities.bytes2String(decryptedBytes);
        
        return serverToken64format;
    }
    
    
    private static String decryptServerReply(String message) throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException, IOException
    {
        byte[] base64DecodedBytes = Utilities.getBase64DecodedBytes(message);
        
        byte[] decryptedBytes = DESEncryptionHelper.decryptMessage(Client_Service_key_Passcode.getBytes(Encoding_Format),
                base64DecodedBytes);
        
        String serverReply64format = Utilities.bytes2String(decryptedBytes);
        
        return serverReply64format;
    }

    // in order to obtain a role-based token from the token-service we sent our credentials to the token-service
    // encrypted with a shared key {credentials}Kct . THe token-service then decrypts the message and authenticates our credentials (using the ITU authentication srvice). 
    // Then the token-service sends back a role-based token encrypted in the same shared key. 
    // We have that key and we can decrypt the message to get to the role-based token {role, timestamp}Kst
    // which we use to communicate with the server
    private static RoleBasedToken getRoleBasedToken() {
    	// new token
        RoleBasedToken roleTokenObj = new RoleBasedToken();

        try {
            System.out.println("Please enter your ITU username");
            System.out.println(">");
            
            String username = in.readLine();
            
            System.out.println("Please enter your password");
            System.out.println(">");
            
            String password = in.readLine();
            
            // This is for taking the password in the base64 format, so that other people can't see your password!
            //password = Utilities.bytes2String(Utilities.getBase64DecodedBytes(password));
            
            // {Username=...;Password=...}Ktc
            String encrypteduserNameToken = getUserNameToken(username, password);

            // create a connection to the token-server and write the encrypted username token {Username=...;Password=...}Ktc
            Socket socket = new Socket(serverAddress, tokenServerPort);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeUTF(encrypteduserNameToken);
            dos.flush();
            
            
            // open an object stream and get the role-based security token object from the Token Service.
            // {{role, timestamp, C, Ksc}Kts, Ksc }Ktc
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream()); // BLOCKING CALL
            
            roleTokenObj = (RoleBasedToken) ois.readObject();

            return roleTokenObj;

        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | ClassNotFoundException ex) {

            roleTokenObj.result = false;
            roleTokenObj.errorMessage = "Error in encrypting the username password token. Error message: " + ex.getMessage();
            return roleTokenObj;
        }
    }

    // returns  {credentials}Kct
    private static String getUserNameToken(String username, String password) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {

        // Format: Username=XXX;Password=XXX";
        String usernamePwd = "Username=" + username + ";Password=" + password;

        // First encrypt the username token with encryption key shared between Client and token service
        byte[] encryptTokenBytes = DESEncryptionHelper.encryptMessage(Client_TokenService_Key_Passcode.getBytes(Encoding_Format),
                usernamePwd.getBytes(Encoding_Format));

        return Utilities.getBase64EncodedString(encryptTokenBytes);
    }
}
