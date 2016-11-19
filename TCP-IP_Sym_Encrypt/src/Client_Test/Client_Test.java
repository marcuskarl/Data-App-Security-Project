package Client_Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

import SocketEncryption.SocketEncryption;
import sym_AES_Based.sym_AES_Based;

public class Client_Test {

	public static void main(String[] args) throws IOException {
		
		System.out.print("Server (1) or client mode (2) ? ");
		
		Scanner scan_in = new Scanner ( System.in );
		
		int input = scan_in.nextInt();
		
		scan_in.nextLine();
		
		if (input == 1) {
			try {
				System.out.print("Enter approximate key bit length (min value 128): ");
				
				int keyLength = scan_in.nextInt();
				scan_in.nextLine();
				
				ServerSocket listen = new ServerSocket(0, 1, InetAddress.getLocalHost());
				
				System.out.println("Server listening on: " + listen.getInetAddress().getHostAddress()
						+ " port " + listen.getLocalPort());
				
				SocketEncryption socket = new SocketEncryption( listen.accept(), keyLength, "", false );
				//listen.close();
				
				//System.out.println("SERVER: Key length is: " + socket.getKeyBitLength() );
				
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
				e.printStackTrace();
			} 
		}
		else if (input == 2) {
			try {
				System.out.print("Enter approximate key bit length (min value 128): ");
				
				int keyLength = scan_in.nextInt();
				scan_in.nextLine();
				
				System.out.print("Enter server IP Address: ");
				String ipAddress = scan_in.nextLine();
				
				System.out.print("Enter Port: ");
				int port = scan_in.nextInt();
				scan_in.nextLine();
				
				SocketEncryption socket = new SocketEncryption(new Socket (ipAddress, port), keyLength, "", false );
								
				//System.out.println("CLIENT: Key length is: " + socket.getKeyBitLength() );
				
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
				e.printStackTrace();
			}
		}
		
		scan_in.close();
		}
}