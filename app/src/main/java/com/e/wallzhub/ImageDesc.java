package com.e.wallzhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.e.wallzhub.Constants.Adapters.AdapterAds;
import com.e.wallzhub.Constants.Adapters.AdapterDEsc;
import com.e.wallzhub.Constants.Adapters.CenterZoomLayoutManager;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.Dashbaord.Dashboard;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;

public class ImageDesc extends AppCompatActivity {
    private RoundedImageView mImageView;
    private ImageView mImageMenu;
    private RecyclerView mRecyclerView;
    private String collection;
    private AdView mAdView;
    private JSONObject src;
    private Button mDownload;
    private AdapterDEsc adapter;
    private List<ImageModel> imageModels;
    private ProgressDialog progressDialog;
    private InterstitialAd mInterstitialAd;
    private String TAG = "Changes";
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_desc);
        //facebook sdk init
        AudienceNetworkAds.initialize(this);
        mInterstitialAd = new InterstitialAd(this, getResources().getString(R.string.fbinterstitila));

        MobileAds.initialize(this, getString(R.string.appid));

        mAdView = (AdView) findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        try {
            src = new JSONObject(getIntent().getStringExtra("src"));
            collection = getIntent().getExtras().getString("collection");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialog = new ProgressDialog(this);

        imageModels = new ArrayList<>();

        adapter = new AdapterDEsc(imageModels, this, collection);

        mImageView = findViewById(R.id.image_main);
        mRecyclerView = findViewById(R.id.recycler_main);
        mImageMenu = findViewById(R.id.image_menu);
        mDownload = findViewById(R.id.btn_download);

        CenterZoomLayoutManager centerZoomLayoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(centerZoomLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkPermission();

        //load data
        try {
            getData(collection, src);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoWbottomSheet(src);
            }
        });

    }

    public void InterstitialAdmob() {
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                mInterstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        mInterstitialAd.loadAd(
                mInterstitialAd.buildLoadAdConfig()
                        .withAdListener(interstitialAdListener)
                        .build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        adRequest = new AdRequest.Builder().build();
        InterstitialAdmob();
    }

    private void getData(String collection, JSONObject src) throws JSONException {
        //setting imageView
        Glide.with(getApplicationContext()).load(src.getString("large"))
                .apply(new RequestOptions()
                        .centerCrop()
                        .dontTransform()
                        .format(DecodeFormat.PREFER_ARGB_8888))
                .placeholder(R.mipmap.ic_launcher_foreground)
                .error(R.mipmap.ic_launcher_foreground)
                .dontAnimate()
                .override(mImageView.getMeasuredWidth(),mImageView.getMeasuredHeight())
                .into(mImageView);

        //loading data
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
                            mRecyclerView.setAdapter(adapter);


                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        //nothing found

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("res_beer", "[" + e.getMessage() + "]");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    private void shoWbottomSheet(JSONObject src) {
        View viewBottom = getLayoutInflater().inflate(R.layout.bottomsheet_layout, null);
        RadioGroup mRadioGroup = viewBottom.findViewById(R.id.radio_group);
        RadioButton mOriginal = viewBottom.findViewById(R.id.radio_original);
        RadioButton mLarge = viewBottom.findViewById(R.id.radio_large);
        RadioButton mMedium = viewBottom.findViewById(R.id.radio_medium);
        RadioButton mSmall = viewBottom.findViewById(R.id.radio_small);
        RadioButton mCustome = viewBottom.findViewById(R.id.radio_custom);
        //edts
        EditText mWidth = viewBottom.findViewById(R.id.edt_width);
        EditText mHeight = viewBottom.findViewById(R.id.edt_height);

        Button mDownloading = viewBottom.findViewById(R.id.btn_download_bottom);


        final Dialog mBottomSheetDialog = new Dialog(this, R.style.SheetDialog);
        mBottomSheetDialog.setContentView(viewBottom);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        mDownloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String w = mWidth.getText().toString().trim();
                String h = mHeight.getText().toString().trim();

                //checking if checked
                if (mRadioGroup.getCheckedRadioButtonId() != -1) {
                    if (checkPermission()) {
                        try {
                            if (mOriginal.isChecked()) {
                                //  new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w=2946&h=3682");
                                downloadig(src.getString("original") + "?auto=compress&cs=tinysrgb&w=2946&h=3682");
                            }
                            if (mLarge.isChecked()) {
                                // new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w=1920&h=2399");
                                downloadig(src.getString("original") + "?auto=compress&cs=tinysrgb&w=1920&h=2399");
                            }
                            if (mMedium.isChecked()) {
                                // new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w=1280&h=1599");
                                downloadig(src.getString("original") + "?auto=compress&cs=tinysrgb&w=1280&h=1599");
                            }
                            if (mSmall.isChecked()) {
                                //new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w=640&h=799");
                                downloadig(src.getString("original") + "?auto=compress&cs=tinysrgb&w=640&h=799");
                            }
                            if (mCustome.isChecked()) {
                                //checkingi fcustom heigh
                                if (TextUtils.isEmpty(w) && TextUtils.isEmpty(h)) {
                                    Toast.makeText(ImageDesc.this, "Enter custome sizes", Toast.LENGTH_SHORT).show();
                                } else {
                                    // new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w="+w+"&h="+h);
                                    downloadig(src.getString("original") + "?auto=compress&cs=tinysrgb&w=" + w + "&h=" + h);
                                }
                            }

                            mBottomSheetDialog.dismiss();
                        } catch (Exception e) {

                        }
                    }

                } else {
                    Toast.makeText(ImageDesc.this, "Please select size..!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_WRITE);
            return false;
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do somethings
        }
    }

    private void downloadig(String url) {
        String filename = "Wallz" + RandomStringUtils.randomAlphanumeric(10) + ".jpg";
        String downloadUrlOfImage = url;
        String directoryNmae = "Wallz Images";
        File direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + directoryNmae + "/");


        if (!direct.exists()) {
            direct.mkdir();
        }
        try {
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                            File.separator + directoryNmae + File.separator + filename);

            dm.enqueue(request);
            Toast.makeText(this, "Saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Storage permission denied make sure you allow this app to wriste your phone storage", Toast.LENGTH_SHORT).show();
        }
    }
}