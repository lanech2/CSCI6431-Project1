package CSCI6431;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Random;

public class SocketClient {

	public SocketClient() {
		try {
			Socket socket = new Socket("127.0.0.1", 2000);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		Socket client;
		String syn = "SYN";
		String synack = "SYNACK";
		String wsp = " ";
		String eol = "\n";
		String userInput;
		String reply;
		Random rand = new Random();
		int seqA = rand.nextInt(Integer.MAX_VALUE);
		int seqB;
		
		try {
			client = new Socket(host, port);
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("Just connected to " + client.getRemoteSocketAddress());
			
			String hostIP = client.getInetAddress().getHostAddress();
			String hostname = client.getInetAddress().getHostName();
			String initMsg = syn + wsp + hostIP + wsp + hostname + wsp + seqA + eol;
			
			out.println(initMsg);
			reply = in.readLine();
		    System.out.println("returnFromServer: " + reply);
		    
		    String[] returnParts = reply.split("\\s");
		    String rtnHost = returnParts[1];
		    int rtnSeqA = Integer.parseInt(returnParts[3]);
		    seqB = Integer.parseInt(returnParts[4]);
		    System.out.println("rtnHost:" + rtnHost + "; rtnSeqA:" + rtnSeqA);
		    if (hostIP != rtnHost || rtnSeqA != (seqA + 1)) {
		    	out.close();
		    	in.close();
		    	stdIn.close();
		    	client.close();
		    } else {
		    	out.println(synack + wsp + hostIP + wsp + hostname + wsp + (seqA + 1 + 1) + wsp + (seqB + 1) + eol);
		    }
		    
			while ((userInput = stdIn.readLine()) != "quit") {
			    out.println(userInput);
			    System.out.println("echo: " + in.readLine());
			}
			
//			InputStream inFromServer = client.getInputStream();
//			DataInputStream in = new DataInputStream(inFromServer);
//			BufferedInputStream in = new BufferedInputStream(inFromServer);
//			System.out.println("Server says: " + in.read());
//			out.writeBytes("quit");
//			System.out.println("Server says: " + in.read());
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
