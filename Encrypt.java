import java.util.*;

public class Encrypt extends Thread {

	private String message;

	public Encrypt(String msg) {
		message = msg;
	}

	public void run() {
		try {
			System.loadLibrary("encrypt");
			message = encrypt(message);
		} catch (ThreadDeath td) {
			throw td;
		}
	}

	public String getMessage() {
		return message;
	}

	public native String encrypt(String message);

}