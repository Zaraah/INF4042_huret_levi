package org.esiea.huret_levi.myapp2;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SecondeActivity extends AppCompatActivity {

    RecyclerView rv;
    private static final String TAG = "SecondeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seconde);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new AsyncTask1().execute();
    }


    public void randomSeries(View v) {
        GetSeriesServices.startActionSeries(this);
        IntentFilter intentFilter = new IntentFilter(SERIES_UPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(new SerieUpdate(), intentFilter);
        rv = (RecyclerView) findViewById(R.id.rv_biere);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(new SeriesAdapter(getSeriesFromFile()));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            final TextView tv = (TextView)findViewById(R.id.tv_hello_world);
            final CharSequence myList[] = { getResources().getString(R.string.black), getResources().getString(R.string.red), getResources().getString(R.string.blue), getResources().getString(R.string.green), getResources().getString(R.string.gray) };
            final AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle(getResources().getString(R.string.dialog_title));
            ad.setSingleChoiceItems(myList, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    switch (arg1) {
                        case 0:
                            tv.setTextColor(Color.BLACK);
                            break;
                        case 1:
                            tv.setTextColor(Color.RED);
                            break;
                        case 2:
                            tv.setTextColor(Color.BLUE);
                            break;
                        case 3:
                            tv.setTextColor(Color.GREEN);
                            break;
                        case 4:
                            tv.setTextColor(Color.GRAY);
                            break;
                    }
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.toast_text) + myList[arg1],
                            Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });
            ad.create().show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void notification(){
        NotificationCompat.Builder notif = new NotificationCompat.Builder(this);
        notif.setContentTitle(getResources().getString(R.string.notif_title));
        notif.setContentText(getResources().getString(R.string.notif_msg));
        notif.setSmallIcon(R.drawable.icone_moyenne);
        Intent resultIntent = new Intent(this, SecondeActivity.class);
        NotificationManager notifMan = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notifMan.notify(22, notif.build());
    }

    private class AsyncTask1 extends AsyncTask<String, Integer, String> {
        public final String tag = AsyncTask1.class.getSimpleName();
        protected String doInBackground(String... args){
            publishProgress(args.length);
            randomSeries(findViewById(R.id.rv_biere));
            Log.d(tag, "Thread async doInBg name:" + Thread.currentThread().getName());
            return "onPostExecute";
        }
        protected void onProgressUpdate(Integer... progress){
            Log.d(tag, "Thread async" + progress[0]+"name:"+Thread.currentThread().getName());
        }
        protected void onPostExecute(String result){

            Log.d(tag, "Thread async"+result+"name:"+Thread.currentThread().getName());
        }
    }

    public static final String SERIES_UPDATE="com.octip.cours.inf4042_11.BIERS_UPDATE";
    public class SerieUpdate extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent){
            SeriesAdapter var;
            //Log.d(TAG, getIntent().getAction());
            notification();
            var=(SeriesAdapter) rv.getAdapter();
            var.setNewSerie(getSeriesFromFile());
        }
    }

    public JSONArray getSeriesFromFile(){
        try{
            InputStream is = new FileInputStream(getCacheDir()+"/"+"series.json");
            System.out.println("is: " +is);
            byte buffer[] = new byte[is.available()];
            is.read(buffer);
            is.close();
            JSONObject object = new JSONObject(new String(buffer, "UTF-8"));
            return object.getJSONArray("shows");
        }catch(IOException e){
            e.printStackTrace();
            return new JSONArray();
        }catch (JSONException e){
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.SerieHolder> {
        private JSONArray series;
        SeriesAdapter(JSONArray SerieArray){
            this.series=SerieArray;
        }
        public void setNewSerie(JSONArray series){
            this.series=series;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(SeriesAdapter.SerieHolder holder, int position) {
            JSONObject json;
            try {
                json = series.getJSONObject(position);
                holder.name.setText(json.getString("title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return series.length();
        }

        @Override
        public SeriesAdapter.SerieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            SerieHolder serie = new SerieHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_serie_element, parent, false));
            return serie;
        }

        public class SerieHolder extends RecyclerView.ViewHolder {
            public TextView name;

            public SerieHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.rv_serie_element_name);
            }
        }

    }

}
