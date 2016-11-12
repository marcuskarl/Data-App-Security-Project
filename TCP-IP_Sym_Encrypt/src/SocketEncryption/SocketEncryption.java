package SocketEncryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import asym_Decrypt.ASym_Decrypt;
import asym_Encrypt.ASym_Encrypt;

// Class extends Socket in order to override read and write methods from inputstream and outputstream
// The methods are overridden in order to provide a seam less opportunity to user for encryption and decryption
// of data being transmitted
public class SocketEncryption extends Socket {
	
	// Creates and initializes InputStream and OutputStream objects
	private OStream oS = new OStream();
	private IStream iS = new IStream();
	private ObjectInputStream inputStream = null;
	private ObjectOutputStream outputStream = null;
	private ASym_Encrypt Encrypt = new ASym_Encrypt();
	private ASym_Decrypt Decrypt = new ASym_Decrypt();
	private boolean swappedKeysOnce = false;
	private Socket socket;
	
	public SocketEncryption(Socket x) throws IOException {
		socket = x;
		outputStream = new ObjectOutputStream( x.getOutputStream() );
		inputStream = new ObjectInputStream( x.getInputStream() );
	}
	
	public InputStream getInputStream() throws IOException {
		return iS;
	}
	
	public OutputStream getOutputStream() throws IOException {
		return oS;
	}
	
	public boolean SwapPublicKeys () throws Exception{
		if ( socket.isConnected() && !swappedKeysOnce ) {			
			// Send signature message
			// Verify signature of remote user
			
			outputStream.writeObject(Decrypt.GetPublicKey());
			swappedKeysOnce = Encrypt.ReceiveKey( (KeyObject) inputStream.readObject() );
			
			return swappedKeysOnce;
		}
		return false;
	}
	
	private int BuildMessage(byte [] sourceArray, byte[] destArray, int startingByte, int totalSize) {
		Random rand = new Random( System.currentTimeMillis() );
		for (int i = 0; i < destArray.length; i++) {
			if (startingByte < totalSize)
				destArray[i] = sourceArray[startingByte++];
			else
				destArray[i] = (byte) rand.nextInt(255);
		}
		
		return startingByte;
	}
	
	private void WriteMessage (byte [] sourceData) throws IOException {
		int startingByte = 0;
		int totalBytes = sourceData.length;
		byte [] msgToSend;
		int RSA_MSG_Size = (Encrypt.getOthersNValue().bitLength() / 8) - 2;
		
		// Sends size of message
		outputStream.writeObject( Encrypt.Encrypt( ByteArrayConversions.LongToByteArray(totalBytes) ) );
		
		if (totalBytes <= RSA_MSG_Size)
			outputStream.writeObject( Encrypt.Encrypt( sourceData ) );
		else {
			// do while will break array into messageByteArraySize chunks and send them
			// loop will exit when all bytes are sent
			do {
				msgToSend = new byte [ RSA_MSG_Size ];
				startingByte = BuildMessage(sourceData, msgToSend, startingByte, totalBytes);
				
				outputStream.writeObject( Encrypt.Encrypt( msgToSend ) );
			} while(startingByte < totalBytes);
		}
	}
	
	private int ParseMessage (byte[] sourceArray, byte [] destArray, int startByte, int totalSize) {
		for (int i = 0; i < sourceArray.length; i++)
			if (startByte < totalSize)
				destArray[startByte++] = sourceArray[i];
			else
				break;
		
		return startByte;
	}
	
	private byte [] ReadMessage() {
		try {
			int totalBytes = (int) ByteArrayConversions.ByteArrayToLong(Decrypt.Decrypt( (BigInteger) inputStream.readObject() ));
			System.out.println("READMESSAGE(): totalBytes " + totalBytes );
			
			BigInteger c = (BigInteger) inputStream.readObject();
			int RSA_MSG_Size = (Decrypt.getNValue().bitLength() / 8) - 2;
			
			if (totalBytes <=  RSA_MSG_Size)
				return Decrypt.Decrypt( c );
			else {
				int startingByte = 0;
				byte [] msgRcvd = new byte [totalBytes];
				
				startingByte = ParseMessage(Decrypt.Decrypt( c ), msgRcvd, startingByte, totalBytes);
				
				while (startingByte < totalBytes) {
					c = (BigInteger) inputStream.readObject();
					startingByte = ParseMessage(Decrypt.Decrypt( c ), msgRcvd, startingByte, totalBytes);
				}
				
				return msgRcvd;
			}
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public class OStream extends OutputStream {
		
		@Override
		public void write(int b) throws IOException {
			WriteMessage( ByteArrayConversions.ObjectToByteArray(b));
		}
		
		@Override
		public void write(byte [] b) throws IOException {
			WriteMessage(b);
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			WriteMessage( Arrays.copyOfRange(b, off, off+len) );
		}
	}
	
	private class IStream extends InputStream {
		
		@Override
		public int read(byte [] b) throws IOException {
			byte [] arr = ReadMessage();
			
			for (int i = 0; i < b.length && i < arr.length; i++)
				b[i] = arr[i];
			
			return arr.length;
		}
		
		@Override
		public int read(byte [] b, int off, int len) throws IOException {
			byte [] arr = ReadMessage();
			
			for (int i = 0; i < b.length && i < arr.length && i < len; i++)
				b[i + off] = arr[i];
			
			return arr.length;
		}
		
		@Override
		// This method needs changed.....it discards the rest of the byte array after returning
		// the first byte
		public int read() throws IOException {
			return ReadMessage()[0];
		}
	}
}