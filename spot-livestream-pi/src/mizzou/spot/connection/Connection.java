package mizzou.spot.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;

public class Connection
{
	private final String SPOT_ADDRESS_WIRED = "https://10.0.0.4";
	private final String SPOT_ADDRESS_WIRELESS = "https://192.168.80.3";
	private final String SPOT_AUTH_SERVER = "/accounts/jwt/generate/";
	
	private final ConnectionType connectionType;
	private final CloseableHttpClient httpClient;
	private String bearerToken;
	
	public Connection(ConnectionType connectionType, String username, String password) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, ClientProtocolException, IOException
	{
		// default trust strategy will be to trust all certificates, should change at some point to only the certs we need
		TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
		SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);

		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslsf).register("http", new PlainConnectionSocketFactory()).build();
		
		BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(socketFactoryRegistry);

		this.connectionType = connectionType;
		this.httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).setConnectionManager(connectionManager).build();
		this.bearerToken = getNewBearerToken(username, password);
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
	 * Tokens expire after 12 hours but can be refreshed.
	 *  
	 * @param username the name of the account you wish to authenticate as
	 * @param password the password for the account you wish to authenticate as
	 * @return a bearer token that gives access to Spot's resources
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	protected String getNewBearerToken(final String username, final String password) throws ClientProtocolException, IOException
	{
		if (username == null || password == null)
		{
			return null;
		}
		
		// build a post request to authentication server with username and password body
		HttpPost request = new HttpPost(getSpotAuthServer());
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		request.setEntity(new UrlEncodedFormEntity(params));
		
		// execute the post request
		CloseableHttpResponse response = httpClient.execute(request);
		
		// build the response into a string
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()), 65728);
		
		String line = null;
		while((line = reader.readLine()) != null)
		{
				sb.append(line);
		}
		
		response.close();
		reader.close();
					
		return sb.toString(); // bearer token
	}

	public String getBearerToken()
	{
		return bearerToken;
	}

}
