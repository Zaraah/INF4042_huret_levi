package org.esiea.huret_levi.myapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GetSeriesServices.startActionSeries(this);
    }

    public void launchIntent(View v) {
        Intent intent = new Intent(this, SecondeActivity.class);
        startActivity(intent);
    }

}
