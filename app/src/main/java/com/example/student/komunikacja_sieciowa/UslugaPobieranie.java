package com.example.student.komunikacja_sieciowa;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UslugaPobieranie extends IntentService {
    private static final String AKCJA_POBIERANIE = "com.example.student.komunikacja_sieciowa.action.pobieranie";
    private static final String ADRES = "com.example.student.komunikacja_sieciowa.extra.adres";


    public UslugaPobieranie() {
        super("UslugaPobieranie");
    }


    public static void uruchomPobieranie(Context context, String adres) {
        Intent intent = new Intent(context, UslugaPobieranie.class);
        intent.setAction(AKCJA_POBIERANIE);
        intent.putExtra(ADRES, adres);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (AKCJA_POBIERANIE.equals(action)) {
                final String adres = intent.getStringExtra(ADRES);
                Log.d("Usługa pobierania", "Rozpoczęcie akcji");
                pobieranie(adres);
            } else {
                Log.e("Usluga pobierania", "nieznana akcja");
            }
        }
        Log.d("Usługa pobierania", "Zakończenie akcji");
    }


    private void pobieranie(String adres) {

        HttpURLConnection polaczenie = null;
        FileOutputStream strumienDoPliku = null;
        int pobranoBajtow = 0;
        int rozmiar = 0;
        try {
            URL url = new URL(adres);
            polaczenie = (HttpURLConnection) url.openConnection();
            Log.d("pobieranie()", "Ustanowienie polaczenia");
            polaczenie.connect();
            File plikRoboczy = new File(url.getFile());
            File plikWyjsciowy = new File(
                    Environment.getExternalStorageDirectory() +
                            File.separator + plikRoboczy.getName());
            if (plikWyjsciowy.exists()) plikWyjsciowy.delete();

            rozmiar = polaczenie.getContentLength();
            strumienDoPliku = new FileOutputStream(plikWyjsciowy.getPath());
            DataInputStream czytnik = new DataInputStream(polaczenie.getInputStream());

            Log.d("pobieranie()", "Pobieranie");
            byte[] bufor = new byte[1024];
            int pobrano = czytnik.read(bufor, 0, 1024);
            while (pobrano != -1) {
                strumienDoPliku.write(bufor, 0, pobrano);
                pobranoBajtow += pobrano;
                Log.d("pobieranie()", "Pobrano:" + pobranoBajtow);
                wyslijBroadcast(rozmiar, pobranoBajtow, 0);
                pobrano = czytnik.read(bufor, 0, 1024);
            }
            wyslijBroadcast(rozmiar, pobranoBajtow, 1);
            Log.d("pobieranie()", "Pobrano");
            strumienDoPliku.close();
            czytnik.close();

        } catch (Exception e) {
            wyslijBroadcast(rozmiar, pobranoBajtow, -1);
            e.printStackTrace();
        } finally {
            if (polaczenie != null) polaczenie.disconnect();
        }

    }

    public final static String POWIADOMIENIE = "com.example.student.komunikacja_sieciowa.odbiornik";
    public final static String INFO_O_POBIERANIU = "info o pobieraniu";

    private void wyslijBroadcast(int rozmiar, int pobrano, int wynik) {

        Log.d("Broadcast", "wysłanie powiadomienia");
        PostepInfo postep = new PostepInfo();
        postep.mWynik = wynik;
        postep.mRozmiar = rozmiar;
        postep.mPobranychBajtow = pobrano;
        Intent zamiar = new Intent(POWIADOMIENIE);
        zamiar.putExtra(INFO_O_POBIERANIU, postep);
        sendBroadcast(zamiar);
    }
}
