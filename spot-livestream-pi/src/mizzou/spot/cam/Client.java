package mizzou.spot.cam;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpResponse;

public class Client
{
	private static final String SPOT_ADDRESS_WIRED = "10.0.0.4";
	private static final String SPOT_ADDRESS_WIRELESS = "192.168.80.3";
	private static final String SPOT_AUTH_SERVER_WIRED = SPOT_ADDRESS_WIRED + "/accounts/jwt/generate/";
	private static final String SPOT_AUTH_SERVER_WIRELESS = SPOT_ADDRESS_WIRELESS + "/accounts/jwt/generate/";
	private static final String SPOT_CAM_PORT = "31102";
	private static final String SPOT_CAM_SERVER_WIRED = SPOT_ADDRESS_WIRED + ":" + SPOT_CAM_PORT + "/h264.sdp/";
	
	private CloseableHttpClient httpClient;
	
	public Client()
	{
		httpClient = HttpClients.createDefault();
		
		
		
	}
	
	/**
	 * Sends POST request with credentials to Spot authentication server and retrieves a new bearer token.
	 * A Bearer token is required in request headers to verify access to Spot's resources.
	 *  
	 *  @param username the name of the account you wish to authenticate as
	 *  @param password the password for the account you wish to authenticate as
	 *  @return a bearer token that gives access to Spot's resources
	 */
	private String getBearerToken(final String username, final String password, boolean wireless);
	{
		HttpPost request = new HttpPost();
		
		
		return null;
	}

}
