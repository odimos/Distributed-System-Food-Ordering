import java.io.*;
import java.net.*;

public class Server<M extends RequestHandler > {
	
	ServerSocket providerSocket;
	Socket connection = null;
	
	void openServer(M manager, int port) {
		try {
			providerSocket = new ServerSocket(port, 20);
			System.out.println("Server Waiting for clients on port: "+port);
			while (true) {
				connection = providerSocket.accept();

				Thread t = new ActionsForClients<M>(connection, manager);
				t.start();

			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

    // public static void main(String args[]) {
	// 	new Server().openServer();
	// }

}
