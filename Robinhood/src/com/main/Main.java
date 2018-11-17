package com.main;

import com.robinhood.RobinhoodClient;

public class Main {
    private static String USER_NAME = "billupus@gmail.com";

    public static void main(String[] args) {
        RobinhoodClient client = new RobinhoodClient(USER_NAME);
        boolean res = client.login();
        if (!res) {
            System.out.println("Login failed.");
            return;
        }
        System.out.println("accessToken: " + client.getAccessToken());
        System.out.println(client.getPositions().toString(4));
    }
}
