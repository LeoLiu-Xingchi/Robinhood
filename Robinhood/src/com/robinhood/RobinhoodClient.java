package com.robinhood;

import com.http.HttpClient;
import com.http.Request;
import com.login.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
        urls.put("orders", "https://api.robinhood.com/orders/");
        urls.put("instruments", "https://api.robinhood.com/instruments/");
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

    public JSONObject buyShares(String ticker) {
        String instrumentUrl = getInstrumentUrl(ticker);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost(urls.get("orders"));
            post.addHeader("Content-Type", "application/json");
            post.addHeader("Authorization", tokenType + " " + accessToken);
            post.setEntity(generateTradePostData("buy", instrumentUrl, ticker));
            HttpResponse response = httpClient.execute(post);

            return getResultContentFromResponse(response);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unsupported encoding exception: " + e);
        } catch (ClientProtocolException e) {
            System.out.println("Error executing http post: " + e);
        } catch (IOException e) {
            System.out.println("Error executing http post: " + e);
        }
        return null;
    }

    public String getAccessToken() {
        return accessToken;
    }

    private StringEntity generateTradePostData(String side, String instrumentUrl, String ticker) throws UnsupportedEncodingException {
        JSONObject object = new JSONObject();
        object.put("account", String.format("https://api.robinhood.com/accounts/%s/", accountIdList.get(0)));
        object.put("instrument", instrumentUrl);
        object.put("symbol", ticker);
        object.put("type", "market");
        object.put("time_in_force", "gtc");
        object.put("trigger", "immediate");
        object.put("quantity", "1");
        object.put("side", side);

        StringEntity entity = new StringEntity(object.toString(), "UTF-8");
        entity.setContentType("application/json");

        return entity;
    }

    private String getInstrumentUrl(String ticker) {
        try {
            Request getRequest = new Request(urls.get("instruments"), Request.RequestType.GET)
                    .addHeader("Accept", "application/json")
                    .addParameter("symbol", ticker);
            String res = client.makeRequest(getRequest);
            return new JSONObject(res).getJSONArray("results").getJSONObject(0).getString("url");
        } catch (MalformedURLException e) {
            System.out.println("Url malformed exception: " + e);
        }
        return null;
    }

    private JSONObject getResultContentFromResponse(HttpResponse response) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }

        return new JSONObject(result);
    }
}
