package com.e.wallzhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.wallzhub.Constants.Adapters.AdapterAds;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.Dashbaord.Dashboard;
import com.e.wallzhub.Fragments.FragmentChild;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Searching extends AppCompatActivity {
    private CardView mBack;
    private TextView mTitle;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String title;
    private AdapterAds adapter;
    private List<ImageModel> imageModels;
    private LinearLayout linearLayoutMainSecond;
    private ConstraintLayout mLinearLayoutMain;
    int page = 0;
    private FloatingActionButton mNext, mPrev;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);

        title = getIntent().getExtras().getString("title");
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
                //ADS
                loadInterstitial();
            }
        });

        imageModels = new ArrayList<>();

        adapter = new AdapterAds(imageModels, this, title, mInterstitialAd);

        mRecyclerView = findViewById(R.id.recycler_main);
        mSwipeRefreshLayout = findViewById(R.id.swipe_main);
        mPrev = findViewById(R.id.float_prev);
        mNext = findViewById(R.id.float_next);
        mLinearLayoutMain = findViewById(R.id.linear_main_load);
        linearLayoutMainSecond = findViewById(R.id.linear_main_second);
        mBack = findViewById(R.id.card_back);
        mTitle = findViewById(R.id.tv_title);

        mTitle.setText(title);

        int mNoOfColumns = FragmentChild.Utility.calculateNoOfColumns(this, 180);

        final LinearLayoutManager layoutManager = new GridLayoutManager(this, mNoOfColumns, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);
        mRecyclerView.setDrawingCacheEnabled(true);
        mRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        mSwipeRefreshLayout.setColorSchemeResources(R.color.primaryColor,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                if (title.equals("All")) {
                    getData();
                } else {
                    getData(title + "&locale=en-US&per_page=80");
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.clear();
                adapter.addAll(imageModels);
                //loading
                if (title.equals("All")) {
                    getData();
                } else {
                    getData(title + "&locale=en-US&per_page=80");
                }

            }
        });


        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //clicks
        mPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPrevClicks(page);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNextClicks(page);
            }
        });
    }


    private void buttonNextClicks(int page) {
        mSwipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.addAll(imageModels);
        //loading
        if (title.equals("All")) {
            getDataNext("https://pexelsdimasv1.p.rapidapi.com/v1/curated?per_page=80&page=" + (page + 1));
        } else {
            getData(title + "&locale=en-US&per_page=80&page=" + (page + 1));
        }
    }

    private void buttonPrevClicks(int page) {
        mSwipeRefreshLayout.setRefreshing(true);
        adapter.clear();
        adapter.addAll(imageModels);
        //loading
        if (title.equals("All")) {
            getData();
        } else {
            getData(title + "&locale=en-US&per_page=80");
        }
    }

    private void getData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl_Collection+title + "&locale=en-US&per_page=80&page=1", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Dashboard.toolbar.setSubtitle("Total result " + response.getString("total_results"));
                    Log.i("res_data", "[" + response + "]");

                    JSONArray jsonArray = response.getJSONArray("photos");

                    if (jsonArray.length() > 0) {

                        Log.i("TAG", "onResponse: data loaded");
                    }

                    if (shuffleJsonArray(jsonArray).length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String photographer = jsonObject.getString("photographer");
                            String photographer_url = jsonObject.getString("photographer_url");
                            String id = jsonObject.getString("id");
                            JSONObject src = jsonObject.getJSONObject("src");

                            //adding to list
                            ImageModel imageModel = new ImageModel(photographer, photographer_url, id, src,0);
                            imageModels.add(imageModel);


                            if (i == (jsonArray.length()-1)){
                                getVideoData(title+"&per_page=80");
                            }


                        }
                    } else {
                        //nothing found

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_beer", "[" + error.getMessage() + "]");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("authorization", Constants.api_key);
                map.put("x-rapidapi-key", "d498088a25msheae6bb5b8a6fc9fp1c3886jsn5cef9e372445");
                map.put("x-rapidapi-host", "PexelsdimasV1.p.rapidapi.com");
                return map;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(jsonObjectRequest);

    }

    private void getVideoData(String title) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.videos+title, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.i("res_data", "[" + response + "]");

                    JSONArray jsonArray = response.getJSONArray("videos");

                    if (jsonArray.length() > 0) {

                        Log.i("TAG", "onResponse: data loaded");
                    }

                    if (shuffleJsonArray(jsonArray).length() > 0) {
                        linearLayoutMainSecond.setVisibility(View.GONE);
                        mLinearLayoutMain.setVisibility(View.VISIBLE);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String id = jsonObject.getString("id");
                            JSONObject user = jsonObject.getJSONObject("user");
                            JSONArray video_files = jsonObject.getJSONArray("video_files");
                            JSONArray video_pictures = jsonObject.getJSONArray("video_pictures");
                            //adding to lists
                            imageModels.add(new ImageModel(user,video_files,video_pictures,id,1));
                            //notifyadapter changes
                            mRecyclerView.setAdapter(adapter);


                            if (i == (jsonArray.length()-1)){
                                Collections.shuffle(imageModels,new Random());
                            }


                        }
                    }
                    adapter.notifyItemRangeChanged(0,(imageModels.size()));
                    adapter.setHasStableIds(true);
                    mSwipeRefreshLayout.setRefreshing(false);


                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_beer", "[" + error.getMessage() + "]");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("authorization", Constants.api_key);
                map.put("x-rapidapi-key", "d498088a25msheae6bb5b8a6fc9fp1c3886jsn5cef9e372445");
                map.put("x-rapidapi-host", "PexelsdimasV1.p.rapidapi.com");
                return map;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(jsonObjectRequest);

    }

    private void getDataNext(String url) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Dashboard.toolbar.setSubtitle("Total result " + response.getString("total_results"));
                    Log.i("res_beer", "[" + response + "]");

                    JSONArray jsonArray = response.getJSONArray("photos");

                    if (shuffleJsonArray(jsonArray).length() > 0) {
                        linearLayoutMainSecond.setVisibility(View.GONE);
                        mLinearLayoutMain.setVisibility(View.VISIBLE);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String photographer = jsonObject.getString("photographer");
                            String photographer_url = jsonObject.getString("photographer_url");
                            String id = jsonObject.getString("id");
                            JSONObject src = jsonObject.getJSONObject("src");

                            //adding to list
                            ImageModel imageModel = new ImageModel(photographer, photographer_url, id, src,0);
                            imageModels.add(imageModel);


                            if (i == (jsonArray.length()-1)){
                                getVideoData(title+"&per_page=80&page="+(page+1));
                            }


                        }
                    } else {
                        //nothing found

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("res_beer", "[" + error.getMessage() + "]");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("authorization", Constants.api_key);
                map.put("x-rapidapi-key", "d498088a25msheae6bb5b8a6fc9fp1c3886jsn5cef9e372445");
                map.put("x-rapidapi-host", "PexelsdimasV1.p.rapidapi.com");
                return map;
            }
        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(jsonObjectRequest);

    }

    private void getData(String collection) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.baseUrl_Collection + collection, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("res_beer", "[" + response + "]");
                    JSONObject object = new JSONObject(response);
                    Dashboard.toolbar.setSubtitle("Total result " + object.getString("total_results"));

                    JSONArray jsonArray = object.getJSONArray("photos");

                    if (jsonArray.length() > 0) {

                        Log.i("TAG", "onResponse: data loaded");
                    }


                    if (shuffleJsonArray(jsonArray).length() > 0) {
//                        linearLayoutMainSecond.setVisibility(View.GONE);
//                        mLinearLayoutMain.setVisibility(View.VISIBLE);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String photographer = jsonObject.getString("photographer");
                            String photographer_url = jsonObject.getString("photographer_url");
                            String id = jsonObject.getString("id");
                            JSONObject src = jsonObject.getJSONObject("src");

                            //adding to list
                            ImageModel imageModel = new ImageModel(photographer, photographer_url, id, src,0);
                            imageModels.add(imageModel);
                            //mRecyclerView.setAdapter(adapter);


                            if (i == (jsonArray.length()-1)){
                                getVideoData(title+"&per_page=80");
                            }


                        }
//                        adapter.notifyDataSetChanged();
//                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer", "[" + e.getMessage() + "]");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mSwipeRefreshLayout.setRefreshing(false);

            }


        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("authorization", Constants.api_key);
                map.put("x-rapidapi-key", "d498088a25msheae6bb5b8a6fc9fp1c3886jsn5cef9e372445");
                map.put("x-rapidapi-host", "PexelsdimasV1.p.rapidapi.com");
                return map;
            }


        };
        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }

    public static JSONArray shuffleJsonArray(JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--) {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();

        com.google.android.gms.ads.interstitial.InterstitialAd.load(this, getResources().getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull com.google.android.gms.ads.interstitial.InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }
        });
    }
}