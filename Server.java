import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;

	private final int portNumber = 12345;
	private final int backlogLimit = 100;
	
	private Scanner terminal;
	
	
	// wrote this while looking at teachers reference code
	public void runServer() {
		
		try {
			
			server = new ServerSocket(portNumber, backlogLimit);
			while (true) {
				try
				{
					// this is where most of the work is done
					waitForConnection();
					getStreams();
					processConnection();
				} 
				catch (EOFException e) 
				{
					System.out.println("\nServer terminated connection");
				}
				finally
				{
					closeConnection();
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// wait for connection, then display info
	private void waitForConnection() throws IOException {
		System.out.println("Waiting for connection\n");
		
		// this will wait until it finds a connection
		connection = server.accept();
		
		System.out.println("New connection received from: " + connection.getInetAddress().getHostName());
	}
	
	// get streams to send and receive data over
	private void getStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();

		input = new ObjectInputStream(connection.getInputStream());
	}
	
	// these methods were also copied from intructor's server.java, but will
	// require modification.
	private void processConnection() throws IOException {
		
		terminal = new Scanner(System.in);
		
		Object clientMessage = "";
		Object serverMessage = "";
		while (!clientMessage.equals("TERMINATE")) {
			
			try {
				System.out.println("waiting on client");
				clientMessage = input.readObject();
				System.out.println("CLIENT: " + (String)clientMessage + "\n");
			}
			catch (ClassNotFoundException e) {
				System.out.println("Class wasnt found or some shit");
				System.exit(1);
			}
			
			System.out.print("SERVER: ");
			serverMessage = terminal.nextLine();
			sendData(serverMessage);
		}
		
	}
	
	
	// close streams and socket
	private void closeConnection() 
	{
		System.out.println("\nTerminating connection\n");
		try
		{
			output.close(); // close output stream
			input.close(); // close input stream
			connection.close(); // close socket
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// send message to client
	private void sendData(Object message)
	{
		try
		{
			output.writeObject(message);
			output.flush();
		}
		catch (IOException e) 
		{
			System.out.println("\nError writing object");
		}
	}
	
}