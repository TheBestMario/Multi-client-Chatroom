package jUnitTesting;
import main.ChatServer;
import main.ChatServer.Handler;
import main.ChatServer.Coordinator;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;



/*
 * 
 *PLEASE NOTE THAT YOU MUST SET OBJECTS TO PUBLIC WHEN TESTING
 *
 *
 */
class CoordinatorTest {
	Socket socket;
	@Test
	void testGetCoordinatorName() {
		Socket socket;
		ArrayList<Handler> clientList = new ArrayList<>();
		try {
			socket = new Socket("127.0.0.1", 789);
			Handler handler1 = new Handler(socket, true);
			clientList.add(handler1);
			Handler handler2 = new Handler(socket, false);
			clientList.add(handler2);
			Handler handler3 = new Handler(socket, false);
			clientList.add(handler3);
			Handler handler4 = new Handler(socket, false);
			clientList.add(handler4);
			Handler handler5 = new Handler(socket, false);
			clientList.add(handler5);
			clientList.get(0).setName("jay");
			clientList.get(1).setName("mario");
			clientList.get(2).setName("ves");
			clientList.get(3).setName("jacob");
			clientList.get(4).setName("chris");
			for (Handler client: clientList) {
				if (client.getcoordinator()){
					assertEquals(client.getName(),"jay");
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	@Test
	void testTellCoordinator() {

		boolean MessageExists = false;

		ArrayList<Handler> clientList = new ArrayList<>();
		try {
			socket = new Socket("127.0.0.1", 789);
			Coordinator handler1 = new Coordinator(socket, true);
			clientList.add(handler1);
			Handler handler2 = new Handler(socket, false);
			clientList.add(handler2);
			Handler handler3 = new Handler(socket, false);
			clientList.add(handler3);
			Handler handler4 = new Handler(socket, false);
			clientList.add(handler4);
			Handler handler5 = new Handler(socket, false);
			clientList.add(handler5);
			clientList.get(0).setName("jay");
			clientList.get(1).setName("mario");
			clientList.get(2).setName("ves");
			clientList.get(3).setName("jacob");
			clientList.get(4).setName("chris");
			for (Handler client: clientList) {
				if (client.getcoordinator()) {
					MessageExists = true;
					assertTrue(MessageExists);
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testSetNewCoordinator() {
		Socket socket;
		
		try {
			socket = new Socket("127.0.0.1", 789);
			Coordinator handler1 = new Coordinator(socket, true);
			Handler handler2 = new Handler(socket, false);
			Handler handler3 = new Handler(socket, false);
			Handler handler4 = new Handler(socket, false);
			Handler handler5 = new Handler(socket, false);
					
			handler1.setName("mario");
			handler2.setName("chris");
			handler3.setName("jay");
			handler4.setName("ves");
			handler5.setName("Ayse");
			
			ChatServer.getClientList().remove(0);
			
			handler1.setNewCoordinator();
			assertEquals(handler2.getcoordinator(),true);
			
	    	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Ignore
	//this is supposed to send data to the port and then ChatClient will update its' list with data given.
	void testSetNames() {
		Coordinator handler1 = new Coordinator(socket, true);
		handler1.setName("Jay");
		handler1.updateClientNames("Jay");
	}

	@Test
	public void testHandler() {
	     Socket socket;
		try {
			socket = new Socket("127.0.0.1", 789);
			Handler[] handlers = {
					new Handler(socket, true),
					new Handler(socket, false),
					new Handler(socket, false),
					new Handler(socket, false),
					new Handler(socket, false)
			};
			boolean CoordinatorExists = false;
			for (Handler handler : handlers) {
				if (handler.getcoordinator() == true) {
					CoordinatorExists = true;
					break;
				}
			}
			assertTrue(CoordinatorExists);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


				


	}

	@Test
	void testGetcoordinator() {
		//fail("Not yet implemented");
	}

	@Test
	void testSetcoordinator() {
		//fail("Not yet implemented");
	}

	@Test
	void testGetName() {
		//fail("Not yet implemented");
	}

	@Test
	void testRun() {
		//fail("Not yet implemented");
	}

}
