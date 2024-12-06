import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;


// Client class was largely copied/referenced by instructor code
public class Client extends JFrame {
	
	private final Scanner userInput = new Scanner(System.in);
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket client;

	//Use local host and port 12345
	private final String host = "174.56.89.149";
	private final int portNumber = 12345;
	
	private JTextField textField;
	private JTextArea textArea;
	
	public Client() {
		
		super("Client");
		
		setLayout(null);
		textField = new JTextField("Tell me something");
		textField.setBounds(20,20,300,40);
		textArea = new JTextArea();
		textArea.setBounds(20,80,300,100);
		
		add(textField);
		add(textArea);
		
		textField.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					sendAndReceiveMessages(event);
				}
			}
		); 
		
		setSize(400, 300);
		setVisible(true);
	}
	
	private void sendAndReceiveMessages(ActionEvent event) {
		String clientMessage = event.getActionCommand();
		if (!clientMessage.equals("TERMINATE")) {
			
			textField.setText("");
			textField.update(textField.getGraphics());
			String serverMessage;
			
			System.out.println("sending data");
			textArea.append("\nMe: " + clientMessage);
			textArea.update(textArea.getGraphics());
			sendData(clientMessage);
			
			try {
				System.out.println("waiting on reply");
				serverMessage = (String)input.readObject();
				textArea.append("\nServer: " + serverMessage);
			}
			catch (ClassNotFoundException e) {
				System.out.println("what even causes this error?");
				System.exit(1);
			}
			catch (IOException e) {
				System.out.println("IOException");
				System.exit(1);
			}
			
		}
	}
	
	
	// connect to server and process messages from server
	public void runClient()
	{
		try
		{
			connectToServer();
			getStreams();
		}
		catch (EOFException e) 
		{
			System.out.println("\nClient terminated connection");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	// connect to server
	private void connectToServer() throws IOException
	{
		System.out.println("Attempting connection\n");

		// create Socket to make connection to server
		client = new Socket(InetAddress.getByName(host), portNumber);

		// display connection information
		System.out.println("Connected to: " + client.getInetAddress().getHostName());
	}

	// get streams to send and receive data
	private void getStreams() throws IOException
	{
		// set up output stream for objects
		output = new ObjectOutputStream(client.getOutputStream());
		
		output.flush(); // flush output buffer to send header information

		// set up input stream for objects
		input = new ObjectInputStream(client.getInputStream());

		System.out.println("\nGot I/O streams\n");
	}

	// close streams and socket
	private void closeConnection()
	{
		System.out.println("\nClosing connection");
		try
		{
			output.close(); // close output stream
			input.close(); // close input stream
			client.close(); // close socket
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// send message to server
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