import java.io.*;
import java.net.*;

import data.Answer;
import data.Task;

public class Client<M extends ResponseHandler> extends Thread  {

    String host; 
	Task task;
    int port;
    M manager;
	public Client( Task task, String host, int port, M manager) {
		this.task = task;
        this.host = host;
        this.port = port;
        this.manager = manager;
	}
	
	public void run() {
		Socket requestSocket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
		try {
			requestSocket = new Socket(host, port);
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			out.writeObject(task);
			out.flush();
			Answer res = (Answer) in.readObject();
			manager.handleResponseFromServer(res);
	
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();	out.close();
				requestSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

}
