import java.io.*;
import java.net.*;

// Help from: http://www.di.ase.md/~aursu/ClientServerThreads.html

/* THis code handles the clients communicating with the server and encrypts/decrypts the data */

/* Jacob Charlebois, February 2016 */
public class ClientHandler implements Runnable {

    protected Socket socket;
    protected boolean terminate;
    protected String client;
    protected Crypto crypt;
    protected DataOutputStream out;
    protected DataInputStream in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.terminate = false;
    }

    public void run() {
        try {
            ClientState state = new ClientState.Authorizing(this);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            while(!isTerminated()) {
                state = state.run(read());
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            terminate();
        }

    }

    public synchronized boolean isTerminated() {
        return terminate;
    }

    public synchronized void terminate() {
        if (isTerminated()) {
            return;
        }

        System.out.println("Disconnecting " + client);
        terminate = true;
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch(IOException e) {
            System.out.println(e.toString());
        }
    }

    public void write(byte[] message) throws IOException {
        System.out.println("Writing message to client.");
        out.writeInt(message.length);
        out.write(message);
        out.flush();
    }

    public byte[] read() throws IOException {
        System.out.println("Reading from client.");
        int size = in.readInt();
        System.out.println("Message size: " + size);
        byte[] encrypted = new byte[size];
        in.readFully(encrypted);
        return encrypted;
    }

    public byte[] encrypt(String message) {
        byte[] b = message.getBytes();
        return crypt.encrypt(b);
    }

    public byte[] encrypt(byte[] message) {
        return crypt.encrypt(message);
    }

    public String decrypt(byte[] encrypted) {
        byte[] decrypted = crypt.decrypt(encrypted);
        return new String(decrypted).trim();
    }

    public void setClientId(String client) {
        this.client = client;
    }

    public void setcrypt(Crypto crypt) {
        this.crypt = crypt;
    }

}
