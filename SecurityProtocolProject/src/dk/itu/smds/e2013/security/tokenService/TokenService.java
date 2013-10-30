/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security.tokenService;

import dk.itu.smds.e2013.security.DESEncryptionHelper;
import dk.itu.smds.e2013.security.RoleBasedToken;
import dk.itu.smds.e2013.security.SSHAuthenticationHelper;
import dk.itu.smds.e2013.security.Utilities;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author rao
 * 
 * This is a simplified example of authenticated communication with a server.
 * the TokenService takes the place of an authentication authority (it uses ITU's service for that)
 * and issues challenges/tickets for users of the TokenService. 
 * 
 * The TokenService then, knows all users' secret keys. So it is predominantly used in some sort of 
 * closed network, that the secret keys can be sent over a secure channel.
 * 
 * The good thing is that the user don't have to constantly expose his password over the network in order to communicate with the server.
 * Instead a challenge is issued. Only the user can see the challenge (ticket) since it is encrypted in his private key.  
 * 
 * Note the private keys are typically produced by using an algorithm on the users password... 
 * hence it is difficult to crack that private key without knowing the password. 
 * (thus we trust that the principle behind the secret key is who he states to be.)
 * 
 * Problem: there's no guarantee against replay of old authentication messages. i.e the ticket could be a replay.
 * Problem: How to establish the shared key? 
 *  
 * So it is not suitable for commerce or wide area applications where there's no trusted authentication authority 
 * and no secure channel over which to send the secret keys. 
 * 
 * For these scenarios we use authenticated communication with public keys (a hybrid protocol).
 * 
 * 
 */
public class TokenService {

	// Role-based security. A authentication service (like this token-service) hold an access-rights table.
	// It holds key-rights pairs for resources. 
    private static HashMap<String, String> roleMappings = new HashMap();
    
    private static int tokenServerPort = 8008;

    // shared key ( Ktc )
    private static String Client_Key_Passcode = "Topmost Secret";
    // shared key ( Kts )
    private static String Server_Key_Passcode = "Don't reveal the Secret";
    // shared key (Kcs)
    private static String Client_Service_key_Passcode = "yeah man n all dat";
    
    
    private static BufferedReader in;
    private static final String Encoding_Format = "UTF8";
    private static final SimpleDateFormat formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static void main(String args[]) throws Exception {

        //  Access rights table.
    	// pairs of [principle, role]
        roleMappings.put("rao", "student,teacher,ta");
        roleMappings.put("nsth", "student");

        // hook on to console input ..
        in = new BufferedReader(new InputStreamReader(System.in));
        ServerSocket serverSocket = null;
        DataInputStream dis;
        ObjectOutputStream oos;

        try {

            serverSocket = new ServerSocket(tokenServerPort);
            System.out.println("Token Service started at 8008");

            while (true) {
                
                Socket socket = serverSocket.accept(); // blocking call

                // Data Input and output streams
                dis = new DataInputStream(socket.getInputStream());
                
                // get the client credentials encrypted in a shared private key (tokenservice - client)
                // {credentials}Ktc
                String usertoken = dis.readUTF(); // blocking call
                System.out.println("Token received: " + usertoken);
                
                // generate the token ->  {{role, timestamp, C, Ksc}Kts, Ksc }Ktc
                RoleBasedToken roleBasedToken = generateToken(usertoken);

                // Write the token into socket..
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(roleBasedToken);
                oos.flush();

                // wait for the next request...
            }
        } catch (Exception ex) {

            System.out.println(ex.getMessage());
        }
    }

    private static RoleBasedToken generateToken(String userToken) {
    	// new token
        RoleBasedToken token = new RoleBasedToken();
        String tokenString;

        try {

        	//  transform client's message from ANSCII to bytes
            byte[] encryptedBytes = Utilities.getBase64DecodedBytes(userToken);

            // Client and token-server's shared key (Ktc) as bytes
            byte[] keyBytes = Client_Key_Passcode.getBytes(Encoding_Format);
            
            //  decrypt the clients message
            byte[] decryptMessage = DESEncryptionHelper.decryptMessage(keyBytes, encryptedBytes);
            
            // client's message 
            tokenString = Utilities.bytes2String(decryptMessage);

        } catch (Exception ex) {

            System.out.println("Authentication error: " + ex.getMessage());
            token.result = false;
            token.errorMessage = "Failed to decrypt message! Error message: " + ex.getMessage();
            return token;
        }

        String[] tokenArray = tokenString.split(";");

        if (tokenArray.length != 2) {
            token.result = false;

            token.errorMessage = "The supplied user credentials are not in right format! Format: Username=XXX;Password=XXX";
            return token;
        }

        String[] userNameArray = tokenArray[0].split("=");
        String[] passwordArray = tokenArray[1].split("=");

        if ((userNameArray.length != 2) || (passwordArray.length != 2)) {
            token.result = false;
            token.errorMessage = "The supplied user credentials are not in right format! Format: Username=XXX;Password=XXX";
            return token;
        }

        try {
            // Authenticate using SSH...
            boolean authntication = SSHAuthenticationHelper.Authenticate(userNameArray[1], passwordArray[1]);
            //boolean authntication = LDAPAuthenticationHelper.AuthenticateRevised(userNameArray[1], passwordArray[1]);

            if (!authntication) {
                token.result = false;
                token.errorMessage = "Incorrect user credentials!";
                return token;
            }
            
            System.out.println("Authentication succeeded for user: " + userNameArray[1] );


        } catch (Exception ex) {

            System.out.println("Authentication error: " + ex.getMessage());
            token.result = false;
            token.errorMessage = "Failed to authenticate user credentials with SSH! Error message: " + ex.getMessage();
            return token;
        }

        String role;
        // return error if we dont find a mapping
        if (roleMappings.containsKey(userNameArray[1])) {
            role = roleMappings.get(userNameArray[1]);
        }
        else
        {
            // if no role mapping is found, defaulted to student role.
            System.out.println("No role mapping found for user: " + userNameArray[1] 
                    + ". Therefore the role has been defaulted to 'student'." );
            
            role = "student";
        }

        try {
        	
            // All is well, return a token {{role,timestamp,C,Ksc}Kts,Ksc}Ktc
            token.token = getRoleBasedToken(role, userNameArray[1]);
            token.result = true;
            token.key = Client_Service_key_Passcode;
            return token;
            
        } catch (Exception ex) {

            System.out.println("Failed to encrypt role token: " + ex.getMessage());
            token.result = false;
            token.errorMessage = "Failed to create encrypted token for the server! Error message: "
                    + ex.getMessage();

            return token;
        }
    }

    private static String getRoleBasedToken(String role, String user) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, FileNotFoundException, IOException, IllegalBlockSizeException, BadPaddingException {

    	Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);
        Date expiryTime = calendar.getTime();

        // Format of server token = [role];[timestamp];[principle];[session key]
        String roleToken = role + ";" + formatted.format(expiryTime) +";"+ user +";"+ Client_Service_key_Passcode;  

        // First encrypt the server token with server encryption key..
        byte[] encryptTokenBytes = DESEncryptionHelper.encryptMessage(Server_Key_Passcode.getBytes(Encoding_Format),
                roleToken.getBytes(Encoding_Format));
        
        String base64ServerToken = Utilities.getBase64EncodedString(encryptTokenBytes);

        // Now again encrypt the base 64 encoded server token with Client key.
        byte[] doubleEncryptTokenBytes = DESEncryptionHelper.encryptMessage(Client_Key_Passcode.getBytes(Encoding_Format),
                base64ServerToken.getBytes(Encoding_Format));

        String encryptedRoleToken = Utilities.getBase64EncodedString(doubleEncryptTokenBytes);

        return encryptedRoleToken;
    }
}
