package SocketEncryption;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import Sym_Decrypt.Sym_Decrypt;
import Sym_Encrypt.Sym_Encrypt;

// Class extends Socket in order to override read and write methods from inputstream and outputstream
// The methods are overridden in order to provide a seam less opportunity to user for encryption and decryption
// of data being transmitted
public class SocketEncryption extends Socket {
	
	// Creates and initializes InputStream and OutputStream objects
	private OStream oS = new OStream();
	private IStream iS = new IStream();
	private OOStream ooS = null;
	private OIStream oiS = null;
	private Sym_Encrypt Encrypt = new Sym_Encrypt();
	private Sym_Decrypt Decrypt = new Sym_Decrypt();
	private boolean swappedKeys = false;
	
	public InputStream getInputStream() throws IOException {
		// Gets inputstream from super
		// Sets custom iS inputstream as super inputstream for use
		iS.SetStream( super.getInputStream() );
		
		// Returns custom inputstream object
		return iS;
	}
	
	public OutputStream getOutputStream() throws IOException {
		oS.SetStream( super.getOutputStream() );
		
		return oS;
	}
	
	public ObjectOutputStream getObjectOutputStream () throws IOException {
		ooS = new OOStream( super.getOutputStream() );
		return ooS.getOOStream();
	}
	
	public ObjectInputStream getObjectInputStream () throws IOException {
		oiS = new OIStream( super.getInputStream() );
		return oiS.getOIStream();
	}
	
	public boolean SwapPublicKeys () throws Exception{
		if ( this.isConnected() && !swappedKeys ) {
			
			// Send signature message
			// Verify signature of remote user
			
			
			
			EncryptionObject OutGoingKey = new EncryptionObject();
			EncryptionObject IncomingKey = null;
			
			OutGoingKey.setKey( true );
			OutGoingKey.setMsg( Decrypt.GetPublicKey() );
			
			ObjectOutputStream oos = new ObjectOutputStream( oS );
			ObjectInputStream ois = new ObjectInputStream( iS );
			
			oos.writeObject( OutGoingKey );
			
			IncomingKey = (EncryptionObject) ois.readObject();
			
			if ( IncomingKey.getKey() == true ) {
				Encrypt.ReceiveKey( IncomingKey.getMsg() );
				
				
				// Send/Receive test message
				swappedKeys = true;
				return true;
			}
			else {
				// Unable to resolve encryption keys
				return false;
			}
		}
		return false;
	}
	
	private class OStream extends OutputStream {
		private OutputStream socketOStream = null;
		
		private void SetStream(OutputStream os) {
			socketOStream = os;
		}
		
		@Override
		public void write(int b) throws IOException {
			int val = b;
			// Call encryption on val....
			
			
			// Write encrypted b to socket
			socketOStream.write(val);
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			byte [] arr = new byte[b.length];
			for (int i = 0; i < arr.length; i++)
				arr[i] = b[i];

			// Call encryption on arr....
			
			
			
			// Write encrypted b to socket
			socketOStream.write(arr);
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			
			// Copies byte array from given array into a new array with specified elements to write from
			byte [] arr = new byte[len];
			for (int i = 0; i < len; i++)
				arr[i] = b[off + i];
			
			// Call encryption on arr....
			
			
			// Write encrypted array to socket
			socketOStream.write(arr);
		}
		
	}
	
	private class IStream extends InputStream {
		private InputStream socketIStream = null;
		
		private void SetStream(InputStream is) {
			socketIStream = is;
		}
		
		@Override
		public int read(byte [] b) throws IOException {
			// Creates array to read from socket input stream
			byte [] arr = new byte[b.length];
			int value = socketIStream.read(arr);
			
			// Decrypt arr...
			
			
			
			// Changes b pointer to decrypted arr...
			b = arr;
			
			return value;
		}
		
		@Override
		public int read(byte [] b, int off, int len) throws IOException {
			byte [] arr = new byte[len];
			int value = socketIStream.read(arr, 0, len);
			
			// Decrypt arr.....
			
			
			
			// Copies decrypted arr to specified location in b
			for (int i = 0; i < len; i++)
				b[off + i] = arr[i];
			
			return value;
		}

		@Override
		public int read() throws IOException {
			int value = socketIStream.read();
			
			// Decrypt value.....
			
			return value;
		}
	}
	
	private class OOStream extends ObjectOutputStream {
		ObjectOutputStream ooS = null;
		
		OOStream ( OutputStream x) throws IOException {
			ooS = new ObjectOutputStream( x );
		}
		
		public ObjectOutputStream getOOStream() {
			return ooS;
		}
		
	}
	
	private class OIStream extends ObjectInputStream {
		ObjectInputStream oiS = null;
		
		OIStream ( InputStream x) throws IOException {
			oiS = new ObjectInputStream( x );
		}
		
		public ObjectInputStream getOIStream() {
			return oiS;
		}
	}
}