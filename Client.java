import java.io.*;
import java.util.*;
import java.net.*;

/*  The following code heavily relies on an example found at: 
 *  http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets
 *  The work, however, is still all my own. 
 */

public class Client {

    Socket request;
    ObjectOutputStream out;
    ObjectInputStream in;
    String message;
    public Client() {}
    private void run() {
        try { 

            request = new Socket("localhost", 16000);
            System.out.println("Connected to localhost in port 16000");
            // Get Input and Output streams
            out = new ObjectOutputStream(request.getOutputStream());
            out.flush();
            in = new ObjectInputStream(request.getInputStream());
            //3: Communicating with the server
            do {
                try {
                    // File requests to the server
                    message = (String)in.readObject();
                    System.out.println("server>" + message);
                    sendEncryptedMessage("Hi my server");
                    message = "bye";
                    sendEncryptedMessage(message);
                } catch(ClassNotFoundException classNot) {
                    System.err.println("Data improperly received");
                }
            }
            while(!message.equals("terminate"));
        }
        catch(UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch(IOException io) {
            io.printStackTrace();
        }
        finally {
            // Close connection
            try {
                in.close();
                out.close();
                request.close();
            } catch(IOException io) {
                io.printStackTrace();
            }
        }
    }

    private void sendEncryptedMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    public static void main(String args[]) {
        Client client = new Client();
        client.run();
    }
}	
