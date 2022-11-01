// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
import ocsf.server.ConnectionToClient;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  private ChatIF serverUI;
  private static EchoServer server;
  private boolean used;
  
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    used = false;
    server = this;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	
	  try {
		  String message = msg.toString();
		  if (message.startsWith("#login") && used) {
			  client.close();
		  }
		  else if (message.startsWith("#login")) {
			  String[] command = message.split(" ");
			  if (command.length == 2) {
				  client.setInfo(command[1], client.getInfo("loginID"));
				  used = true;
				  System.out.println(command[0] + " " + command[1]);
			  }
			  else {
				  System.out.println("Invalid command");
			  }
		  }
		  else {
			  System.out.println("Message received: " + msg + " from " + client.getInfo("loginID"));
			  this.sendToAllClients(client.getInfo("loginID") + ": " + msg);
		  }
	  }
	  catch (IOException e) {
		  serverUI.display("Could not do action.");
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Implements the hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
	@Override
  protected void clientConnected(ConnectionToClient client) {
		System.out.println("Client connected: " + client);
	}

  /**
   * Implements the hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
	@Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
		System.out.println("Client disconnected: " + client);
  	
  }
	
	/**
	* Implements the hook method called each time an exception is thrown in a
	* ConnectionToClient thread.
	* The method may be overridden by subclasses but should remains
	* synchronized.
	*
	* @param client the client that raised the exception.
	* @param Throwable the exception thrown.
	*/
	@Override
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
		System.out.println("Exception occured on client: " + client);
		System.out.println(exception);
		if (!(exception instanceof NumberFormatException)) {
			clientDisconnected(client);
		}
	}
	
	public void handleMessageFromServerUI(String message) {
		try {
			boolean used = false;
			if (message.startsWith("#")) {
				if (message.startsWith("#setport")) {
					String[] command = message.split(" ");
					if (command.length == 2) {
						setPort(Integer.parseInt(command[1]));
						serverUI.display("Your port was set to: " + command[1]);
						used = true;
					}
					else {
						serverUI.display("Invalid command");
					}
				}
				switch (message) {
					case "#quit":
						close();
						used = true;
						break;
					case "#stop":
						stopListening();
						used = true;
						break;
					case "#close":
						stopListening();
						close();
						used = true;
						break;
					case "#start":
						if (!isListening()) {
							listen();
						}
						used = true;
						break;
					case "#getport":
			    		String portNum = Integer.toString(getPort());
			    		serverUI.display("Your port is: " + portNum);
			    		used = true;
					default:
						if (!used) {
							serverUI.display("Invalid command");
						}
				}				
			}
			else {
				serverUI.display(message);
				server.display(message);
				this.sendToAllClients(message);
			}
		}
		catch (IOException e) {
		      serverUI.display("Could not do action.");
		}
		catch (NumberFormatException e) {
			serverUI.display("Please enter in a number for port");
		}
	}
	
	public void display(String message) {
		System.out.println(message);
	}
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    ServerConsole sc = new ServerConsole(port);
    EchoServer sv = new EchoServer(port, sc);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class