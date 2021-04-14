package mizzou.spot.connection;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.

import java.util.ArrayList;

import org.apache.*;

public class Connection
{
	private final String SPOT_ADDRESS_WIRED = "10.0.0.4";
	private final String SPOT_ADDRESS_WIRELESS = "192.168.80.3";
	private final String SPOT_AUTH_SERVER = "/accounts/jwt/generate/";
	
	private final ConnectionType connectionType;
	
	public Connection(ConnectionType connectionType)
	{
		this.connectionType = connectionType;
	}
	
	public String getSpotAddress()
	{
		if(connectionType == ConnectionType.WIRED)
		{
			return SPOT_ADDRESS_WIRED;
		}
		else if(connectionType == ConnectionType.WIRELESS)
		{
			return SPOT_ADDRESS_WIRELESS;
		}
		else
		{
			return null;
		}
	}
	
	public String getSpotAuthServer()
	{
		String address = getSpotAddress();
		
		if(address == null)
		{
			return null;
		}
		
		return address + SPOT_AUTH_SERVER;
	}
	
	/**
	 * Sends POST request with credentials to Spot authentication server and retrieves a new bearer token.
	 * A Bearer token is required in request headers to verify access to Spot's resources.
	 *  
	 *  @param username the name of the account you wish to authenticate as
	 *  @param password the password for the account you wish to authenticate as
	 *  @return a bearer token that gives access to Spot's resources
	 */
	public String getBearerToken(final String username, final String password)
	{
		if (username == null || password == null)
		{
			return null;
		}
		
		String payload = "{'username': " + username + ", 'password': " + password + "}";
		
		return null;
	}

}
