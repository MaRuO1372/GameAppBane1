package com.baza.gameappbane;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baza.gameappbane.ImageViewScrolling.IEventEnd;
import com.baza.gameappbane.ImageViewScrolling.ImageViewScrolling;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.util.Random;

public class MainActivity  extends AppCompatActivity implements IEventEnd {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String text = "url";

    private ImageView btn_up, btn_down;
    private ImageViewScrolling image, image2, image3;
    private TextView txt_score;
    private WebView webView;
    private FrameLayout frameBar;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference reference = firebaseDatabase.getReference();
    private DatabaseReference childReference = reference.child("url");

    int count_done=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        frameBar = findViewById(R.id.frame_bar);

        webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                webView.setVisibility(View.GONE);
                btn_up.setVisibility(View.VISIBLE);
                txt_score.setVisibility(View.VISIBLE);
                frameBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                saveData(url);
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String url2 = "https://";
                if(uri.toString().startsWith(url2)){
                    view.loadUrl(uri.toString());
                    saveData(uri.toString());
                    return true;
                }else {
                    view.loadUrl(uri.toString());
                    saveData(uri.toString());
                    return true;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        btn_up = findViewById(R.id.image2);
        //btn_down = findViewById(R.id.btn_down);

        image = findViewById(R.id.image);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);

        txt_score = findViewById(R.id.txt_score);

        image.setEventEnd(MainActivity.this);
        image2.setEventEnd(MainActivity.this);
        image3.setEventEnd(MainActivity.this);

        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_up.setVisibility(View.GONE);
                btn_down.setVisibility(View.VISIBLE);

                if (Common.SCORE >= 500) {
                    image.setValueRandom(new Random().nextInt(6), new Random().nextInt((15-5)+1)+5);
                    image2.setValueRandom(new Random().nextInt(6), new Random().nextInt((15-5)+1)+5);
                    image3.setValueRandom(new Random().nextInt(6), new Random().nextInt((15-5)+1)+5);

                    Common.SCORE -= 500;
                    txt_score.setText(String.valueOf(Common.SCORE));
                }
                else {
                    Toast.makeText(MainActivity.this, "Not enough money", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();

        if(intent != null && intent.getData() != null ){
            imageGone();
            webView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)); //webview settings part
            Uri data = intent.getData();
            webView.loadUrl(data.toString());
            webView.setVisibility(View.VISIBLE);
            saveData(data.toString());
        }

        loadWebview();
    }

    public void loadWebview(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String regUrl = sharedPreferences.getString("url", "false");
        URI uri = URI.create(regUrl);
        if(!regUrl.equals("false")) {
            webView.loadUrl(uri.toString());
            imageGone();
            webView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)); //webview settings part
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        childReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String regUrl = sharedPreferences.getString("url", "false");
                String message = dataSnapshot.getValue(String.class);
                assert message != null;
                if (!message.equals("") && regUrl.equals("false")){
                    webView.loadUrl(message);
                    saveData(message);
                    imageGone();
                    webView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)); //webview settings part
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            webView.setVisibility(View.GONE);
        }
    }

    @Override
    public void eventEnd(int result, int count) {
        if(count_done < 2)
            count_done++;
        else {
            btn_down.setVisibility(View.GONE);
            btn_up.setVisibility(View.VISIBLE);

            count_done = 0;

            if (image.getValue() == image2.getValue() && image2.getValue() == image3.getValue()) {
                Toast toast = Toast.makeText(this, "WOW, +2000!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Common.SCORE += 2000;
                txt_score.setText(String.valueOf(Common.SCORE));
            } else if (image.getValue() == image2.getValue() ||
                    image2.getValue() == image3.getValue() ||
                    image3.getValue() == image.getValue()) {
                Toast toast = Toast.makeText(this, "You won a small prize +750!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Common.SCORE += 750;
                txt_score.setText(String.valueOf(Common.SCORE));
            } else{
                Toast toast = Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    public void imageGone(){
        btn_up.setVisibility(View.GONE);
        txt_score.setVisibility(View.GONE);
        frameBar.setVisibility(View.GONE);
    }

    public void saveData(String url){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(text, url);
        editor.apply();
    }
}
