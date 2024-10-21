package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.Date;

interface message{
	
	public static void setType() {	
	}
	
	public static void getMessage() {
		
	}
}
public class ChatServer {

    // All client names
    private static Set<String> names = new HashSet<>();
    //All handlers which act as object for the client, can find 
    private static ArrayList<Handler> clientList = new ArrayList<>(); 
    
     // The set of all the print writers for all the clients, used for broadcast.
    private static Set<PrintWriter> writers = new HashSet<>();
    
    private static Coordinator coordinatorHandler;
    
    private static InetAddress serverAddress;
    
    public static void main(String[] args) throws Exception {
        System.out.println("The chat server is running...");
        ExecutorService pool = Executors.newFixedThreadPool(200);
        try (ServerSocket listener = new ServerSocket(789)) {
        	serverAddress = InetAddress.getLocalHost();
        	System.out.println(serverAddress);
        	coordinatorHandler = new Coordinator(listener.accept(),true);
        	pool.execute(coordinatorHandler);
        	while (true) {
        		
        		pool.execute(new Handler(listener.accept(), false));
        		
        	}
        } catch (Exception e){
        	System.err.println("Cannot run new server whilst server is already running.");
    
        }
    }
    
    public static ArrayList<Handler> getClientList() {
    	return clientList;
    }

  
    //each message becomes an object which could have expanded upon manipulation
    //e.g editing written messages, deleting them etc.
    //CHANGE VISIBILITY TO PRIVATE AFTER JUNIT
    public static class messageType implements message{
    	private String type = "message";
    	private String reciever;
    	private String input;
    	private String sender;
    	
    	//constructor checks each message type, defaults to "message" type.
    	//
    	public messageType(String input, String sender) {
    		this.input = input;
    		this.sender = sender;
    		
    	}	
    	
    	//overrides messageInterface.setType which.
    	public void setType() {
    		if (input.toLowerCase().startsWith("/quit")) {
        		type = "quit";
            } else if (input.toLowerCase().startsWith("/whisper ")) {
            	String[] inputList = input.split(" ");
            	reciever = inputList[1];
            	for (String nameInList : names) {
            		if (reciever.equals(nameInList) && !sender.equals(reciever)) {
            			type = "successful whisper";
            			return;
            		} else {
            			type = "incorrect username whisper";
            		}
            	}
            }
    	}
    	public String getType(){
    		return type;
    	}
    	
    	public String getName() {
    		return reciever;
    	}
    	
    	public String getMessage() {
    		String inputFormatted = input.substring(9+reciever.length(), input.length());
    		return inputFormatted;
    	}
    }
    //CHANGE VISIBILITY TO PRIVATE AFTER JUNIT
    //coordinator object is set to work for a client that is assigned coordinator.
    //will update client's list of user names
    public static class Coordinator extends Handler {
    	private String name;
    	Socket socket;

		public Coordinator(Socket socket, boolean coordinator) {
			super(socket, coordinator);
			this.socket = socket;
		}
		
		public String getCoordinatorName() {
			for (Handler client: getClientList()) {
				if (client.isCoordinator){
					name = client.getName();
				}
			}
			return name;
		}
		public void tellCoordinator() {
			for (Handler client: getClientList()) {
				if (client.isCoordinator) {
					client.out.println("MESSAGE "+"You are the coordinator.");
				}
			}
		}
		public void setNewCoordinator() {
			if (getClientList().size() > 1){
            	if (getcoordinator()) {
                	setcoordinator(getClientList().get(0));
            	}
        	} else if (getClientList().size() == 1)  {
        		setcoordinator(getClientList().get(0));
        	} else {
        		return;
        	}
		}
		
		//will update clients' list of names, literally the slowest thing i've made
		//
		public void updateClientNames(String name) {
			
			for (Handler client: getClientList()) {
				
				client.out.println("CLEAR ");
	
				for (String i : names) {
					String update = ("UPDATE "+i);
					client.out.println(update);
				}
				
				
				
			}
			
		}

    }
    //CHANGE VISIBILITY TO PRIVATE AFTER JUNIT
    //server dedicates an object to a client that connects.
    
    public static class Handler implements Runnable {
        private String name;
        private Socket socket;
        private Scanner in;
        private PrintWriter out;
        private boolean isCoordinator;
        
        //client handler will construct one per client that is connected to the server
        public Handler(Socket socket,boolean coordinator) {
        	
            this.socket = socket;
            this.isCoordinator = coordinator;
            getClientList().add(this);
           
        }
        
        public boolean getcoordinator() {
        	return this.isCoordinator;
        }
        public void setcoordinator(Handler handler) {
        	handler.isCoordinator = true;
        }
        
        public String getName() {
        	return name;
        }
        
        public void setName(String name) {
        	this.name = name;
        }
       
        //continuously gets i/o and checks if name exists already or is null
        //
        public void run() {
            try {
            	
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);
                
                //Keep requesting an id until unique is found
                while (true) {
                    out.println("SUBMITNAME");
                    name = in.nextLine();

                    if (name.equals("null")) {
                    	return;
                    
                    }
                    
                    //locks names so that all handlers in the server have the same list
                    //checks for unique ID, in this case a name.
                    synchronized(names) {
                    if (!name.isEmpty() && !names.contains(name)) {
                    	names.add(name);
                    	if (name != null);
                    		coordinatorHandler.updateClientNames(name);
                    	if (getClientList().size() == 1) {
                    		coordinatorHandler.setNewCoordinator();
                    		ChatServer.coordinatorHandler.tellCoordinator();
                    	}
                        break;
                        }
                    }
                }
                

                
                
                out.println("NAMEACCEPTED " + name);
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " has joined");
                    
                }
                writers.add(out);
                //
                //after 'registration' of user, main loop happens here
                //the loop consists of reading input of the user and sending output
                //to other users
                while (true) {
                
                    String input = in.nextLine();
                    Date timeStamp = new Date();
                    messageType message = new messageType(input, name);
                    message.setType();
                    if (message.getType().equals("message")) {
                    	for (PrintWriter writer: writers) {
                    		writer.println("MESSAGE "+ timeStamp+" " + name +": " + input);
                    	}
                    } else if (message.getType().equals("successful whisper")) {
                    	
                    	if (names.contains(message.getName())) {
                    		
                    		out.println("MESSAGE "+timeStamp+" "+"[WHISPER] "+ name + ": "+ message.getMessage());
                    		
                    		for (Handler client : getClientList()) {
                    			if (client.name.equals(message.getName())) {
                    				client.out.println("MESSAGE "+timeStamp+" "+"[WHISPER] "+ name +": " + message.getMessage());
                    			}
                    		}
                    	}
                    	//user feedback 
                    } else if (message.getType().equals("incorrect username whisper")) {
                    	out.println("MESSAGE "+"User is incorrect");
                    	
                    } else if (message.getType().equals("quit")) {
                    	
                    	//leaves exception loop and goes to finally
                    	return;
                    }
                	
            	}
            } catch (Exception e) {
                System.out.println(e);
            //   
            //After the Handler socket disconnects; reorganises coordinator and removes client.
            //
            } finally {
            	 
            	
            	//deletes this name from list
            	if (name != null) {
	            	names.remove(name);
	                for (PrintWriter writer : writers) {
	                	writer.println("MESSAGE " + name + " has left");
	                }
            	}
            	//new coordinator is picked when he leaves
            	//removes handler
            	getClientList().remove(this);
            	coordinatorHandler.updateClientNames(name);
            	if (getcoordinator()) {
            		ChatServer.coordinatorHandler.setNewCoordinator();
            		ChatServer.coordinatorHandler.tellCoordinator();
            	}
            	
            	//deletes this writer from list
            	if (out != null) {
            		writers.remove(out);
            	}
                
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
 
}
