import java.net.*;
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class Server {

	private static final int sPort = 8000;   //The server will be listening on this port number

	public static void main(String[] args) throws Exception {
		System.out.println("The server is running."); 
		ServerSocket listener = new ServerSocket(sPort);
		int clientNum = 1;
		ArrayList<Handler> handlerList= new ArrayList<Handler> ();
		try {
			while(true) {
			
				Handler h = new Handler(listener.accept(), handlerList);
				handlerList.add(h);
				h.start();
				System.out.println("Client "  + clientNum + " is connected!" + 	h.connection + h.message);
				clientNum++;
			}
		} finally {
			listener.close();
		} 

	}

	
	/**
	 * A handler thread class.  Handlers are spawned from the listening
	 * loop and are responsible for dealing with a single client's requests.
	 */
	private static class Handler extends Thread {
		private String message;    //message received from the client
		private String MESSAGE;    //uppercase message send to the client
		private Socket connection;
		private ObjectInputStream in;	//stream read from the socket
		private ObjectOutputStream out;    //stream write to the socket
		private int no;		//The index number of the client
		ArrayList<Handler> handlerList1 = new ArrayList<Handler> ();

		public Handler(Socket connection, ArrayList<Handler> handlerList) {
			// TODO Auto-generated constructor stub
			this.connection = connection;
			this.handlerList1 = handlerList;
			this.no = handlerList.size() + 1;
		}

		/*public Handler Handler(Socket connection, int no) {
			this.connection = connection;
			this.no = no;
			return this;
		}*/

		public void run() {
			try{
				//initialize Input and Output streams
				out = new ObjectOutputStream(connection.getOutputStream());
				out.flush();
				in = new ObjectInputStream(connection.getInputStream());
				
				try{
					while(true)
					{
						//receive the message sent from the client
						message = (String)in.readObject();
						//show the message to the user
						System.out.println("Receive message: " + message + " from client " + no);
						//Capitalize all letters in the message
						MESSAGE = message.toUpperCase();
						//send MESSAGE back to the client
						for(Handler h:handlerList1){
							if(h!=null && h!=this){
								h.sendMessage(MESSAGE);
							}
						}
						//sendMessage(MESSAGE);
					}
				}
				catch(ClassNotFoundException classnot){
					System.err.println("Data received in unknown format");
				}
			}
			catch(IOException ioException){
				System.out.println("Disconnect with Client " + no);
			}
			finally{
				//Close connections
				try{
					in.close();
					out.close();
					connection.close();
				}
				catch(IOException ioException){
					System.out.println("Disconnect with Client " + no);
				}
			}
		}

		//send a message to the output stream
		public void sendMessage(String msg)
		{
			try{
				out.writeObject(msg);
				out.flush();
			    System.out.println("Send message: " + msg + " to Client " + no);
			}
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}

	}

}
