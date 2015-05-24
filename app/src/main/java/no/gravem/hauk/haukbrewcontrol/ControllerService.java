package no.gravem.hauk.haukbrewcontrol;

import android.os.AsyncTask;
import android.util.Log;

import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

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

    public void setUROMVariable(String query, ControllerResult callback) {
        new DoGetRequest(callback).execute(String.format("/seturom.cgi?%s", query));
    }


    private class DoGetRequest extends AsyncTask<String, Integer, Void> {

        private ControllerResult resultCallback;

        public DoGetRequest(ControllerResult resultCallback) {
            this.resultCallback = resultCallback;
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         *
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         *
         * @return A result, defined by the subclass of this task.
         */
        @Override
        protected Void doInBackground(String... params) {
            try {
                URL url = new URL(rootUrl + params[0]);
                Authenticator.setDefault(new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(userName, password.toCharArray());
                    }
                });

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                resultCallback.done(connection);
                //return connection;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         *
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * //@param connection The result of the operation computed by {@link #doInBackground}.
         */
        /**@Override
        protected void onPostExecute() {
            Log.d(this.getClass().getName(), "onPostExecute");
            //Network on main thread-exception. onPostExecute runs on UI-thread

        }**/
    }
}
