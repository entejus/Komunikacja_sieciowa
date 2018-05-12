package com.example.student.komunikacja_sieciowa;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button pobierzInfo;
    private TextView adres;
    private TextView rozmiarPliku;
    private TextView typPliku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adres =
                (TextView) findViewById(R.id.adres);
        pobierzInfo = (Button) findViewById(R.id.pobierz_info);
        pobierzInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                informacjeOPliku();
            }
        });
    }
    private void informacjeOPliku(){
        ZadanieAsynchroniczne zadanie=new ZadanieAsynchroniczne();
        zadanie.execute(new String[] {adres.getText().toString()});
        Log.d("PI", "zadanie uruchomione");
    }
    class ZadanieAsynchroniczne extends AsyncTask<String,Void,String[]> {

        int mRozmiar;
        String mTyp;

        @Override
        protected String[] doInBackground(String... params) {
            Log.d("PI","uruchomione");
            String[] plikInfo = new String[2];
            rozmiarPliku = findViewById(R.id.rozmiar);
            typPliku = findViewById(R.id.typ);
            HttpURLConnection polaczenie = null;
            try {
                URL url = new URL(params[0]);
                Log.d("PI","URL "+url.toString());
                polaczenie = (HttpURLConnection) url.openConnection();
                polaczenie.setRequestMethod("GET");
                polaczenie.setDoOutput(true);
                plikInfo[0]=new Integer(polaczenie.getContentLength()).toString();
                plikInfo[1]=polaczenie.getContentType();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (polaczenie != null) polaczenie.disconnect();
                Log.d("PI", "rozłączone");
            }
            return plikInfo;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(String[] result) {
            Log.d("PI", "zakonczone");
            rozmiarPliku = findViewById(R.id.rozmiar);
            typPliku = findViewById(R.id.typ);
            rozmiarPliku.setText(result[0]);
            typPliku.setText(result[1]);
            super.onPostExecute(result);
        }
    }
}

