package mizzou.spot.cam;


public class Client
{
	private static final String SPOT_ADDRESS_WIRED = "10.0.0.4";
	private static final String SPOT_ADDRESS_WIRELESS = "192.168.80.3";
	private static final String SPOT_AUTH_SERVER_WIRED = SPOT_ADDRESS_WIRED + "/accounts/jwt/generate/";
	private static final String SPOT_AUTH_SERVER_WIRELESS = SPOT_ADDRESS_WIRELESS + "/accounts/jwt/generate/";
	private static final String SPOT_CAM_PORT = "31102";
	private static final String SPOT_CAM_SERVER_WIRED = SPOT_ADDRESS_WIRED + ":" + SPOT_CAM_PORT + "/h264.sdp/";


}
