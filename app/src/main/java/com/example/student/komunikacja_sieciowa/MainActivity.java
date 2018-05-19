package com.example.student.komunikacja_sieciowa;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public final static int MOJE_UPRAWNIENIA = 2018;
    private Button pobierzInfo;
    private Button pobierzPlik;
    private TextView adres;
    private TextView rozmiarPliku;
    private TextView typPliku;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pobierzPlik = (Button) findViewById(R.id.pobierz_plik);
        pobierzPlik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MOJE_UPRAWNIENIA);
                } else {
                    Log.d("Uprawnienia", "Mam uprawnienia");
                    UslugaPobieranie.uruchomPobieranie(MainActivity.this, adres.getText().toString());
                }
            }
        });
        adres =
                (TextView) findViewById(R.id.adres);
        adres.setText("http://www.tapeta-lampart-grafika-3.na-pulpit.com/zdjecia/lampart-grafika-3.jpeg");
        pobierzInfo = (Button) findViewById(R.id.pobierz_info);

        pobierzInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                informacjeOPliku();
            }
        });

    }

    private void informacjeOPliku() {
        ZadanieAsynchroniczne zadanie = new ZadanieAsynchroniczne();
        zadanie.execute(new String[]{adres.getText().toString()});
        Log.d("PI", "zadanie uruchomione");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Uprawnienia", "Sprawdzenie uprawnien, liczba uprawnien: " + permissions.length);
        switch (requestCode) {
            case MOJE_UPRAWNIENIA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("Uprawnienia", "Otrzymano uprawnienie: " + permissions[0]);
                    pobierzPlik = (Button) findViewById(R.id.pobierz_plik);
                    pobierzPlik.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UslugaPobieranie.uruchomPobieranie(MainActivity.this, adres.getText().toString());
                        }
                    });
                } else {
                    Toast.makeText(this, "Nie mogę pobrać pliku bez uprawnień", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    class ZadanieAsynchroniczne extends AsyncTask<String, Void, String[]> {

        int mRozmiar;
        String mTyp;

        @Override
        protected String[] doInBackground(String... params) {
            Log.d("PI", "uruchomione");
            String[] plikInfo = new String[2];
            rozmiarPliku = findViewById(R.id.rozmiar);
            typPliku = findViewById(R.id.typ);
            HttpURLConnection polaczenie = null;
            try {
                URL url = new URL(params[0]);
                Log.d("PI", "URL " + url.toString());
                polaczenie = (HttpURLConnection) url.openConnection();
                polaczenie.setRequestMethod("GET");
                polaczenie.setDoOutput(true);
                plikInfo[0] = new Integer(polaczenie.getContentLength()).toString();
                plikInfo[1] = polaczenie.getContentType();
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

