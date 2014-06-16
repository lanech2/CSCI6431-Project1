package csci6431;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class SocketClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		Socket client;
		String syn = "SYN";
		String synack = "SYN-ACK";
		String wsp = " ";
//		String eol = "\\n";
		String reply;
		Random rand = new Random();
		int seqA = rand.nextInt(Integer.MAX_VALUE);
		
		try {
			client = new Socket(host, port);
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			
			//this should be the server
			String hostIP = client.getInetAddress().getHostAddress();
			//this should be the server
			String hostName = client.getInetAddress().getHostName();
			
			String clientIP = client.getInetAddress().getLocalHost().getHostAddress();
			String clientName = "ChrisClient";
			
			System.out.println(hostIP + ", " + hostName + ", " + clientIP + ", " + clientName);
			
			String initMsg = syn + wsp + clientIP + wsp + clientName + wsp + seqA;
			System.out.println("Sending: " + initMsg);
			out.println(initMsg);
			reply = in.readLine();
		    System.out.println("returnFromServer: " + reply);
		    
		    String[] returnParts = reply.split("\\s");
		    String serverIP1 = returnParts[1];
		    String serverName1 = returnParts[2];
		    int rtnSeqA1 = Integer.parseInt(returnParts[3]);
		    int rtnSeqB1 = Integer.parseInt(returnParts[4]);

		    if (!serverIP1.equals(hostIP) || rtnSeqA1 != (seqA + 1)) {
		    	System.out.println("rtnHost is " + serverIP1 + " but should be " + hostIP);
		    	out.close();
		    	in.close();
		    	stdIn.close();
		    	client.close();
		    	System.exit(-1);
		    } else {
		    	String msg2 = synack + wsp + clientIP + wsp + clientName + wsp + (seqA + 1 + 1) + wsp + (rtnSeqB1 + 1);
		    	System.out.println("Sending: " + msg2);
		    	out.println(msg2);
		    }

		    reply = in.readLine();
			returnParts = reply.split("\\s");
			String serverIP2 = returnParts[1];
			String serverName2 = returnParts[2];
			int rtnSeqA2 = Integer.parseInt(returnParts[3]);
			int rtnSeqB2 = Integer.parseInt(returnParts[4]);
			if (!serverIP2.equals(serverIP1) || !serverName2.equals(serverName1) || rtnSeqA2 != (rtnSeqA1 + 2) || rtnSeqB2 != (rtnSeqB1 + 2)) {
				System.out.println("Handshake failed!");
				client.close();
			} else {
				System.out.println("returnFromServer: " + reply);
				String msg3 = "I'm done with project 1!";
				System.out.println("Sending: " + msg3);
				out.println(msg3);
			}
		    
			System.out.println("Closing connection!");
			client.close();
		} catch (UnknownHostException e) {
			System.out.println("Caught UnknownHostException: " + e.getMessage());
		} catch (EOFException e) {
			System.out.println("Caught EOFException: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.exit(1);
	}
}
