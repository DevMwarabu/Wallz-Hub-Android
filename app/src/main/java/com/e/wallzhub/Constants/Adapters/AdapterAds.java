package com.e.wallzhub.Constants.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.Fragments.FragmentChild;
import com.e.wallzhub.R;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdapterAds extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Object> imageModels;
    private Context context;
    private static final int ITEM_TYPE_COUNTRY = 0;
    private static final int ITEM_TYPE_BANNER_AD = 1;


    public AdapterAds(ArrayList<Object> imageModels, Context context) {
        this.imageModels = imageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType)
        {
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

        switch (viewType)
        {
            case ITEM_TYPE_BANNER_AD:
                if (imageModels.get(position) instanceof AdView)
                {
                    MyAdViewHolder bannerHolder = (MyAdViewHolder) holder;
                    AdView adView = (AdView) imageModels.get(position);
                    ViewGroup adCardView = (ViewGroup) bannerHolder.itemView;
                    if (adCardView.getChildCount() > 0)
                    {
                        adCardView.removeAllViews();
                    }
                    if (adView.getParent() != null)
                    {
                        ((ViewGroup) adView.getParent()).removeView(adView);
                    }

                    // Add the banner ad to the ad view.
                    adCardView.addView(adView);
                }
                break;

            case ITEM_TYPE_COUNTRY:
            default:
                if (imageModels.get(position) instanceof ImageModel)
                {
                    ViewHolderMain viewHolderMain = (ViewHolderMain) holder;
                    ImageModel imageModel = (ImageModel) imageModels.get(position);
                    //setting data
                    try {
                        viewHolderMain.settingImage(imageModel.getSrc());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (position == 0 || imageModels.get(position) instanceof ImageModel)
        {
            return ITEM_TYPE_COUNTRY;
        } else
        {
            return (position % FragmentChild.ITEMS_PER_AD == 0) ? ITEM_TYPE_BANNER_AD : ITEM_TYPE_COUNTRY;
        }
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public class ViewHolderMain extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        public ViewHolderMain(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_main);
        }

        private void settingImage(JSONObject src) throws JSONException {
            Glide.with(context.getApplicationContext()).load(src.getString("medium")).placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_foreground)
                    .into(mImageView);
        }
    }
    public class MyAdViewHolder extends RecyclerView.ViewHolder
    {
        MyAdViewHolder(View itemView)
        {
            super(itemView);
        }
    }
}
