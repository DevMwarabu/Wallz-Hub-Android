package com.e.wallzhub.Constants.Adapters;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.BuildConfig;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.Fragments.FragmentChild;
import com.e.wallzhub.ImageDesc;
import com.e.wallzhub.R;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterAds extends RecyclerView.Adapter<AdapterAds.ViewHolder> {
    private List<ImageModel> imageModels;
    private Context context;
    private String title;
    private InterstitialAd mInterstitialAd;

    public AdapterAds(List<ImageModel> imageModels, Context context, String title, InterstitialAd mInterstitialAd) {
        this.imageModels = imageModels;
        this.context = context;
        this.title = title;
        this.mInterstitialAd = mInterstitialAd;
    }

    public void clear() {
        imageModels.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ImageModel> imageModels) {
        imageModels.addAll(imageModels);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_main, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            holder.settingImage(imageModels.get(position).getSrc(), imageModels.get(position).getPhotographer(), imageModels.get(position).getType(), imageModels.get(position).getVideo_pictures(), imageModels.get(position).getUser());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) context);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                            Intent intent = new Intent(context, ImageDesc.class);
                            try {
                                if (imageModels.get(position).getType() < 1) {
                                    intent.putExtra("src", imageModels.get(position).getSrc().getString("original"));
                                } else {
                                    for (int i=0; i<=imageModels.get(position).getVideo_files().length();i++){
                                        if (imageModels.get(position).getVideo_files().getJSONObject(i).getString("quality").equals("sd")){
                                            intent.putExtra("src", imageModels.get(position).getVideo_files().getJSONObject(i).getString("link"));
                                            break;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            intent.putExtra("collection", title);
                            intent.putExtra("type", imageModels.get(position).getType());
                            intent.putExtra("id", imageModels.get(position).getId());

                            Pair<View, String> p1 = Pair.create((View) holder.mImageView, "image");

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, p1);
                            v.getContext().startActivity(intent);
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    Intent intent = new Intent(context, ImageDesc.class);
                    try {
                        if (imageModels.get(position).getType() < 1) {
                            intent.putExtra("src", imageModels.get(position).getSrc().getString("original"));
                        } else {
                            for (int i=0; i<=imageModels.get(position).getVideo_files().length();i++){
                                if (imageModels.get(position).getVideo_files().getJSONObject(i).getString("quality").equals("sd")){
                                    intent.putExtra("src", imageModels.get(position).getVideo_files().getJSONObject(i).getString("link"));
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    intent.putExtra("collection", title);
                    intent.putExtra("type", imageModels.get(position).getType());
                    intent.putExtra("id", imageModels.get(position).getId());

                    Pair<View, String> p1 = Pair.create((View) holder.mImageView, "image");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, p1);
                    v.getContext().startActivity(intent);
                }
            }
        });

        //opening photographer details on click
        holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mInterstitialAd != null) {
                    // Show the ad
                    mInterstitialAd.show((Activity) context);
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.d("TAG", "The ad was dismissed.");
                            //opening url
                            try {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModels.get(position).getPhotographer_url())));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModels.get(position).getPhotographer_url())));
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            mInterstitialAd = null;
                            Log.d("TAG", "The ad was shown.");
                        }
                    });
                } else {
                    //opening url
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModels.get(position).getPhotographer_url())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModels.get(position).getPhotographer_url())));
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView, mPLay;
        private TextView mPhotographer;
        private LinearLayout mLinearLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPLay = itemView.findViewById(R.id.image_video);
            mImageView = itemView.findViewById(R.id.image_main);
            mPhotographer = itemView.findViewById(R.id.tv_photographer);
            mLinearLayout = itemView.findViewById(R.id.linear_main);
        }


        private void settingImage(JSONObject src, String photographer, int type, JSONArray video_pictures, JSONObject user) throws JSONException {

            RequestOptions requestOptions = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.place_holder)
                    .error(R.drawable.place_holder);

            switch (type) {
                case 0:
                    Glide.with(context.getApplicationContext())
                            .load(src.getString("medium"))
                            .apply(requestOptions)
                            .into(mImageView);
                    //credititng photographer
                    mPhotographer.setText(photographer);
                    mImageView.setVisibility(View.VISIBLE);
                    break;
                default:
                    mPLay.setVisibility(View.VISIBLE);
                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(video_pictures.getJSONObject(0).getString("picture") + "?auto=compress&cs=tinysrgb&h=350")
                            .apply(requestOptions)
                            .into(mImageView);
                    //credititng photographer
                    mPhotographer.setText(user.getString("name"));
                    break;
            }
        }
    }
}
