package com.e.wallzhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.e.wallzhub.Constants.Adapters.AdapterDEsc;
import com.e.wallzhub.Constants.Adapters.CenterZoomLayoutManager;
import com.e.wallzhub.Constants.Constants;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.Dashbaord.Dashboard;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;

public class ImageDesc extends AppCompatActivity {
    private ImageView mImageView;
    private ImageView mImageMenu;
    private RecyclerView mRecyclerView;
    private String collection, id, imageUrl;
    private AdView mAdView;
    private VideoView mVideoView;
    private ProgressBar mProgressBar;
    private int type;
    private Button mDownload;
    int positionV = 0;
    private CardView mMenu;
    private AdapterDEsc adapter;
    private List<ImageModel> imageModels;
    private ProgressDialog progressDialog;
    private InterstitialAd mInterstitialAd;
    private String TAG = "Changes";
    private AdRequest adRequest;
    private String filename = null,directoryNmae=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_desc);

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
            }
        });

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            imageUrl = uri.getQueryParameter("src");
            collection = uri.getQueryParameter("collection");
            id = uri.getQueryParameter("id");
            type = Integer.parseInt(uri.getQueryParameter("type"));
        } else {
            imageUrl = getIntent().getStringExtra("src");
            collection = getIntent().getExtras().getString("collection");
            id = getIntent().getExtras().getString("id");
            type = getIntent().getExtras().getInt("type");
        }

        progressDialog = new ProgressDialog(this);

        imageModels = new ArrayList<>();

        adapter = new AdapterDEsc(imageModels, this, collection);

        mImageView = findViewById(R.id.image_main);
        mRecyclerView = findViewById(R.id.recycler_main);
        mProgressBar = findViewById(R.id.progressBar2);
        mImageMenu = findViewById(R.id.image_menu);
        mDownload = findViewById(R.id.btn_download);
        mMenu = findViewById(R.id.card_main);
        mVideoView = findViewById(R.id.video_main);

        if (type > 0) {
            mVideoView.setVisibility(View.VISIBLE);
        } else {
            mImageView.setVisibility(View.VISIBLE);
        }

        CenterZoomLayoutManager centerZoomLayoutManager = new CenterZoomLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(centerZoomLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        checkPermission();

        getData(collection + "&locale=en-US&per_page=15");
        //load data

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type > 0) {
                    downloading(imageUrl);
                } else {
                    shoWbottomSheet(imageUrl);
                }

            }
        });

        mMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

    }

    @SuppressLint("MissingPermission")
    private void loadBunnerAd() {
        mAdView = findViewById(R.id.adview);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hi! You can check this image on WallzHub: https://wallzhub.com?collection=" + collection + "&src=" + imageUrl + "&id=" + id + "&type=" + type;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void InterstitialAdmob() {
        InterstitialAd.load(ImageDesc.this, getString(R.string.interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(ImageDesc.this);
                }
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        mInterstitialAd = null;

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
        //InterstitialAdmob();
    }

    private void getData(String collection) {

        RequestOptions requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .skipMemoryCache(true)
                .centerCrop()
                .dontAnimate()
                .dontTransform()
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.place_holder)
                .priority(Priority.IMMEDIATE)
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .format(DecodeFormat.DEFAULT)
                .override(640, 799);

        switch (type) {
            case 0:
                //setting imageView
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(imageUrl + "?auto=compress&cs=tinysrgb&w=640&h=799")
                        .apply(requestOptions)
                        .dontAnimate()
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mImageView.setImageBitmap(resource);
                                mProgressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        });
                break;
            default:
                Uri uri = Uri.parse(imageUrl);
                mVideoView.setVideoURI(uri);
                mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mProgressBar.setVisibility(View.GONE);
                        mp.setLooping(true);
                        mVideoView.start();
                    }
                });
                mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        mp.stop();
                        mImageView.setVisibility(View.VISIBLE);
                        mImageView.setImageResource(R.drawable.place_holder);
                        mVideoView.setVisibility(View.GONE);
                        return false;
                    }
                });
                mVideoView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (mVideoView.isPlaying()) {
                            mVideoView.pause();
                            positionV = mVideoView.getCurrentPosition();
                        } else {
                            mVideoView.seekTo(positionV);
                            mVideoView.start();
                        }
                        return false;
                    }
                });
                break;
        }

        //loading data
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.baseUrl_Collection + collection, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.i("res_beer", "[" + response + "]");
                    JSONObject object = new JSONObject(response);
                    //Dashboard.toolbar.setSubtitle("Total result " + object.getString("total_results"));

                    JSONArray jsonArray = object.getJSONArray("photos");
                    if (shuffleJsonArray(jsonArray).length() > 0) {

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            String photographer = jsonObject.getString("photographer");
                            String photographer_url = jsonObject.getString("photographer_url");
                            String id = jsonObject.getString("id");
                            JSONObject src = jsonObject.getJSONObject("src");

                            //adding to list
                            ImageModel imageModel = new ImageModel(photographer, photographer_url, id, src, 0);
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

    private void shoWbottomSheet(String src) {
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
                                downloading(src);
                            }
                            if (mLarge.isChecked()) {
                                // new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w=1920&h=2399");
                                downloading(src + "?auto=compress&cs=tinysrgb&w=1920&h=2399");
                            }
                            if (mMedium.isChecked()) {
                                // new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w=1280&h=1599");
                                downloading(src + "?auto=compress&cs=tinysrgb&w=1280&h=1599");
                            }
                            if (mSmall.isChecked()) {
                                //new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w=640&h=799");
                                downloading(src + "?auto=compress&cs=tinysrgb&w=640&h=799");
                            }
                            if (mCustome.isChecked()) {
                                //checkingi fcustom heigh
                                if (TextUtils.isEmpty(w) && TextUtils.isEmpty(h)) {
                                    Toast.makeText(ImageDesc.this, "Enter custome sizes", Toast.LENGTH_SHORT).show();
                                } else {
                                    // new Downloading().onPostExecute(src.getString("original")+"?auto=compress&cs=tinysrgb&w="+w+"&h="+h);
                                    downloading(src + "?auto=compress&cs=tinysrgb&w=" + w + "&h=" + h);
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

    private void downloading(String url) {
        if (type > 0) {
            filename = "Wallz" + RandomStringUtils.randomAlphanumeric(10) + ".mp4";

        } else {
            filename = "Wallz" + RandomStringUtils.randomAlphanumeric(10) + ".jpg";

        }
        String downloadUrlOfImage = url;

        if (type > 0) {
            directoryNmae = "Wallz Videos";

        } else {
            directoryNmae = "Wallz Images";

        }
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
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                            File.separator + directoryNmae + File.separator + filename);
            if (type > 0) {

                request.setMimeType("mp4");
            } else {

                request.setMimeType("image/jpeg");
            }

            dm.enqueue(request);
            Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Storage permission denied make sure you allow this app to write your phone storage", Toast.LENGTH_SHORT).show();
        }
    }
}