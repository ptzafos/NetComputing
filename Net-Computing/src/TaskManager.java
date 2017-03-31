import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TaskManager {
	
	private static BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Task Manager");
    private JTextArea messageArea = new JTextArea(8, 60);
    
    public static void main(String[] args) throws IOException{
		TaskManager tm = new TaskManager();
		tm.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tm.frame.pack();
		tm.frame.setVisible(true);
		tm.connectToServer();
	}
    
    public TaskManager(){
    	messageArea.setEditable(false);
        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
    }
	public void connectToServer() throws IOException {

        // Make connection and initialize streams
        Socket socket = new Socket("localhost", 9899);
        in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Consume the initial welcoming messages from the server
        for (int i = 0; i < 2; i++) {
            messageArea.append(in.readLine() + "\n");
        }
        while(true){
        	String response;
            try {
            	response = in.readLine();
            } catch (IOException ex) {
                   response = "Error: " + ex;
            }
            if(response!=null){
            	messageArea.append(response + "\n");
            }
        }
    }
}