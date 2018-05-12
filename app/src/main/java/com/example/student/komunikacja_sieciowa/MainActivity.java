package com.example.student.komunikacja_sieciowa;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button uruchom;
    private TextView mCzasDzialaniaEtykieta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCzasDzialaniaEtykieta =
                (TextView) findViewById(R.id.czas_dzialania_etykieta);
        uruchom = (Button) findViewById(R.id.uruchom);
        uruchom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uruchomZadanieAcynchroniczne();
            }
        });
    }
    private void uruchomZadanieAcynchroniczne() {
        ZadanieAsynchroniczne zadanie=new ZadanieAsynchroniczne();
        zadanie.execute(new Integer[] {10});
        Log.d("async_task", "zadanie uruchomione");
    }
    class ZadanieAsynchroniczne extends AsyncTask<Integer,Integer,Integer> {
        @Override
        protected Integer doInBackground(Integer... params) {

          
        }


        // opcjonalna, wywoływana w wątku GUI
        // aktualizuje informacje o postępie
        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.d("async_task",
                    "aktualizacja postępu: " + values[0].intValue());
            mCzasDzialaniaEtykieta.setText(
                    Integer.toString(values[0].intValue()));
            super.onProgressUpdate(values);
        }

        // opcjonalna, wywoływana w wątku GUI
        // odpowiedzialna za publikację wyników
        @Override
        protected void onPostExecute(Integer result) {
            Log.d("async_task", "wynik: " + result.intValue());
            TextView wynikEtykieta =
                    (TextView) findViewById(R.id.wynik_etykieta);
            wynikEtykieta.setText("wynik: " + result.intValue());
            super.onPostExecute(result);
        }
    }
}

