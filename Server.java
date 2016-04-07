import java.io.*;
import java.util.*;
import java.net.*;

/*  The following code heavily relies on an example found at: 
 *  http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets
 *  The work, however, is still all my own.
 */

public class Server {

	private ServerSocket servSocket;
	private int port;
	private boolean terminate;
	private static Map<String, Crypto> clients;

	public static void main(String args[]) {

		Server serv = new Server(16000);
		serv.run();
	}

	public Server(int port) {

		this.port = port;
		this.terminate = false;
	}

	// http://www.tutorialspoint.com/java/util/collections_unmodifiablemap.htm
	// the above helped fix one of my errors
	public static Map<String, Crypto> getClients() {
        if (clients == null) {
            initializeClients();
        }
        return Collections.unmodifiableMap(clients);
    }

    private static void initializeClients() {
        clients = new HashMap<>();
        clients.put("client1", new Crypto(new long[] {46874L, 3587L, 654876L, 518679L}));
        clients.put("client2", new Crypto(new long[] {4677L, 851212L, 121237L, 6282L}));
        clients.put("client3", new Crypto(new long[] {98371L, 65246L, 8762428L, 16724L}));
        clients.put("client4", new Crypto(new long[] {7983L, 12345678L, 91011L, 12131415L}));
        clients.put("client5", new Crypto(new long[] {7897654L, 3216848L, 6432138L, 43438434L}));
    }

	public void run() {

		createServerSockets();
		while(!isTerminated()) {
			try {
				Socket sock = servSocket.accept();
				new Thread(new ClientHandler(sock)).start();
			} catch (IOException io) {
				if (isTerminated()) {
					return;
				}
				System.out.println(io.toString());
			}
		}
	}

	public synchronized boolean isTerminated() {
		return terminate;
	}

	public synchronized void terminate() {
		terminate = true;
		try {
			System.out.println("Shutting down");
			servSocket.close();
		} catch (IOException io) {
			throw new RuntimeException("Error shutting down", io);
		}
	}

	private void createServerSockets() {
        try {
            System.out.println("Starting server through port number " + this.port);
            this.servSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.port, e);
    	}
    }

}
