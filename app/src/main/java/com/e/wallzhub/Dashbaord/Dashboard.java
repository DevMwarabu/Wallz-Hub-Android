package com.e.wallzhub.Dashbaord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.wallzhub.BuildConfig;
import com.e.wallzhub.Constants.Adapters.SliderAdapter;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.AboutModel;
import com.e.wallzhub.Constants.Models.Advert;
import com.e.wallzhub.Constants.Models.Collection;
import com.e.wallzhub.Fragments.FragmentParent;
import com.e.wallzhub.ImageDesc;
import com.e.wallzhub.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Dashboard extends AppCompatActivity {
    public static Toolbar toolbar;
    private FragmentParent fragmentParent;
    private ProgressBar mProgressBar;
    private AdView mAdView;
    public static List<Collection> collectionsMain;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private SliderView sliderView;

    private SliderAdapter adapter;
    private List<Advert> adverts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        MobileAds.initialize(this, getString(R.string.appid));
        mAdView =(AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        collectionsMain = new ArrayList<>();
        adverts = new ArrayList<>();
        adapter = new SliderAdapter(this,adverts);

        fragmentParent = (FragmentParent) this.getSupportFragmentManager().findFragmentById(R.id.fragmentParent);

        toolbar = findViewById(R.id.toolbar_main);
        mProgressBar = findViewById(R.id.progressBar);
        sliderView = findViewById(R.id.imageSlider_one);

        getSupportActionBar();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        //customization slideView
        sliderView.setSliderTransformAnimation(SliderAnimations.FADETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        sliderView.setIndicatorSelectedColor(getResources().getColor(R.color.secondaryColor));
        sliderView.setIndicatorUnselectedColor(getResources().getColor(R.color.colorHelper));
        sliderView.setScrollTimeInSec(4);
        sliderView.startAutoCycle();

        loadSlides();

    }

    private void loadSlides() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.about_policy, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                loadingCollections();
                try {
                    Log.i("res_beer", "[" + response + "]");

                    JSONArray jsonArray = response.getJSONArray("slide_images");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String title = jsonObject.getString("title");
                        String redirect_link = jsonObject.getString("redirect_link");
                        String imageUrl = jsonObject.getString("imageUrl");

                        //adding to list
                        Advert advert = new Advert(title,redirect_link,imageUrl);
                        adverts.add(advert);
                        //notifyadapter changes
                        sliderView.setSliderAdapter(adapter, true);


                    }
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer", "[" + e.getMessage() + "]");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_beer", "[" + error.getMessage() + "]");
            }
        });
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(jsonObjectRequest);

    }

    private void loadingCollections(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.collectionsUrl, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                Log.i("res_a", "[" + response + "]");

                JSONArray collections = new JSONArray(response);
                if (collections.length()>0) {
                    mProgressBar.setVisibility(View.GONE);

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

    public void InterstitialAdmob() {
        InterstitialAd.load(Dashboard.this,getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        mInterstitialAd=null;

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mInterstitialAd = null;
                        /// perform your action here when ad will not load
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        mInterstitialAd = null;

                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;


            }

        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adRequest = new AdRequest.Builder().build();
        InterstitialAdmob();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        menuClicks(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    private void menuClicks(int r){
        switch (r){
            case R.id.nav_share:
                if (mInterstitialAd!=null){
                    mInterstitialAd.show(Dashboard.this);
                    InterstitialAdmob();
                }
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hi! Update your photos using Wallz Hub App, for more information please click this link: https://play.google.com/store/apps/details?id=com.e.wallzhub";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Wallz Hub");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.nav_update:
                if (mInterstitialAd!=null){
                    mInterstitialAd.show(Dashboard.this);
                    InterstitialAdmob();
                }
                openPlayStore();
                break;
            case R.id.nav_more:
                if (mInterstitialAd!=null){
                    mInterstitialAd.show(Dashboard.this);
                    InterstitialAdmob();
                }
                openDeveloperStore();
                break;
            case R.id.nav_facebook:
                openSocial("https://www.facebook.com/wallz.hub");
                break;
            case R.id.nav_instagram:
                openSocial("https://www.instagram.com/wallz_hub/");
                break;
            case R.id.nav_twitter:
                openSocial("https://www.twitter.com/wallzhub");
                break;
            case R.id.nav_about:
                openAbout();
                break;
        }
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

        if (mInterstitialAd!=null){
            mInterstitialAd.show(Dashboard.this);
            InterstitialAdmob();
        }
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}