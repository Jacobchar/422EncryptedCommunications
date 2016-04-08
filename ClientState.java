import java.io.*;
import java.nio.file.*;
import java.util.*;

// Help from: http://www.di.ase.md/~aursu/ClientServerThreads.html

public abstract class ClientState {

    public static final String NOT_AUTHORIZED = "ACCESS DENIED";
    public static final String AUTHORIZED = "ACCESS GRANTED";
    public static final String FINISHED = "FINISHED";
    public static final String NOT_FOUND = "FILE NOT FOUND";
    public static final String FOUND = "FILE FOUND";
    protected ClientHandler handler;

    public ClientState(ClientHandler handler) {
        this.handler = handler;
    }

    public abstract ClientState run(byte[] data) throws IOException;

    public void write(String message) throws IOException {
        handler.write(handler.encrypt(message));
    }

    public void write(byte[] message) throws IOException {
        handler.write(handler.encrypt(message));
    }

    public static class Authorizing extends ClientState {

        public Authorizing(ClientHandler handler) {
            super(handler);
        }

        public ClientState run(byte[] data) throws IOException {
            System.out.println("Authorizing client...");
            Map.Entry<String, Crypto> result = findClient(data);

            if (result == null) {
                System.out.println("Client not authorized!");
                handler.write(NOT_AUTHORIZED.getBytes()); // write this unencrypted
                this.handler.terminate();
                return null;
            }

            handler.setClientId(result.getKey());
            handler.setTea(result.getValue());

            System.out.println("Client authorized as: " + result.getKey());
            handler.write(AUTHORIZED.getBytes()); // write this unencrypted
            return new Serving(handler);
        }

        private Map.Entry<String, Crypto> findClient(byte[] data) {
            Map.Entry<String, Crypto> client = null;

            for (Map.Entry<String, Crypto> entry : Server.getClients().entrySet()) {
                if (entry.getKey().equals(new String(entry.getValue().decrypt(data)).trim())) {
                    client = entry;
                }
            }

            return client;
        }
    }

    public static class Serving extends ClientState {

        public Serving(ClientHandler handler) {
            super(handler);
        }

        public ClientState run(byte[] bytes) throws IOException {
            String message = handler.decrypt(bytes);
            if (message.equals(FINISHED)) {
                System.out.println("Client terminated session.");
                this.handler.terminate();
                return null;
            } else {
                Path path = Paths.get(message);
                if (Files.isRegularFile(path) && Files.isReadable(path)) {
                    write(FOUND);
                    System.out.println("Sending file " + message);
                    write(Files.readAllBytes(path));
                } else {
                    write(NOT_FOUND);
                }
            }

            return this;
        }
    }
}
