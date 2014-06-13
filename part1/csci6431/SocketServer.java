package csci6431;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }
         
        int portNumber = Integer.parseInt(args[0]);
        
        System.out.println("Server starting, awaiting connections ...");
         
        try (
            ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[0]));
            Socket clientSocket = serverSocket.accept();   
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);                   
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
        	System.out.println("Connection from " + clientSocket.getRemoteSocketAddress());
        	String inputLine;
            while ((inputLine = in.readLine()) != null) {
            	if (inputLine.compareToIgnoreCase("quit") == 0) {
            		System.out.println("Caught the quit!");
            		break;
            	}
                out.println("> " + inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

}
