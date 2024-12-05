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
	private final String host = "127.0.0.1";
	private final int portNumber = 12345;
	
	private JTextField textField;
	private JTextArea textArea;
	
	public Client() {
		
		super("Client");
		
		setLayout(new FlowLayout());
		textField = new JTextField("Enter file name");
		textArea = new JTextArea();
		add(textField);
		add(textArea);
		
		textField.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					String message = event.getActionCommand();
					if (!message.equals("TERMINATE")) {
						getAndSendFiles(event);
						getAndPrintResult();
					}
					else {
						closeConnection();
					}
				}
			}
		); 
		
		setSize(400, 300);
		setVisible(true);
	}
	
	private void getAndSendFiles(ActionEvent event) {
		Scanner file = getFile(event.getActionCommand());
		int rows = file.nextInt();
		int cols = file.nextInt();
					
		int[][] matrix1 = matrixFromFile(rows, cols, file);
		int[][] matrix2 = matrixFromFile(rows, cols, file);
					
		System.out.println("sending matrices");
		sendData(matrix1);
		sendData(matrix2);
		System.out.println("sent");
	}
	
	private void getAndPrintResult() {
		int[][] result;
		try {
			result = (int[][]) input.readObject();
			System.out.println("result received");
			String matrixAsString = getString(result);
			textArea.append("\n" + matrixAsString);
		}
		catch (IOException e) {
			
		}
		catch (ClassNotFoundException e) {
			
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
	
	
	
	// copied this from readwritedata.java
	private Scanner getFile(String fileName) {
		File file = new File(fileName);
		Scanner input = null;
		try {
			input = new Scanner(file);
		} catch (FileNotFoundException e) {
			System.out.printf("%nError on file: %s (either enpty or wrong file format)%n%n", file); 
			System.exit(1);
		}
		
		return input;
	}
	
	// copied this from my matrix addition
	public static int[][] matrixFromFile(int rows, int cols, Scanner fileReader) {
		int[][] matrix = new int[rows][cols];
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				// read in the values and put in the proper places in the array
				matrix[r][c] = fileReader.nextInt();
			}
		}
		
		return matrix;
	}
	
	// get string of a given matrix
	private String getString(int[][] array) {
		String result = "";
		
		for (int row = 0; row < array.length; row++) {
			for (int col = 0; col < array[row].length; col++) {
				// add each number
				result = result + String.format("%-4d", array[row][col]);
			}
			// new line for every row
			result = result + "\n";
		}
		return result;
	}
}