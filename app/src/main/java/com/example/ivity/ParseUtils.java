package com.example.ivity;


import android.net.Uri;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class ParseUtils{

    final static String BASE_URL = "https://pbe41.000webhostapp.com/query.php/";
    private static String paswd = "";



    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
    public static URL buildUrl(String query) {
        String string;
        if(query.contains("?") && (query.split("\\?").length == 1)) {
            string = BASE_URL + query + "owner_id=" + paswd;
        }else{
            string = BASE_URL + query + "&owner_id=" + paswd;
        }
        URL url = null;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    public static URL buildUrlLogin(String user, String pass){
        setPaswd(pass);
        String string = BASE_URL + "students?user=" + user + "&student_pasw=" + crypt(pass);
        URL url = null;
        try {
            url = new URL(string);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static void setPaswd(String paswd) {
        ParseUtils.paswd = paswd;
    }
    public static String crypt(String pass){
        return MD5Crypt.crypt(pass, "$1$pbe201941$").substring(12);
    }
}
