package csci6431;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 
        if (args.length != 1) {
            System.err.println("Usage: java csci6431.SocketServer <port number>");
            System.exit(1);
        }
         
        int port = Integer.parseInt(args[0]);
        
        System.out.println("Server starting, awaiting connections ...");
         
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            Socket clientSocket = serverSocket.accept();   
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    
            System.out.println("Connection from " + clientSocket.getRemoteSocketAddress());
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
        		System.out.println("Received Message: " + inputLine);
            	if (inputLine.compareToIgnoreCase("quit") == 0) {
            		out.println("> Good Bye!");
            		System.out.println("Closing connection to " + clientSocket.getRemoteSocketAddress());
            		serverSocket.close();
            		break;
            	}
                out.println("> " + inputLine);
            }
        } catch (IOException e) {
            System.out.println("We caught an exception while trying to listen on port "
                + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
