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
    private int rozmiar;


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
        int pobranoBajtow=0;
        //przygotujPowiadomienie();
        try {
            URL url = new URL(adres);
            polaczenie = (HttpURLConnection) url.openConnection();
            polaczenie.connect();
            File plikRoboczy = new File(url.getFile());
            File plikWyjsciowy = new File(
                    Environment.getExternalStorageDirectory() +
                            File.separator + plikRoboczy.getName());
            if (plikWyjsciowy.exists()) plikWyjsciowy.delete();

            rozmiar = polaczenie.getContentLength();
            strumienDoPliku = new FileOutputStream(plikWyjsciowy.getPath());
            DataInputStream czytnik = new DataInputStream(polaczenie.getInputStream());
            Log.d("P","Pobieraanie");
            byte[] bufor = new byte[1024];
            int pobrano = czytnik.read(bufor, 0, 1024);
            while (pobrano != -1) {
                strumienDoPliku.write(bufor, 0, pobrano);
                pobranoBajtow += pobrano;
                Log.d("P;","Pobrano:"+pobrano+" "+pobranoBajtow);
                wyslijBroadcast(pobranoBajtow, 1);
                pobrano = czytnik.read(bufor, 0, 1024);
            }
            Log.d("P","Pobrano");
      //      aktualizujPowiadomienie();
            strumienDoPliku.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            polaczenie.disconnect();
        }

    }

    public final static String POWIADOMIENIE = "com.example.student.komunikacja_sieciowa.odbiornik";
    public final static String INFO_O_POBIERANIU = "info o pobieraniu";

    private void wyslijBroadcast(int pobrano, int wynik) {
        PostepInfo postep = new PostepInfo();
        postep.mWynik = wynik;
        postep.mRozmiar = rozmiar;
        postep.mPobranychBajtow = pobrano;
        Log.d("Broad: ",pobrano+" "+wynik+" "+rozmiar);
        Log.d("Broad parcel:" , postep.mPobranychBajtow+" "+postep.mWynik+" "+postep.mRozmiar);
        Intent zamiar = new Intent(POWIADOMIENIE);
        zamiar.putExtra(INFO_O_POBIERANIU, postep);
        sendBroadcast(zamiar);
    }

//    NotificationManager menedzerPowiadomien;
//    int idPowiadomienia;
//    Notification.Builder budowniczyPowiadomien;
//    Intent zamiarPowiadomienia;
//
//    void przygotujPowiadomienie() {
//        Log.d("MP", "przygotujPowiadomienie()");
//
//        zamiarPowiadomienia = new Intent(this, MainActivity.class);
//
//        TaskStackBuilder budowniczyStosu = TaskStackBuilder.create(this);
//        budowniczyStosu.addParentStack(MainActivity.class);
//        budowniczyStosu.addNextIntent(zamiarPowiadomienia);
//
//        PendingIntent zamiarOczekujacy = budowniczyStosu.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        menedzerPowiadomien = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        idPowiadomienia = 1;
//        budowniczyPowiadomien = new Notification.Builder(this);
//        budowniczyPowiadomien.setContentTitle("Tytuł powiadomienia")
//                .setContentIntent(zamiarOczekujacy)
//                .setSubText("pobieranie")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
//                .setOngoing(true);
//
//        menedzerPowiadomien.notify(idPowiadomienia, budowniczyPowiadomien.build());
//    }
//
//    void aktualizujPowiadomienie() {
//        Log.d("MP", "aktualizujPowiadomienie()");
//        TaskStackBuilder budowniczyStosu = TaskStackBuilder.create(this);
//        budowniczyStosu.addParentStack(MainActivity.class);
//        budowniczyStosu.addNextIntent(zamiarPowiadomienia);
//
//        PendingIntent zamiarOczekujacy = budowniczyStosu.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        budowniczyPowiadomien.setOngoing(false) // już nie trwa
//                // po kliknięciu automatycznie się usunie z paska
//                .setAutoCancel(true)
//                .setContentIntent(zamiarOczekujacy);
//
//        budowniczyPowiadomien.setSubText("Pobrano");
//        menedzerPowiadomien.notify(idPowiadomienia, budowniczyPowiadomien.build());
//    }
}
