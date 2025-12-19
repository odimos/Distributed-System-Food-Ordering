
import java.io.*;
import java.net.*;

import data.Answer;
import data.Task;

public class ActionsForClients<M extends RequestHandler> extends Thread {
	ObjectInputStream in;
	ObjectOutputStream out;
    M manager;

	public ActionsForClients(Socket connection, M manager ) {
        this.manager = manager;
		try {
			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
		
			Task task = (Task) in.readObject();
            Answer response_to_send = manager.handleRequestFromClient(task);
			out.writeObject(response_to_send);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}
}
