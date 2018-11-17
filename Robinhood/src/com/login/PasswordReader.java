package com.login;

import java.io.*;

/**
 * Created by billupus on 11/11/18.
 */
public class PasswordReader {
    private static String BASE_PATH = "users/";

    public static String readPasswordFromFile(String username) {
        try {
            File file = new File(BASE_PATH + username);
            BufferedReader reader = new BufferedReader(new FileReader(file));

            return reader.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("User file not found. Username: " + username);
        } catch (IOException e) {
            System.out.println("Cannot read password from billupus@gmail.com file: " + username);
        }
        return null;
    }
}
