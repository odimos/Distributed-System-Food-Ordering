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
			//System.out.println("CONNECTING.. --> " + host + ":" + port);
			requestSocket = new Socket(host, port);
			requestSocket.setSoTimeout(15000); // wait up to 5s for readObject()

			out = new ObjectOutputStream(requestSocket.getOutputStream());
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			out.writeObject(task);
			out.flush();
			Answer res = (Answer) in.readObject();
			manager.handleResponseFromServer(res);
	
		} catch (UnknownHostException unknownHost) {
			System.err.println("You are trying to connect to an unknown host!");
		}  catch (SocketTimeoutException e) {
			// no answer in time
			// close socket and treat as failure
			System.out.println("Socket timed out while waiting for response from " + host + ":" + port);
		} 
		 catch (Throwable t) {
			t.printStackTrace();
		}finally {
			try { if (in != null) in.close(); } catch (Exception ignored) {}
			try { if (out != null) out.close(); } catch (Exception ignored) {}
			try { if (requestSocket != null) requestSocket.close(); } catch (Exception ignored) {}
		}
	}

}
