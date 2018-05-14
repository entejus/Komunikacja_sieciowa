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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UslugaPobieranie extends IntentService {
    private static final String AKCJA_POBIERANIE = "com.example.student.komunikacja_sieciowa.action.pobieranie";
    private static final String ADRES = "com.example.student.komunikacja_sieciowa.extra.czas";


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
                pobieranie(adres);
                Log.d("Usługa pobierania", "Rozpoczęcie akcji");
            } else {
                Log.e("Usluga pobierania", "nieznana akcja");
            }
        }
        Log.d("Usługa pobierania", "Zakończenie akcji");
    }


    private void pobieranie(String adres) {

        HttpURLConnection polaczenie = null;
        FileOutputStream strumienDoPliku = null;
        przygotujPowiadomienie();
        try {
            URL url = new URL(adres);
            polaczenie = (HttpURLConnection) url.openConnection();
            polaczenie.connect();
            File plikRoboczy = new File(url.getFile());
            File plikWyjsciowy = new File(
                    Environment.getExternalStorageDirectory() +
                            File.separator + plikRoboczy.getName());
            if (plikWyjsciowy.exists()) plikWyjsciowy.delete();

            strumienDoPliku = new FileOutputStream(plikWyjsciowy.getPath());
            InputStream strumien = polaczenie.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = strumien.read(buffer)) != -1) {
                strumienDoPliku.write(buffer, 0, len1);
            }
            aktualizujPowiadomienie();
            strumienDoPliku.close();
            strumien.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    NotificationManager menedzerPowiadomien;
    int idPowiadomienia;
    Notification.Builder budowniczyPowiadomien;
    Intent zamiarPowiadomienia;

    void przygotujPowiadomienie() {
        Log.d("MP", "przygotujPowiadomienie()");

        zamiarPowiadomienia = new Intent(this, MainActivity.class);

        TaskStackBuilder budowniczyStosu = TaskStackBuilder.create(this);
        budowniczyStosu.addParentStack(MainActivity.class);
        budowniczyStosu.addNextIntent(zamiarPowiadomienia);

        PendingIntent zamiarOczekujacy = budowniczyStosu.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        menedzerPowiadomien = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        idPowiadomienia = 1;
        budowniczyPowiadomien = new Notification.Builder(this);
        budowniczyPowiadomien.setContentTitle("Tytuł powiadomienia")
                .setContentIntent(zamiarOczekujacy)
                .setSubText("pobieranie")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true);

        menedzerPowiadomien.notify(idPowiadomienia, budowniczyPowiadomien.build());
    }

    void aktualizujPowiadomienie() {
        Log.d("MP", "aktualizujPowiadomienie()");
        TaskStackBuilder budowniczyStosu = TaskStackBuilder.create(this);
        budowniczyStosu.addParentStack(MainActivity.class);
        budowniczyStosu.addNextIntent(zamiarPowiadomienia);

        PendingIntent zamiarOczekujacy = budowniczyStosu.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        budowniczyPowiadomien.setOngoing(false) // już nie trwa
                // po kliknięciu automatycznie się usunie z paska
                .setAutoCancel(true)
                .setContentIntent(zamiarOczekujacy);

        budowniczyPowiadomien.setSubText("Pobrano");
        menedzerPowiadomien.notify(idPowiadomienia,budowniczyPowiadomien.build());
    }
}
