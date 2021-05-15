package mizzou.spot.cam;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import mizzou.spot.connection.Connection;
import mizzou.spot.connection.ConnectionType;

public class Client
{
	//private static final String SPOT_CAM_PORT = "31102";
	//private static final String SPOT_CAM_SERVER; //_WIRED = SPOT_ADDRESS_WIRED + ":" + SPOT_CAM_PORT + "/h264.sdp/";
	
	private static Connection connection;
	private static String username;
	private static String password;
	
	public static void main(String[] args)
	{
		if(args.length != 3)
		{
			System.err.println("Invalid number of arguments.\nRun with arguments (WIRED/WIRELESS username password).");
			System.exit(1);
		}
		
		final ConnectionType connectionType = args[0].equalsIgnoreCase("wired") ? ConnectionType.WIRED
				: args[0].equalsIgnoreCase("wireless") ? ConnectionType.WIRELESS : null;
		
		if(connectionType == null) 
		{
			System.err.println("Unknown connection type. Use (WIRED/WIRELESS).");
			System.exit(1);
		}
		
		username = args[1];
		password = args[2];
		
		try
		{
			connection = new Connection(connectionType, username, password);
		} 
		catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.print("Connected with bearer token: ");
		System.out.println(connection.getBearerToken());
		
	}
}
