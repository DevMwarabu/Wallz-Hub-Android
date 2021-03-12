package com.e.wallzhub.Constants.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.e.wallzhub.Constants.Models.Advert;
import com.e.wallzhub.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

/**
 * This project file is owned by DevMwarabu, johnmwarabuchone@gmail.com.
 * Created on 3/11/21. Copyright (c) 2021 DevMwarabu
 */
public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    private Context context;
    private List<Advert> stringUrls;

    public SliderAdapter(Context context, List<Advert> stringUrls) {
        this.context = context;
        this.stringUrls = stringUrls;
    }

    public void renewItems(List<Advert> sliderItems) {
        this.stringUrls = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.stringUrls.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Advert sliderItem) {
        this.stringUrls.add(sliderItem);
        notifyDataSetChanged();
    }

    public void clear() {
        stringUrls.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Advert> stringAds) {
        stringAds.addAll(stringAds);
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slides_layout, parent, false);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        Advert sliderItem = stringUrls.get(position);
        //setting values
        Glide.with(context.getApplicationContext()).load(sliderItem.getStrImageUrl()).placeholder(R.drawable.ic_launcher_foreground)
                .into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return stringUrls.size();
    }

    public class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        private View itemView;
        private ImageView imageViewBackground;

        public SliderAdapterVH(View itemView) {
            super(itemView);

            imageViewBackground = itemView.findViewById(R.id.image_main);
            this.itemView = itemView;
        }
    }
}
