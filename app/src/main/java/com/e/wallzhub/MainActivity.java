package com.e.wallzhub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Dashbaord.Dashboard;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private LinearLayout mLinearLayoutMain, mlLinearLayoutNoInternet;
    private Button mRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRetry = findViewById(R.id.btn_retry);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLinearLayoutMain.setVisibility(View.VISIBLE);
                mlLinearLayoutNoInternet.setVisibility(View.GONE);
                onStart();
            }
        });
    }

    @Override
    protected void onStart() {
        mLinearLayoutMain = findViewById(R.id.linear_loading);
        mlLinearLayoutNoInternet = findViewById(R.id.linear_no_internet);
        //loading();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //loading next activity
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        },2000);
        super.onStart();
    }

    private void loading() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.gettingUniversities, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //loading next activity
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //showing error linear
                mLinearLayoutMain.setVisibility(View.GONE);
                mlLinearLayoutNoInternet.setVisibility(View.VISIBLE);
            }


        });


        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }
}