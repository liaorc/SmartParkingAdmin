package icat.sjtu.edu.smartparkingadmin;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ruochen on 2015/8/5.
 */
public class HttpRequestTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "PARK_HTTPS";



    private static final String HTTP_ROOT = "https://139.196.26.139";
    private static final String API_LOGIN = "/park/login";
    private static final String API_QUERY = "/park/query";
    private static final String API_CONFIRM = "/park/confirm";
    private static final String API_CHECKQR = "/park/check_qr";

    private JSONObject jsonReq = new JSONObject();
    private JSONObject jsonRev = null;

    private void checkRev() throws ExecutionException, InterruptedException, JSONException {
        if(jsonRev == null) {
            jsonRev = new JSONObject(this.get());
        }
    }

    public void setString(String label, String str) throws JSONException {
        jsonReq.put(label, str);
    }
    public void setInt(String label, int i) throws JSONException {
        jsonReq.put(label, i);
    }

    public int getStatusCode() throws Exception {
        checkRev();
        return jsonRev.getInt(JSONLabel.STATUS);
    }

    public String getSession() throws Exception {
        checkRev();
        return jsonRev.getString(JSONLabel.SESSION);
    }

    public String getData() throws Exception {
        checkRev();
        return jsonRev.getString(JSONLabel.DATA);
    }

    public void login() throws JSONException {
        String url = HTTP_ROOT + API_LOGIN
                + "?" + JSONLabel.USERNAME + "=" + jsonReq.getString(JSONLabel.USERNAME)
                + "&" + JSONLabel.PASSWORD + "=" + jsonReq.getString(JSONLabel.PASSWORD)
                + "&" + "verify=PW";
        this.execute(url);
    }

    public void queryOrders() throws JSONException {
        String url = HTTP_ROOT + API_QUERY
                + "?" + JSONLabel.SESSION + "=" + jsonReq.getString(JSONLabel.SESSION);
        this.execute(url);
    }
    
    public void confirmOrder() throws JSONException {
        String url = HTTP_ROOT + API_CONFIRM
                + "?" + JSONLabel.SESSION + "=" + jsonReq.getString(JSONLabel.SESSION)
                + "&" + JSONLabel.USERID + "=" + jsonReq.getString(JSONLabel.USERID)
                + "&" + JSONLabel.ORDERID + "=" + jsonReq.getString(JSONLabel.ORDERID);
        this.execute(url);
    }

    public void checkQRCode() throws JSONException {
        String url = HTTP_ROOT + API_CHECKQR
                + "?" + JSONLabel.SESSION + "=" + jsonReq.getString(JSONLabel.SESSION)
                + "&" + JSONLabel.QRCODE + "=" + jsonReq.getString(JSONLabel.QRCODE);
        this.execute(url);
    }



    @Override
    protected String doInBackground(String... url) {

        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };


        StringBuilder stringBuilder = new StringBuilder();
        try {


            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            //SSLSocketFactory factory = sc.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            URL obj = new URL(url[0]);
            Log.d(TAG, url[0]);

            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
            //compact issue exists
            con.setHostnameVerifier(new AllowAllHostnameVerifier());
            con.setConnectTimeout(500);
            con.setReadTimeout(500);

            // optional default is GET
            con.setRequestMethod("GET");

            BufferedReader rd;

            rd = new BufferedReader((new InputStreamReader(con.getInputStream())));

            String line = null;
            while ( (line = rd.readLine()) != null ) {
                stringBuilder.append(line);
            }
        }
        catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        return stringBuilder.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        //Log.d(TAG, s);
    }
}
