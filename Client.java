import java.io.*;
import java.util.*;
import java.net.*;
import java.nio.file.*;

/*  The following code heavily relies on an example found at: 
 *  http://stackoverflow.com/questions/1776457/java-client-server-application-with-sockets
 *  The work, however, is still all my own. 
 */

public class Client implements Runnable {

    private Socket sock;
    private String hostName;
    private int port;
    private boolean terminate;
    private Crypto crypt;
    protected DataOutputStream out;
    protected DataInputStream in;

    public static void main(String args[]) {
        
        if(args.length < 2) {
            System.out.println("Use: java Client hostname port#\n");
            System.exit(1);
        }

        String hostName = args[0];
        int port = 0;

        try {
            port = Integer.parseUnsignedInt(args[1]);
        } catch(NumberFormatException e) {
            System.out.println("Use: java Client hostname port#");
            System.exit(1);
        }

        Client client = new Client(hostName, port);
        Runtime.getRuntime().addShutdownHook(new Thread(client::terminate));
        client.run();
    }

    public Client(String hostName, int port) {
     
        this.hostName = hostName;
        this.port = port;
        this.terminate = false;
    }

    public void run() {
        
        try { 
            createSocket();
            getID();
            while(!isTerminated()) {
                requestFile();
            }
        } catch(IOException io) {
            System.out.println("Could not process request");
            System.exit(1);
        }
    }

    private void createSocket() throws IOException {
       
        try {
            sock = new Socket(hostName, port);
            in = new DataInputStream(sock.getInputStream());
            out = new DataOutputStream(sock.getOutputStream());    
        } catch (IOException io) {
            throw new RuntimeException("Cannot connect server " + hostName + " at " + port, io);
        }
    }

    private void getID() throws IOException {
     
        String id;
        while(crypt == null) {
            id = prompt("Enter client ID: ");
            crypt = Server.getClients().get(id);
            if (crypt == null) {
                System.out.println("Try again");
            }
        }
    }

    private String prompt(String msg) throws IOException {
     
        System.out.println(msg);
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        return r.readLine();
    }

    public synchronized boolean isTerminated() {
        return terminate;
    }

    public synchronized void terminate() throws IOException {
        if (isTerminated()) {
            return;
        }

        terminate = true;
        
        try {
            System.out.println("Closing socket.");
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (sock != null) {
                sock.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error closing socket", e);
        }
    }

    private void requestFile() throws IOException {
        String file = prompt("Enter file name or 'terminate' to terminate:");

        if (file.equals("terminate")) {
            write(ClientState.FINISHED);
            terminate();
            return;
        }

        write(file);
        String response = read();
        System.out.println(response);
        if (response.equals(ClientState.FOUND)) {
            saveFile();
        }
    }

    private void saveFile() throws IOException {
        System.out.println("Reading file from server.");
        int size = in.readInt();
        byte[] encrypted = new byte[size];
        in.readFully(encrypted);
        byte[] decrypted = crypt.decrypt(encrypted);
        System.out.println("File size (bytes): " + decrypted.length);
        String name = prompt("Enter name to save file as: ");

        Path path = Paths.get(name);
        if (!Files.isDirectory(path) && !Files.exists(path)) {
            Files.write(path, removePadding(decrypted));
            System.out.println("Successfully saved file.");
        } else {
            System.out.println("Path provided is not writeable.");
        }
    }

    private byte[] removePadding(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i+1);
    }


    private void write(String message) throws IOException {
        System.out.println("Writing message to server.");
        byte[] encrypted = crypt.encrypt(message.getBytes());
        out.writeInt(encrypted.length);
        out.write(encrypted);
        out.flush();
    }

    private String read() throws IOException {
        return read(true);
    }

    private String read(boolean decrypt) throws IOException {
        System.out.println("Reading from server.");
        int size = in.readInt();
        System.out.println("Message size: " + size);
        byte[] bytes = new byte[size];
        in.readFully(bytes);
        if (decrypt) {
            bytes = crypt.decrypt(bytes);
        }
        return new String(bytes).trim();
    }
}	
