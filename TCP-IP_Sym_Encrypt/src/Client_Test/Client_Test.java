package Client_Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import SocketEncryption.KeyObject;
import SocketEncryption.SocketEncryption;

public class Client_Test {

	public static void main(String[] args) throws IOException {
		System.out.print("Server (1) or client mode (2) ? ");
		
		Scanner scan_in = new Scanner ( System.in );
		
		int input = scan_in.nextInt();
		
		scan_in.nextLine();
		
		if (input == 1) {
			try {
				ServerSocket listen = new ServerSocket(0, 1, InetAddress.getLocalHost());
				
				System.out.println("Server listening on: " + listen.getInetAddress().getHostAddress()
						+ " port " + listen.getLocalPort());
				
				SocketEncryption socket = new SocketEncryption( listen.accept() );
				//listen.close();
				
				if (socket.SwapPublicKeys())
					System.out.println("SERVER: Keys swapped");
				else
					System.out.println("SERVER: Failed to swap keys");
				
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				
				byte [] echo = new byte [1024];
				int len = 0;
				
				while (true) {
					len = in.read(echo);
					System.out.println("SERVER: " + new String ( Arrays.copyOfRange(echo, 0, len) ) );
					
					if ( (new String ( Arrays.copyOfRange(echo, 0, len) )).equals("quit") )
						break;
					
					out.write(echo, 0, len);
					System.out.println("SERVER: len=" + len + " message: " + (new String ( Arrays.copyOfRange(echo, 0, len) )) + " sent");
					
				}
				
				in.close();
				out.close();
				socket.close();
				listen.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (input == 2) {
			try {
				System.out.print("Enter server IP Address: ");
				String ipAddress = scan_in.nextLine();
				
				System.out.print("Enter Port: ");
				int port = scan_in.nextInt();
				scan_in.nextLine();
				
				SocketEncryption socket = new SocketEncryption( new Socket (ipAddress, port) );
				
				if (socket.SwapPublicKeys())
					System.out.println("CLIENT: Keys swapped");
				else
					System.out.println("CLIENT: Failed to swap keys");
				
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				
				String userInput;
				byte [] echo = new byte [1024];;
				int len = 0;
				
				while (true) {
					userInput = scan_in.nextLine();					
					
					if (userInput.equals("quit") ) {
						out.write( userInput.getBytes() );
						break;
					}
					else if (userInput.equals("1") ) {
						int a = 5023;
						out.write( a );
						System.out.print("CLIENT: Waiting for reply... ");
						len = in.read();
						byte b = (byte) len;
						System.out.println("server replied: a=" + a + ", b=" + b);
					}
					else if (userInput.equals("obj") ) {
						socket.writeObject( socket.Decrypt.GetPublicKey() );
						
						System.out.println("CLIENT: Waiting for reply... ");

						KeyObject Key = (KeyObject) socket.readObject();
						
						System.out.println("Server replied: " + Key.getNValue() );
					}
					else {
						out.write( userInput.getBytes() );
						System.out.print("CLIENT: Waiting for reply... ");
						len = in.read(echo);
						
						System.out.println("server replied: " + (new String( Arrays.copyOfRange(echo, 0, len) ) ) );
					}
				}
				in.close();
				out.close();
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		scan_in.close();
	}
}