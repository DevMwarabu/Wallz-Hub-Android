package com.e.wallzhub.Dashbaord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.Collection;
import com.e.wallzhub.Fragments.FragmentParent;
import com.e.wallzhub.R;

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
    public static List<Collection> collectionsMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        collectionsMain = new ArrayList<>();

        fragmentParent = (FragmentParent) this.getSupportFragmentManager().findFragmentById(R.id.fragmentParent);

        toolbar = findViewById(R.id.toolbar_main);
        mProgressBar = findViewById(R.id.progressBar);

        getSupportActionBar();
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        loadingCollections();
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
}