package com.e.wallzhub.Dashbaord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.wallzhub.BuildConfig;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.Collection;
import com.e.wallzhub.Fragments.FragmentParent;
import com.e.wallzhub.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Dashboard extends AppCompatActivity {
    public static Toolbar toolbar;
    private FragmentParent fragmentParent;
    private AdView mAdView;
    public static List<Collection> collectionsMain;
    private String TAG = "Changes";
    public static LinearLayout mLinearLayoutMain,linearLayoutMainSecond;
    public static com.google.android.gms.ads.interstitial.InterstitialAd mInterstitialAdGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //ADS
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }

                // Start loading ads here...
                loadBunnerAd();
                loadInterstitial();
            }
        });

        collectionsMain = new ArrayList<>();

        fragmentParent = (FragmentParent) this.getSupportFragmentManager().findFragmentById(R.id.fragmentParent);

        toolbar = findViewById(R.id.toolbar_main);
        mLinearLayoutMain = findViewById(R.id.linear_main_load);
        linearLayoutMainSecond = findViewById(R.id.linear_main_second);

        getSupportActionBar();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getResources().getString(R.string.app_name));


        loadingCollections();

    }

    @SuppressLint("MissingPermission")
    private void loadBunnerAd() {
        mAdView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, getResources().getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAdGoogle = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAdGoogle = null;
            }
        });
    }

    private void loadingCollections(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.collectionsUrl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.i("res_a", "[" + response + "]");

                JSONArray collections = new JSONArray(response);
                Log.i(TAG, "onResponse: dat"+response);
                if (collections.length()>0) {
                    linearLayoutMainSecond.setVisibility(View.GONE);
                    mLinearLayoutMain.setVisibility(View.VISIBLE);

                    fragmentParent.addPage("All");
                    //default colection
                    Collection collectionDft = new Collection("All");
                    collectionsMain.add(collectionDft);

                    for (int c = 0; c < shuffleJsonArray(collections).length(); c++) {
                        JSONObject collection = collections.getJSONObject(c);
                        String strCatName = collection.getString("strCatName");

                        com.e.wallzhub.Constants.Models.Collection collection1 = new com.e.wallzhub.Constants.Models.Collection(strCatName);
                        collectionsMain.add(collection1);
                        fragmentParent.addPage(strCatName);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("res_a", "[" + e.getMessage() + "]");
            }

        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }


    });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }


    public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void openAbout(){
        startActivity(new Intent(Dashboard.this,AboutUs.class));
    }

    private void openPlayStore(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+ BuildConfig.APPLICATION_ID)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.playstore_link))));
        }
    }

    private void openDeveloperStore(){
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=ClemMwa")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=ClemMwa")));
        }
    }

    private void openSocial(String url){
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}