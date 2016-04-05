import java.io.*;
import java.util.*;
import java.net.*;

/*  The following code heavily relies on an example found at: 
 *  http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets
 *  The work, however, is still all my own. 
 */

public class Server {

	private ServerSocket servSocket;;
	private Socket connection = null;
	private ObjectOutputStream out;
	private ObjectOutputStream in; 
	private String message;

	public Server() {}

	void run() {

		try {
			// ServerSocket(int port, int backlog)
			// Backlog -> requested maximum length of the queue of incoming connections
			servSocket = new ServerSocket(16000, 10);
			System.out.println("Waiting for connection . . .");
			connection = servSocket.accept();
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());

			// Get Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectOutputStream(connection.getInputStream());
			sendEncryptedMessage("Connection Successful");

			do {
				try {
					// Receive file requests
					message = (String) in.readobject();
					System.out.println("client>" + message);
					
					// Decrypt here

					// Encrypt file and send to client here

					// Terminate condition
					if(message.equals("terminate")) {
						sendEncryptedMessage("Terminated");
					}
				} catch (ClassNotFoundException classnot) {
					System.err.println("Data improperly received");
				}
			}

			while(!message.equals("terminate")) {}

		} catch (IOException io) {
			io.printStackTrace();
		}
		// Now we close the connection
		finally {

			try {
				in.close();
				out.close();
				servSocket.colse();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	private void sendEncryptedMessage(String msg) {

		try {
			out.writeObject(msg);
			out.flush;
			System.out.println("server>" + msg);
		} catch (IOException io) {
			io.printStackTrace();
		}
	}

	public void main(String args[]) {

		Server serv = new Server();
		while(true) {
			serv.run();
		}
	}
}