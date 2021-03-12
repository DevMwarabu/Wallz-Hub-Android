package com.e.wallzhub.Dashbaord;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.e.wallzhub.Constants.Adapters.Adapter;
import com.e.wallzhub.Constants.Adapters.CenterZoomLayoutManager;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.AboutModel;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutUs extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private List<AboutModel> aboutModels;
    private Adapter adapter;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        MobileAds.initialize(this, getString(R.string.appid));
        mAdView =(AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        aboutModels = new ArrayList<>();
        adapter =new Adapter(aboutModels,this);

        toolbar = findViewById(R.id.toolbar_main);
        mRecyclerView = findViewById(R.id.recycler_main);
        getSupportActionBar();
        setSupportActionBar(toolbar);
        toolbar.setTitle("About Us");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        CenterZoomLayoutManager centerZoomLayoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(centerZoomLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        getData();
    }

    private void getData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.about_policy, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("res_beer", "[" + response + "]");

                    JSONArray jsonArray = response.getJSONArray("policies");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String title = jsonObject.getString("title");
                        String body = jsonObject.getString("body");

                        //adding to list
                        AboutModel aboutModel = new AboutModel(title,body);
                        aboutModels.add(aboutModel);
                        //notifyadapter changes
                        mRecyclerView.setAdapter(adapter);


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
}