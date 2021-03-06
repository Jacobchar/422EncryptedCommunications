import java.util.*;

public class Crypto {

	static {
		System.loadLibrary("crypto");
	}

	private long[] userID;

	public Crypto(long[] userID) {
		assert userID.length == 4;
		this.userID = userID;
	}

	public byte[] encrypt(byte[] data) {
		byte[] newData = padData(data);
		encrypt(newData, userID);
		return newData;
	}

	public byte[] decrypt(byte[] data) {
		byte[] newData = padData(data);
		decrypt(newData, userID);
		return newData;
	}

	private byte[] padData(byte[] data) {
		return Arrays.copyOf(data, (int) Math.ceil((double) data.length / (Long.SIZE * 2))*(Long.SIZE * 2));
	}
	
	private native static void encrypt(byte[] v, long[] k);
	private native static void decrypt(byte[] v, long[] k);

}
