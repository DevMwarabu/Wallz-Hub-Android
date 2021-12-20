package com.e.wallzhub.Constants.Adapters;

import static com.facebook.FacebookSdk.getApplicationContext;

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

public class AdapterAds extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Object> imageModels;
    private Context context;
    private String title;
    private static final int ITEM_TYPE_COUNTRY = 0;
    private static final int ITEM_TYPE_BANNER_AD = 1;
    private InterstitialAd mInterstitialAd;

    public AdapterAds(ArrayList<Object> imageModels, Context context, String title,InterstitialAd mInterstitialAd) {
        this.imageModels = imageModels;
        this.context = context;
        this.title = title;
        this.mInterstitialAd = mInterstitialAd;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case ITEM_TYPE_BANNER_AD:
                //Inflate ad banner container
                View bannerLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_ad_row, parent, false);

                //Create View Holder
                MyAdViewHolder myAdViewHolder = new MyAdViewHolder(bannerLayoutView);

                return myAdViewHolder;
            case ITEM_TYPE_COUNTRY:
            default:

                //Inflate RecyclerView row
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_main, parent, false);

                //Create View Holder
                final ViewHolderMain viewHolderMain = new ViewHolderMain(view);

                return viewHolderMain;
        }
    }

    public void clear() {
        imageModels.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ImageModel> imageModels) {
        imageModels.addAll(imageModels);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);

        switch (viewType) {
            case ITEM_TYPE_BANNER_AD:
                if (imageModels.get(position) instanceof AdView) {
                    MyAdViewHolder bannerHolder = (MyAdViewHolder) holder;
                    AdView adView = (AdView) imageModels.get(position);
                    ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                    if (adCardView.getChildCount() > 0) {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null) {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView);
                }
                break;

            case ITEM_TYPE_COUNTRY:
            default:
                if (imageModels.get(position) instanceof ImageModel) {
                    ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
                    ImageModel imageModel = (ImageModel) imageModels.get(position);
                    //setting data
                    try {
                        viewHolderMain.settingImage(imageModel.getSrc(), imageModel.getPhotographer(), imageModel.getType(), imageModel.getVideo_pictures(), imageModel.getUser());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    viewHolderMain.mImageView.setOnClickListener(new View.OnClickListener() {
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
                                            if (imageModel.getType() < 1) {
                                                intent.putExtra("src", imageModel.getSrc().getString("original"));
                                            } else {
                                                for (int i=0; i<=imageModel.getVideo_files().length();i++){
                                                    if (imageModel.getVideo_files().getJSONObject(i).getString("quality").equals("sd")){
                                                        intent.putExtra("src", imageModel.getVideo_files().getJSONObject(i).getString("link"));
                                                        break;
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        intent.putExtra("collection", title);
                                        intent.putExtra("type", imageModel.getType());
                                        intent.putExtra("id", imageModel.getId());

                                        Pair<View, String> p1 = Pair.create((View) viewHolderMain.mImageView, "image");

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
                                    if (imageModel.getType() < 1) {
                                        intent.putExtra("src", imageModel.getSrc().getString("original"));
                                    } else {
                                        for (int i=0; i<=imageModel.getVideo_files().length();i++){
                                            if (imageModel.getVideo_files().getJSONObject(i).getString("quality").equals("sd")){
                                                intent.putExtra("src", imageModel.getVideo_files().getJSONObject(i).getString("link"));
                                                break;
                                            }
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                intent.putExtra("collection", title);
                                intent.putExtra("type", imageModel.getType());
                                intent.putExtra("id", imageModel.getId());

                                Pair<View, String> p1 = Pair.create((View) viewHolderMain.mImageView, "image");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, p1);
                                v.getContext().startActivity(intent);
                            }
                        }
                    });

                    //opening photographer details on click
                    viewHolderMain.mLinearLayout.setOnClickListener(new View.OnClickListener() {
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
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModel.getPhotographer_url())));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModel.getPhotographer_url())));
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
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModel.getPhotographer_url())));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(imageModel.getPhotographer_url())));
                                }
                            }
                        }
                    });
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || imageModels.get(position) instanceof ImageModel) {
            return ITEM_TYPE_COUNTRY;
        } else {
            return (position % FragmentChild.ITEMS_PER_AD == 0) ? ITEM_TYPE_BANNER_AD : ITEM_TYPE_COUNTRY;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolderMain extends RecyclerView.ViewHolder {
        private ImageView mImageView, mPLay;
        private TextView mPhotographer;
        private VideoView mVideoView;
        private LinearLayout mLinearLayout;

        public ViewHolderMain(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_main);
            mPhotographer = itemView.findViewById(R.id.tv_photographer);
            mLinearLayout = itemView.findViewById(R.id.linear_main);
            mPLay = itemView.findViewById(R.id.image_video);
            mVideoView = itemView.findViewById(R.id.video_main);
        }

        private void settingImage(JSONObject src, String photographer, int type, JSONArray video_pictures, JSONObject user) throws JSONException {

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
                    .override(150, 200);

            switch (type) {
                case 0:
                    Glide.with(context.getApplicationContext())
                            .asBitmap()
                            .load(src.getString("medium"))
                            .apply(requestOptions)
                            .dontAnimate()
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mImageView.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
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
                            .dontAnimate()
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mImageView.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });
                    //credititng photographer
                    mPhotographer.setText(user.getString("name"));
                    break;
            }
        }
    }

    public class MyAdViewHolder extends RecyclerView.ViewHolder {
        MyAdViewHolder(View itemView) {
            super(itemView);
        }
    }
}
