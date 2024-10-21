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


public class newChatClient {

    String serverAddress;
    Scanner in;
    static PrintWriter out;
    private Set<String> names = new HashSet<>();
    private static InetAddress Address;
    GUI gui;
    //ChatClient object takes in the server IP address as an argument
    //please input the 127.0.0.1 as an argument.
    public newChatClient(String serverAddress) {
        this.serverAddress = serverAddress;
        this.gui = gui;
        try {
			Address = InetAddress.getLocalHost();
			System.out.println(Address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        
    }
    
    private String getName() {
    	return gui.drawNameInput();
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
                    gui.frame.setTitle("Chatter - " + line.substring(13));
                    gui.textField.setEditable(true);
                } else if (line.startsWith("MESSAGE")) {
                    gui.messageArea.append(line.substring(8) + "\n");
 
                } else if (line.startsWith("UPDATE")) {
                	setNames(line.substring(7).trim());
                } else if (line.startsWith("CLEAR")) {
                	names.clear();
                }
            }
        } catch (Exception e){
        	System.out.println(e);
        	System.out.println("Must start server before a client can connect!");
        } finally {
            gui.frame.setVisible(false);
            gui.frame.dispose();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        newChatClient client = new newChatClient(args[0]);
        GUI gui = new GUI();
        gui.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.frame.setVisible(true);
        client.run();
        
        
    }
    public static class GUI {
    	JFrame frame = new JFrame("Chatter");
        JTextField textField = new JTextField(50);
        JTextArea messageArea = new JTextArea(16, 50);
    	public GUI() {
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
    	public String drawNameInput() {
    		String name = JOptionPane.showInputDialog(
                    frame,
                    "Choose a screen name:",
                    "Screen name selection",
                    JOptionPane.PLAIN_MESSAGE
                );
    		return name;
    	}
    }
}

