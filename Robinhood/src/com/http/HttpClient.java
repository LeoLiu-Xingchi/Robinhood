package com.http;

import java.io.*;
import java.net.*;
import java.util.Map;

/**
 * Created by billupus on 11/11/18.
 */
public class HttpClient {
    public HttpClient() {};

    public String makeRequest(Request request) {
        String stringResponse = null;
        try {
            switch (request.getType()) {
                case GET:
                    stringResponse = makeGetRequest(request);
                    break;
                case POST:
                    stringResponse = makePostRequest(request);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            System.out.println("IOException when making http call: " + e);
        } catch (URISyntaxException e) {
            System.out.println("URISyntaxException when converting between url and uri: " + e);
        }
        return stringResponse;
    }

    private String makeGetRequest(Request getRequest) throws IOException, URISyntaxException {
        String stringResponse = null;
        // Build url with parameters
        URL url = appendParamenters(getRequest.getBaseUrl(), getRequest.getParameters());
        System.out.println("GET URL: " + url.toString());
        // Make request
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        addHeaders(connection, getRequest.getHeaders());
        int responseCode = connection.getResponseCode();
        System.out.println("GET response code: " + responseCode);
        if (responseCode >= HttpURLConnection.HTTP_OK) {
            //&& responseCode <= HttpURLConnection.HTTP_BAD_REQUEST) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            stringResponse = response.toString();
        }
        return stringResponse;
    }

    private String makePostRequest(Request postRequest) throws IOException {
        String stringResponse = null;
        // Build url with parameters
        HttpURLConnection connection = (HttpURLConnection) postRequest.getBaseUrl().openConnection();
        connection.setRequestMethod("POST");
        addPostParameters(connection, postRequest.getParameters());
        // Make request
        int responseCode = connection.getResponseCode();
        if (responseCode >= HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            stringResponse = response.toString();
        }
        return stringResponse;
    }

    private URL appendParamenters(URL baseUrl, Map<String, String> parameters) throws URISyntaxException, UnsupportedEncodingException, MalformedURLException {
        URI uri = baseUrl.toURI();
        String parameterString = buildParameterString(parameters);
        return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), parameterString, uri.getFragment()).toURL();
    }

    private String buildParameterString(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry: parameters.entrySet()) {
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append('=');
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            sb.append("&");
        }
        String resultString = sb.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    private void addPostParameters(HttpURLConnection connection, Map<String, String> parameters) throws IOException {
        String parameterString = buildParameterString(parameters);
        connection.setDoOutput(true);
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());
        out.writeBytes(parameterString);
        out.flush();
        out.close();
    }

    private void addHeaders(HttpURLConnection connection, Map<String, String> headers) {
        for (Map.Entry<String, String> entry: headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }
    }
}
