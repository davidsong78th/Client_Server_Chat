package Chat;

import java.io.*;
import java.net.*;
import java.util.Date;

public class Server {
	private ServerSocket serverSocket;
	private Socket clientSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private BufferedReader input;
	private Thread sendClient;
	private Thread receiveFromClient;

	public static void main(String[] args)
	{
		int port = 192;		//Random Port Number
		try {
			new Server(port);
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public Server (int port) throws IOException {
		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setSoTimeout(15000);		//Connection Timeout is 15 secs

			System.out.println("Waiting for Client Connection on Port " + serverSocket.getLocalPort() + "...");

			//Wait for a connection to accept by serveer	
			clientSocket = serverSocket.accept();

			System.out.println("Connection Established at " + new Date() + " from " + clientSocket);
			//Sockets Inputs/Outputs stream
			in = new DataInputStream(clientSocket.getInputStream());	//For Reading Message from client

			out = new DataOutputStream(clientSocket.getOutputStream()); 	//For Sending Message to client

			input = new BufferedReader(new InputStreamReader(System.in));	//Server input 

			//Use Thread to send multiple messages without having to wait
			sendClient = new sendClient(serverSocket, clientSocket, in, out, input);	
			receiveFromClient = new receiveFromClient(serverSocket, clientSocket, in, out);

			sendClient.start();
			receiveFromClient.start();

		}	
		catch (SocketTimeoutException s) {
			System.out.println("Server Socket Timed Out");
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

class sendClient extends Thread implements Runnable {
	ServerSocket serverSocket;
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	BufferedReader input;

	public sendClient(ServerSocket serverSocket, Socket clientSocket, DataInputStream in, DataOutputStream out, BufferedReader input) {
		this.serverSocket = serverSocket;
		this.clientSocket = clientSocket;
		this.in = in;
		this.out = out;
		this.input = input;
	}
	@Override
	public void run() {		//Thread Subclass
		try {
			String serverInput = "";	
			while (true) {
				serverInput = input.readLine();		//read Server input
				out.writeUTF(serverInput);			//Write to Client
				System.out.println();		
			}
		}				
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class receiveFromClient extends Thread {
	ServerSocket serverSocket;
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;

	public receiveFromClient(ServerSocket serverSocket, Socket clientSocket, DataInputStream in, DataOutputStream out) {
		this.serverSocket = serverSocket;
		this.clientSocket = clientSocket;
		this.in = in;
		this.out = out;
	}

	@Override
	public void run() {
		try {
			String msgFromClient = "";
			while ((msgFromClient = in.readUTF()) != null) {
				if (msgFromClient.equals("exit")) {
					break;
				}
				System.out.print("<Client>: " + msgFromClient + "\n\n");
			}
			System.out.println("***Client disconnected from Server***");	
			clientSocket.close();
			serverSocket.close();
			System.exit(0);		//Force Exit
		}
		catch (IOException e) {
			e.printStackTrace();
		}		
	}

}
