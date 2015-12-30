package org.esiea.huret_levi.myapp2;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GetSeriesServices extends IntentService {

    private static final String ACTION_SERIES = "org.esiea.huret_levi.myapp2.action.get_series";
    public static final String TAG = "GetSeriesService";

    public static void startActionSeries(Context context) {
        Intent intent = new Intent(context, GetSeriesServices.class);
        intent.setAction(ACTION_SERIES);
        context.startService(intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SecondeActivity.SERIES_UPDATE));
    }

    public GetSeriesServices() {
        super("GetSeriesServices");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SERIES.equals(action)) {
                handleActionSeries();
            }
        }
    }

    private void handleActionSeries() {

        Log.d(TAG, "Thread service name:"+Thread.currentThread().getName());
        URL url = null;
        try{
            url = new URL("http://api.betaseries.com/shows/random?nb=10&key=141B708F9752&v=2.4.json");
            //Toast.makeText(getApplicationContext(), "Toasty!", Toast.LENGTH_LONG).show();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            conn.connect();
            if(HttpURLConnection.HTTP_OK == conn.getResponseCode()){
                copyInputStreamToFile(conn.getInputStream(), new File(getCacheDir(), "series.json"));
            }
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyInputStreamToFile(InputStream in, File file){
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }



}
