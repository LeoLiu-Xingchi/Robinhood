package com.cmd;

import com.robinhood.RobinhoodClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Scanner;
import java.util.Spliterator;

/**
 * Created by billupus on 11/16/18.
 */
public class CommandBot {

    public void start() {
        boolean loginSucceeded = false;
        Scanner reader = new Scanner(System.in);
        System.out.println("Enter your Robinhood username: ");
        String username = reader.next();
        // User login
        RobinhoodClient client = new RobinhoodClient(username);

        while (!loginSucceeded) {
            try {
                loginSucceeded = client.login();
                if (loginSucceeded) {
                    System.out.println("Login succeeded\n***********************************");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Exception logging in " + e);
            }
            System.out.println("Please re-enter your user name:");
            username = reader.next();
            client.setUser(username);
        }
        // Infinite loop
        while (true) {
            System.out.println("Enter command:\n" +
                               "1.Print user info\n" +
                               "2.Print employment info\n" +
                               "3.Print accounts info\n" +
                               "4.Positions\n" +
                               "5.Quote\n" +
                               "6.Buy shares (coming soon)\n" +
                               "7.Sell shares (coming soon)\n" +
                               "8.Show orders (coming soon)\n" +
                               "0.Exit\n***********************************");
            int choice = reader.nextInt();
            String ticker = null;
            try {
                switch (choice) {
                    case 1:
                        System.out.println(client.getUserInfo().toString(4));
                        break;
                    case 2:
                        System.out.println(client.getUserEmploymentInfo().toString(4));
                        break;
                    case 3:
                        JSONObject res = client.getAccountsInfo();
                        System.out.println(res.toString(4));
                        break;
                    case 4:
                        System.out.println(client.getPositions().toString(4));
                        break;
                    case 5:
                        System.out.println("Please input ticker:");
                        ticker = reader.next();
                        System.out.println(client.getQuote(ticker).toString(4));
                        break;
                    case 6:
                        System.out.println("Please input ticker:");
                        ticker = reader.next();
                        System.out.println(client.buyShares(ticker).toString(4));
                        break;
                    case 0:
                        System.out.println("Goodbye!\n");
                        return;
                    default:
                        System.out.println("Unsupported. Please try again ... ");
                }
            } catch (Exception e) {
                System.out.println("Oops. Something is wrong. Please try again ... ");
            }
            System.out.println("***********************************");
        }
    }
}
