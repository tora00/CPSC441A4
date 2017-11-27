package main;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import cpsc441.a4.shared.*;

/**
 * Router Class
 * 
 * This class implements the functionality of a router
 * when running the distance vector routing algorithm.
 * 
 * The operation of the router is as follows:
 * 1. send/receive HELLO message
 * 2. while (!QUIT)
 *      receive ROUTE messages
 *      update mincost/nexthop/etc
 * 3. Cleanup and return
 * 
 *      
 * @author 	Majid Ghaderi
 * @version	3.0
 *
 */
public class Router {
	private int routerId;
	private String serverName;
	private int serverPort;
	private int updateInterval;
	private OutputStream output;
	private InputStream input;
	private ObjectOutputStream oout;
	private ObjectInputStream oinp;
	private RtnTable rtn;
	
    /**
     * Constructor to initialize the rouer instance 
     * 
     * @param routerId			Unique ID of the router starting at 0
     * @param serverName		Name of the host running the network server
     * @param serverPort		TCP port number of the network server
     * @param updateInterval	Time interval for sending routing updates to neighboring routers (in milli-seconds)
     */
	public Router(int routerId, String serverName, int serverPort, int updateInterval) {
		// to be completed
		this.routerId = routerId;
		this.serverName = serverName;
		this.serverPort = serverPort;
		this.updateInterval = updateInterval;
		rtn = null;
	}

    /**
     * starts the router 
     * 
     * @return The forwarding table of the router
     */
	public RtnTable start() {
		// to be completed
		try {
			Socket socket = new Socket(serverName, serverPort);
			DvrPacket outhello = createHello();
			output = socket.getOutputStream();
			input = socket.getInputStream();
			oout = new ObjectOutputStream(output);
			oinp = new ObjectInputStream(input);
			oout.writeObject(outhello);					// sends HELLO packet
			DvrPacket inhello = (DvrPacket)oinp.readObject();	// stores HELLO packet response
			if(inhello.type==DvrPacket.HELLO) {			// initial HELLO response
				System.out.println("Initial packet received");
				
				while((inhello=(DvrPacket)oinp.readObject()).type!=DvrPacket.QUIT) {
					System.out.println("Receiving packet");
				}
				System.out.println("Quit received. Shutting down");
			}
			else {
				// TODO Process non-HELLO reply;
				System.out.println(inhello.toString());
			}
			
			oinp.close();
			oout.close();
			input.close();
			output.close();
			socket.close();
			
		}
		catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return new RtnTable();
	}
	
	private DvrPacket createHello() {
		DvrPacket dvr = new DvrPacket(routerId, DvrPacket.SERVER, DvrPacket.HELLO);
		//dvr.type = dvr.HELLO;
		return dvr;
	}

	
	
    /**
     * A simple test driver
     * 
     */
	public static void main(String[] args) {
		// default parameters
		int routerId = 0;
		String serverName = "localhost";
		int serverPort = 2227;
		int updateInterval = 1000; //milli-seconds
		
		if (args.length == 4) {
			routerId = Integer.parseInt(args[0]);
			serverName = args[1];
			serverPort = Integer.parseInt(args[2]);
			updateInterval = Integer.parseInt(args[3]);
		} else {
			System.out.println("incorrect usage, try again.");
			System.exit(0);
		}
			
		// print the parameters
		System.out.printf("starting Router #%d with parameters:\n", routerId);
		System.out.printf("Relay server host name: %s\n", serverName);
		System.out.printf("Relay server port number: %d\n", serverPort);
		System.out.printf("Routing update intwerval: %d (milli-seconds)\n", updateInterval);
		
		// start the router
		// the start() method blocks until the router receives a QUIT message
		Router router = new Router(routerId, serverName, serverPort, updateInterval);
		RtnTable rtn = router.start();
		System.out.println("Router terminated normally");
		
		// print the computed routing table
		System.out.println();
		System.out.println("Routing Table at Router #" + routerId);
		System.out.print(rtn.toString());
	}

}
