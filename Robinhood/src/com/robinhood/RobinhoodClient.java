package com.robinhood;

import com.http.HttpClient;
import com.http.Request;
import com.login.User;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by billupus on 11/11/18.
 */
public class RobinhoodClient {
    private String accessToken = null;
    private String tokenType = null;
    private User user = null;
    private static Map<String, String> urls = new HashMap<String, String>();
    private HttpClient client = new HttpClient();
    static {
        urls.put("login", "https://api.robinhood.com/oauth2/token/");
        urls.put("billupus@gmail.com", "https://api.robinhood.com/billupus@gmail.com/");
        urls.put("employment", "https://api.robinhood.com/billupus@gmail.com/employment/");
        urls.put("positions", "https://api.robinhood.com/positions/");
    }

    public RobinhoodClient(String username) {
        this.user = new User(username);
    }

    public boolean login() {
        // Login as the billupus@gmail.com to obtain access token
        try {
            Request postRequest = new Request(urls.get("login"), Request.RequestType.POST)
                    .addParameter("username", user.getUsername())
                    .addParameter("password", user.getPassword())
                    .addParameter("grant_type", "password")
                    .addParameter("client_id", "c82SH0WZOsabOXGP2sxqcj34FxkvfnWRZBKlBjFS");
            String stringResponse = client.makeRequest(postRequest);
            JSONObject jsonObject = new JSONObject(stringResponse);
            String accessToken = jsonObject.get("access_token").toString();
            String tokenType = jsonObject.get("token_type").toString();
            if (accessToken != null && tokenType != null) {
                this.accessToken = accessToken;
                this.tokenType = tokenType;
                return true;
            }
            System.out.println("Failed to get token or token type.");
        } catch (MalformedURLException e) {
            System.out.println("Url malformed exception: " + e);
        }
        return false;
    }

    public JSONObject getUserInfo() {
        try {
            Request getRequest = new Request(urls.get("billupus@gmail.com"), Request.RequestType.GET)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", tokenType + " " + accessToken);
            return new JSONObject(client.makeRequest(getRequest));
        } catch (MalformedURLException e) {
            System.out.println("Url malformed exception: " + e);
        }
        return null;
    }

    public JSONObject getUserEmploymentInfo() {
        try {
            Request getRequest = new Request(urls.get("employment"), Request.RequestType.GET)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", tokenType + " " + accessToken);
            return new JSONObject(client.makeRequest(getRequest));
        } catch (MalformedURLException e) {
            System.out.println("Url malformed exception: " + e);
        }
        return null;
    }

    public JSONObject getPositions() {
        try {
            Request getRequest = new Request(urls.get("positions"), Request.RequestType.GET)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", tokenType + " " + accessToken);
            return new JSONObject(client.makeRequest(getRequest));
        } catch (MalformedURLException e) {
            System.out.println("Url malformed exception: " + e);
        }
        return null;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
