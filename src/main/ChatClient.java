package main;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatClient {

    String serverAddress;
    Scanner in;
    PrintWriter out;
    JFrame frame = new JFrame("Chatter");
    JTextField textField = new JTextField(50);
    JTextArea messageArea = new JTextArea(16, 50);
    private Set<String> names = new HashSet<>();
    private InetAddress Address;
    //ChatClient object takes in the server IP address as an argument
    //please input the 127.0.0.1 as an argument.
    public ChatClient(String serverAddress) {
        this.serverAddress = serverAddress;
        try {
			Address = InetAddress.getLocalHost();
			System.out.println(Address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        
        textField.setEditable(false);
        messageArea.setEditable(false);
        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();
        
        //INPUT BOX; Send on enter then clear to prepare for next message
        textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(textField.getText());
                textField.setText("");
            }
        });
    }
    
    private String getName() {
    	String name = JOptionPane.showInputDialog(
                frame,
                "Choose a screen name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE
            );
    	return name;
    }
    public Set<String> getNames() {
    	return names;
    	
    }
    public void setNames(String name) {
    	
    	names.add(name);
    	
    }
    //the client creates a socket to search for the port 789 in the IP provided
    //while loop encompasses message reading and writing to server
    private void run() throws IOException {
        try {
        	
            Socket socket = new Socket(serverAddress, 789);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            
            while (in.hasNextLine()) {
                String line = in.nextLine();
                if (line.startsWith("SUBMITNAME")) {
                    out.println(getName());
                } else if (line.startsWith("NAMEACCEPTED")) {
                    this.frame.setTitle("Chatter - " + line.substring(13));
                    textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    messageArea.append(line.substring(8) + "\n");
                } else if (line.startsWith("UPDATE")) {
                	setNames(line.substring(7).trim());
                	System.out.println(names);
                } else if (line.startsWith("CLEAR")) {
                	names.clear();
                }
            }
        } catch (Exception e){
        	System.out.println(e);
        	System.out.println("Must start server before a client can connect!");
        } finally {
            frame.setVisible(false);
            frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        ChatClient client = new ChatClient(args[0]);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
        
    }
}
