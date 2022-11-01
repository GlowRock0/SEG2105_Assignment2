import java.io.*;
import java.util.Scanner;
import common.*;

public class ServerConsole implements ChatIF {
	
	final public static int DEFAULT_PORT = 5555;
	EchoServer server;
	Scanner fromConsole;
	
	public ServerConsole(int port) {
		server = new EchoServer(port, this);
		fromConsole = new Scanner(System.in);
	}
	
	public void display(String msg) {
		System.out.println("SERVER MSG> " +msg);
	}
	
	public void accept() {
		try {
			String message;
			while (true) {
				message = fromConsole.nextLine();
				server.handleMessageFromServerUI(message);
			}
		}
		catch (Exception e) {
			System.out.println(e);
			System.out.println("Unexpected error while reading from console!");
		}
	}
	
	public static void main(String args[]) {
		
		int port = 0;
		
		try {
			port = Integer.parseInt(args[0]);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			port = DEFAULT_PORT;
		}
		catch (NumberFormatException e) {
			port = DEFAULT_PORT;
		}
		
		ServerConsole server = new ServerConsole(port);
		server.accept();
	}
}