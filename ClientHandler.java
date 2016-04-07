import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    protected Socket socket;
    protected boolean stop;
    protected String clientId;
    protected Crypto tea;
    protected DataOutputStream out;
    protected DataInputStream in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.stop = false;
    }

    public void run() {
        try {
            ClientState state = new ClientState.Authorizing(this);
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            while(!isStopped()) {
                state = state.run(read());
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            this.stop();
        }

    }

    public synchronized boolean isStopped() {
        return this.stop;
    }

    public synchronized void stop() {
        if (this.isStopped()) {
            return;
        }

        System.out.println("Disconnecting client: " + clientId);
        this.stop = true;
        try {
            if (this.in != null) {
                this.in.close();
            }
            if (this.out != null) {
                this.out.close();
            }
            if (this.socket != null) {
                this.socket.close();
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
        return tea.encrypt(b);
    }

    public byte[] encrypt(byte[] message) {
        return tea.encrypt(message);
    }

    public String decrypt(byte[] encrypted) {
        byte[] decrypted = this.tea.decrypt(encrypted);
        return new String(decrypted).trim();
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setTea(Crypto tea) {
        this.tea = tea;
    }

}
