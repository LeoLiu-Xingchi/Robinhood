package com.robinhood;

import com.http.HttpClient;
import com.http.Request;
import com.login.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private List<String> accountIdList = new ArrayList<String>();

    static {
        urls.put("login", "https://api.robinhood.com/oauth2/token/");
        urls.put("user", "https://api.robinhood.com/user/");
        urls.put("employment", "https://api.robinhood.com/user/employment/");
        urls.put("positions", "https://api.robinhood.com/positions/");
        urls.put("quotes", "https://api.robinhood.com/quotes/%s/");
        urls.put("accounts", "https://api.robinhood.com/accounts/");
    }

    public RobinhoodClient(String username) {
        this.user = new User(username);
    }

    public void setUser(String username) {
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
            Request getRequest = new Request(urls.get("user"), Request.RequestType.GET)
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

    public JSONObject getAccountsInfo() {
        try {
            Request getRequest = new Request(urls.get("accounts"), Request.RequestType.GET)
                    .addHeader("Accept", "application/json")
                    .addHeader("Authorization", tokenType + " " + accessToken);
            JSONObject res = new JSONObject(client.makeRequest(getRequest));
            JSONArray resultList = res.getJSONArray("results");
            List<String> accountIds = new ArrayList<String>();
            for (int i = 0; i < resultList.length(); i++) {
                String parts[] = resultList.getJSONObject(0).getString("url").split("/");
                accountIds.add(parts[parts.length - 1]);
            }
            accountIdList = accountIds;
            printAccountIds();
            return res;
        } catch (MalformedURLException e) {
            System.out.println("Url malformed exception: " + e);
        }
        return null;
    }

    public void printAccountIds() {
        if (accountIdList.size() == 0) {
            System.out.println("No account id available.");
            return;
        }
        System.out.println("Account ids: " + accountIdList);
    }

    public JSONObject getQuote(String ticker) {
        try {
            Request getRequest = new Request(String.format(urls.get("quotes"), ticker), Request.RequestType.GET);
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
