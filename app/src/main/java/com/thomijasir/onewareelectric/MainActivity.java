package com.thomijasir.onewareelectric;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    WebView webview, web;
    LinearLayout linearLayout,linearLayoutwebview,loadfail;
    Animation slideUp,slideDown,fadeOut,fadeIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = (LinearLayout) findViewById(R.id.loadingMatrinxAset);
        linearLayoutwebview = (LinearLayout) findViewById(R.id.webViewLayout);
        loadfail = (LinearLayout) findViewById(R.id.failload);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_out);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);

        PusherOptions options = new PusherOptions();
        options.setCluster("ap1");
        Pusher pusher = new Pusher("7106608cddcc977d6465", options);

        Channel channel = pusher.subscribe("my-channel");

        channel.bind("my-event", new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                System.out.println(data);
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                showNotification(currentDateTimeString);
            }
        });

        pusher.connect();

        web = (WebView) findViewById(R.id.matrixview);
        web.loadUrl("https://oneacademic.000webhostapp.com/oneelectric/public/");
        web.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                linearLayout.setVisibility(View.GONE);
                linearLayout.startAnimation(fadeOut);
                linearLayoutwebview.setVisibility(View.VISIBLE);
                linearLayoutwebview.startAnimation(fadeIn);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                web.loadUrl("file:///android_asset/errorpage.html");
                linearLayout.setVisibility(View.GONE);
                linearLayout.startAnimation(fadeOut);
                linearLayoutwebview.setVisibility(View.GONE);
                linearLayoutwebview.startAnimation(fadeOut);
                loadfail.setVisibility(View.VISIBLE);
                loadfail.setAnimation(fadeIn);
                Log.e("ACTIVITY","ERROR THOE");
            }
        });
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
    public void showNotification(String timestamp) {
        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Oneware")
                .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
                .setContentTitle("Oneware Electric")
                .setContentText("Motion Detected! at "+timestamp)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
