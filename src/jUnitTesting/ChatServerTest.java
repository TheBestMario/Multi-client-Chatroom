package jUnitTesting;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import main.ChatServer;

class ChatServerTest {
	ChatServer server;
	
	@Test
	void testMain() {
		var server = new ChatServer();
		
		assertEquals(server,server);
	}

}
