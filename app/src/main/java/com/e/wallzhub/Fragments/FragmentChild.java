package com.e.wallzhub.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.wallzhub.Constants.Adapters.Adapter;
import com.e.wallzhub.Constants.Adapters.AdapterAds;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.Dashbaord.Dashboard;
import com.e.wallzhub.MainActivity;
import com.e.wallzhub.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FragmentChild extends Fragment {
    private View mView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String title;
    private AdapterAds adapter;
    private List<ImageModel> imageModels;
    public static final int ITEMS_PER_AD = 7;
    private ArrayList<Object> mListItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_child, container, false);
        //getting bandle arguments
        Bundle bundle = getArguments();
        title = bundle.getString("title");

        initAdMobAdsSDK();

        imageModels = new ArrayList<>();

        adapter = new AdapterAds(mListItems, getContext(),title);

        mRecyclerView = mView.findViewById(R.id.recycler_main);
        mSwipeRefreshLayout = mView.findViewById(R.id.swipe_main);



        //ADS
        addAdMobBannerAds();

        int mNoOfColumns = FragmentChild.Utility.calculateNoOfColumns(getContext(), 180);

        final LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), mNoOfColumns, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


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
                    getData(title + "&locale=en-US&per_page=24&page=1");
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
                    getData(title + "&locale=en-US&per_page=24&page=1");
                }

            }
        });
        loadBannerAds();


        return mView;
    }

    private void getData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.baseUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Dashboard.toolbar.setSubtitle("Total result " + response.getString("total_results"));
                    Log.i("res_beer", "[" + response + "]");

                    JSONArray jsonArray = response.getJSONArray("photos");

                    if (shuffleJsonArray(jsonArray).length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String photographer = jsonObject.getString("photographer");
                            String photographer_url = jsonObject.getString("photographer_url");
                            String id = jsonObject.getString("id");
                            JSONObject src = jsonObject.getJSONObject("src");

                            //adding to list
                            ImageModel imageModel = new ImageModel(photographer, photographer_url, id, src);
                            imageModels.add(imageModel);

                            //add to list
                            mListItems.add(imageModel);
                            //notifyadapter changes
                            mRecyclerView.setAdapter(adapter);


                        }
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                    if (shuffleJsonArray(jsonArray).length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String photographer = jsonObject.getString("photographer");
                            String photographer_url = jsonObject.getString("photographer_url");
                            String id = jsonObject.getString("id");
                            JSONObject src = jsonObject.getJSONObject("src");

                            //adding to list
                            ImageModel imageModel = new ImageModel(photographer, photographer_url, id, src);
                            imageModels.add(imageModel);
                            //notifyadapter changes

                            //add to list
                            mListItems.add(imageModel);
                            mRecyclerView.setAdapter(adapter);


                        }
                        adapter.notifyDataSetChanged();
                        mSwipeRefreshLayout.setRefreshing(false);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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

    private void initAdMobAdsSDK() {
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    private void addAdMobBannerAds() {
        for (int i = ITEMS_PER_AD; i <= mListItems.size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(getContext());
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId(getResources().getString(R.string.banner));
            mListItems.add(i, adView);
        }

        loadBannerAds();
    }

    private void loadBannerAds() {
        //Load the first banner ad in the items list (subsequent ads will be loaded automatically in sequence).
        loadBannerAd(ITEMS_PER_AD);
    }

    private void loadBannerAd(final int index)
    {
        if (index >= mListItems.size())
        {
            return;
        }

        Object item = mListItems.get(index);
        if (!(item instanceof AdView))
        {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad" + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener()
        {
            @Override
            public void onAdLoaded()
            {
                super.onAdLoaded();
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode)
            {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                        + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }


    @Override
    public void onResume()
    {
        for (Object item : mListItems)
        {
            if (item instanceof AdView)
            {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause()
    {
        for (Object item : mListItems)
        {
            if (item instanceof AdView)
            {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        for (Object item : mListItems)
        {
            if (item instanceof AdView)
            {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();
    }


    public static class Utility {
        public static int calculateNoOfColumns(Context context, float columnWidthDp) { // For example columnWidthdp=180
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float screenWidthDp = displayMetrics.widthPixels / displayMetrics.density;
            return (int) (screenWidthDp / columnWidthDp + 0.5);
        }
    }
}