import java.util.*;

public class Decrypt extends Thread {

	private String message;

	public Decrypt(String msg) {
		message = msg;
	}

	public void run() {
		try {
			System.loadLibrary("decrypt");
			message = decrypt(message);
		} catch (ThreadDeath td) {
			throw td;
		}
	}

	public String getMessage() {
		return message;
	}

	public native String decrypt(String message);

}