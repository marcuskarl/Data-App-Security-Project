package SocketEncryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
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
	private ASym_Decrypt Decrypt = null;
	private boolean swappedKeysOnce = false;
	private Socket socket;
	
	public SocketEncryption(Socket x, int keyLength) throws Exception{
		Decrypt = new ASym_Decrypt( keyLength );
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
	
	public int getKeyBitLength () {
		return Decrypt.getNBitLength();
	}
	
	public boolean SwapPublicKeys () throws Exception{
		if ( socket.isConnected() && !swappedKeysOnce ) {			
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
		int RSA_MSG_Size = (Encrypt.getOthersNBitLength() / 8) - 2;
		
		// Sends size of message
		outputStream.writeObject( Encrypt.Encrypt( ByteArrayConversions.IntToByteArray(totalBytes) ) );
		
		if (totalBytes <= RSA_MSG_Size)
			outputStream.writeObject( Encrypt.Encrypt( sourceData ) );
		else {
			// do while will break array into RSA_MSG_Size chunks and send them
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
	
	private byte [] ReadMessage() throws IOException {
		try {
			int totalBytes = ByteArrayConversions.ByteArrayToInt(Decrypt.Decrypt( (BigInteger) inputStream.readObject() ));
			
			BigInteger c = (BigInteger) inputStream.readObject();
			int RSA_MSG_Size = (Decrypt.getNBitLength() / 8) - 2;
			
			if (totalBytes == 1) {
				byte [] b = new byte[1];
				b[0] = Decrypt.DecryptSingleByte( c );				
				return b;
			}
			else if (totalBytes <=  RSA_MSG_Size)
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public class OStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			System.out.println("Writing single byte");
			byte [] x = new byte[1];
			x[0] = ByteBuffer.allocate(4).putInt(b).array()[3];
			WriteMessage( x );
		}
		
		@Override
		public void write(byte [] b) throws IOException  {
			WriteMessage(b);
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			WriteMessage( Arrays.copyOfRange(b, off, off+len) );
		}
	}
	
	private class IStream extends InputStream {
		byte [] buffer = null;
		
		@Override
		public int read(byte [] b) throws IOException {
			if (buffer == null) {
				buffer = ReadMessage();
				
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length; i++)
					b[i] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
			else {
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length; i++)
					b[i] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
		}
		
		@Override
		public int read(byte [] b, int off, int len) throws IOException {
			if (buffer == null) {
				buffer = ReadMessage();
				
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length && i < len; i++)
					b[i + off] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
			else {
				int i = 0;
				
				for ( ; i < b.length && i < buffer.length && i < len; i++)
					b[i + off] = buffer[i];
				
				if (i < buffer.length)
					buffer = Arrays.copyOfRange(buffer, i, buffer.length);
				else
					buffer = null;
				
				return i;
			}
		}
		
		@Override
		public int read() throws IOException {
			if (buffer == null) {
				buffer = ReadMessage();
				
				byte b = buffer[0];
				
				if (buffer.length > 1)
					buffer = Arrays.copyOfRange(buffer, 1, buffer.length);
				else
					buffer = null;
				
				return b;
			}
			else {
				byte b = buffer[0];
				
				if (buffer.length > 1)
					buffer = Arrays.copyOfRange(buffer, 1, buffer.length);
				else
					buffer = null;
				
				return b;	
			}
		}
	}
	
	public Object readObject() throws ClassNotFoundException, IOException {
		return ByteArrayConversions.ByteArrayToObject( ReadMessage() );
	}
	
	public void writeObject(Object x) throws IOException {
		WriteMessage( ByteArrayConversions.ObjectToByteArray( x ) );
	}
}