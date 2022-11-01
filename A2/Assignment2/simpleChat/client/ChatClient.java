// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import ocsf.server.ConnectionToClient;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try {
    	
    	boolean used = false;
    	
    	if (message.startsWith("#")) {
    		if (message.startsWith("#sethost")) {
    			String[] command = message.split(" ");
    			if (command.length == 2) {
    				setHost(command[1]);
    				clientUI.display("Your host was set to: " + command[1]);
    				used = true;
    			}
    			else {
    				clientUI.display("Invalid command");
    			}
    		}
    		else if (message.startsWith("#setport")) {
    			String[] command = message.split(" ");
    			if (command.length == 2) {
    				setPort(Integer.parseInt(command[1]));
    				clientUI.display("Your port was set to: " + command[1]);
    				used = true;
    			}
    			else {
    				clientUI.display("Invalid command");
    			}
    		}
    		switch (message) {
		    	case "#quit":
		    		quit();
		    		used = true;
		    		break;
		    	case "#logoff":
		    		closeConnection();
		    		used = true;
		    		break;
		    	case "#login":
		    		openConnection();
		    		clientUI.display("Connected");
		    		used = true;
		    		break;
		    	case "#gethost":
		    		String host = getHost();
		    		clientUI.display("Your host is called: " + host);
		    		used = true;
		    		break;
		    	case "#getport":
		    		String portNum = Integer.toString(getPort());
		    		clientUI.display("Your port is: " + portNum);
		    		used = true;
		    		break;
		    	default:
		    		if (!used) {
		    			clientUI.display("Invalid command");
		    		}
	    	}
    	}
    	else {
    		sendToServer(message);
    	}
    }
    catch(IOException e) {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
    catch (NumberFormatException e) {
    	clientUI.display("Please enter in a number for port");
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
	/**
	 * Implements the hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("Connection closed");
	}
  	
	/**
	 * Implements the hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  	@Override
	protected void connectionException(Exception exception) {
  		clientUI.display("The server has shut down");
  		quit();
	}
}
//End of ChatClient class