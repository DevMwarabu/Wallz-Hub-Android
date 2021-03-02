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
import com.e.wallzhub.R;

import org.json.JSONObject;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<ImageModel> imageModels;
    private Context context;

    public Adapter(List<ImageModel> imageModels, Context context) {
        this.imageModels = imageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_main, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_main);
        }

        private void settingImage(JSONObject src){
//            Glide.with(context.getApplicationContext()).load(imageUrl).placeholder(R.drawable.fdd)
//                    .error(R.drawable.fdd)
//                    .into(mImageView);
        }
    }
}
