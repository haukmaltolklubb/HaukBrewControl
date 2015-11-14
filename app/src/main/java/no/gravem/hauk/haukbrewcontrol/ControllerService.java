package no.gravem.hauk.haukbrewcontrol;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.List;

/**
 * Created by agravem on 19.05.2015.
 */
public class ControllerService {

    private static String rootUrl = "http://88.84.49.55/api";
    private static String userName = "admin";
    private static String password = "hannah2002";

    public void getStatusXml(ControllerResult callback) {
        new DoGetRequest(callback).execute("/status.xml");
    }

    public void setVariable(String query, ControllerResult callback) {
        new DoGetRequest(callback).execute(String.format("/setvar.cgi?%s", query));
    }
    public void setVariable(String query) {
        new DoGetRequest().execute(String.format("/setvar.cgi?%s", query));
    }
    public void setVariables(List<String> queries){
        String[] strings = new String[queries.size()];
        for(int i = 0; i < queries.size(); i++){
            strings[i] = String.format("/setvar.cgi?%s", queries.get(i));
        }
        new DoGetMultipleRequests().execute(strings);
    }

    public void setUROMVariable(String query, ControllerResult callback) {
        new DoGetRequest(callback).execute(String.format("/seturom.cgi?%s", query));
    }


    private class DoGetRequest extends AsyncTask<String, Integer, Void> {

        private ControllerResult resultCallback;

        public DoGetRequest(){
            this.resultCallback = null;
        }

        public DoGetRequest(ControllerResult resultCallback) {
            this.resultCallback = resultCallback;
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(rootUrl + params[0]);
                Log.d(this.getClass().getName(), "URL: " + url.toString());
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, password.toCharArray());
                    }
                });

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                connection.getResponseCode(); // sic: This forces the request to be executed.
                Log.d(this.getClass().getName(), "Responsestatus: " + connection.getResponseCode() + ". Msg: " + connection.getResponseMessage());

                if(resultCallback != null) {
                    String responseBody = "";
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String responseLine = null;
                    while ((responseLine = reader.readLine()) != null) {
                        responseBody += responseLine;
                    }

                    resultCallback.done(responseBody);
                }

            } catch (Exception e) {
                throw new PLSConnectionException(e);
            }
            return null;
        }
    }

    //TODO: Skriv om denne til å kjøre loop for flere requests
    private class DoGetMultipleRequests extends AsyncTask<String, Integer, Void>{

        private ControllerResult resultCallback;

        public DoGetMultipleRequests(){
            this.resultCallback = null;
        }

        public DoGetMultipleRequests(ControllerResult resultCallback) {
            this.resultCallback = resultCallback;
        }

        @Override
        protected Void doInBackground(String... params) {
            for (String param : params){
                Log.d(this.getClass().getName(), "Param:" + param);
            }

            return null;
        }
    }
}
