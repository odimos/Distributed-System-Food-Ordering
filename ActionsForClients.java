
import java.io.*;
import java.net.*;

import data.Answer;
import data.Task;

public class ActionsForClients<M extends RequestHandler> extends Thread {
	private final Socket connection;
    private final M manager;

	ObjectInputStream in;
	ObjectOutputStream out;

    public ActionsForClients(Socket connection, M manager) {
        this.connection = connection;
        this.manager = manager;
    }

	public void run() {
		try {

			connection.setSoTimeout(5000);
			out = new ObjectOutputStream(connection.getOutputStream());
       		in = new ObjectInputStream(connection.getInputStream());

			Task task = (Task) in.readObject();
            Answer response_to_send = manager.handleRequestFromClient(task);
			out.writeObject(response_to_send);
			out.flush();
		} catch (Throwable t) {
        	t.printStackTrace();
		} finally {
			try { if (in != null) in.close(); } catch (Exception ignored) {}
			try { if (out != null) out.close(); } catch (Exception ignored) {}
			try { connection.close(); } catch (Exception ignored) {}
		}
	}
}
