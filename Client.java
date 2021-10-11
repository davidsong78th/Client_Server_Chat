package Chat;

import java.io.*;
import java.net.*;

public class Client {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	BufferedReader input;
	private Thread sendServer;
	private Thread receiveFromServer;


	public static void main(String[] args) {
		new Client();
	}

	public Client () {	
		try {	
			socket = new Socket("localhost", 192);		//IP as localhost for the sake of on the same machine
			System.out.println("Connected to the Server. You May Begin the Conversation. To Disconnect, type \"exit\" \n");
			//Sockets Input/Outputs
			in = new DataInputStream(socket.getInputStream());		//Input Stream from Server

			out = new DataOutputStream(socket.getOutputStream());		//Output to Server

			input = new BufferedReader(new InputStreamReader(System.in));	//Client inputs 

			sendServer = new sendServer(socket, in, out, input);			//Run Thread for sending multiple messages on one side
			receiveFromServer = new receiveFromServer(socket, in, out);

			sendServer.start();
			receiveFromServer.start();

		}
		catch (IOException e) {
			System.out.println("Connection Refused");
		}
	}

}

class sendServer extends Thread implements Runnable  {
	DataInputStream in;
	DataOutputStream out;
	Socket socket;
	BufferedReader input;

	public sendServer(Socket socket, DataInputStream in, DataOutputStream out, BufferedReader input) {
		this.socket = socket;
		this.in = in;
		this.out = out;
		this.input = input;
	}

	@Override
	public void run() {
		try {
			while (true) {
				String clientInput = input.readLine();		//Read client input
				out.writeUTF(clientInput);					//Write to server
				System.out.println();
				if (clientInput.equals("exit")) {			//When client wants to disconnect from server
					break;
				}
			}
			System.out.println("***You have been disconnected from the Server***");
			in.close();
			out.close();
			socket.close();
		}	
		catch (IOException e) {
			e.printStackTrace();
		}			
	}	
}

class receiveFromServer extends Thread implements Runnable {
	DataInputStream in;
	DataOutputStream out;
	Socket socket;

	public receiveFromServer(Socket socket, DataInputStream in, DataOutputStream out) {
		this.socket = socket;
		this.in = in;
		this.out = out;
	}
	@Override
	public void run() {
		try {	
			String msgFromServer = "";
			while ((msgFromServer = in.readUTF()) != null) {
				System.out.print("<Server>: " + msgFromServer + "\n\n");
			}
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			System.out.println("***Socket Closed***");
		}	
	}	
}
