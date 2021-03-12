package com.e.wallzhub.Constants.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.e.wallzhub.Constants.Models.AboutModel;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.ImageDesc;
import com.e.wallzhub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    private List<AboutModel> imageModels;
    private Context context;

    public Adapter(List<AboutModel> imageModels, Context context) {
        this.imageModels = imageModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_about, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    public void clear() {
        imageModels.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<ImageModel> imageModels) {
        imageModels.addAll(imageModels);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.settingImage(imageModels.get(position).getTitle(),imageModels.get(position).getBody());
    }

    @Override
    public int getItemCount() {
        return imageModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle,mBody;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.tv_title);
            mBody = itemView.findViewById(R.id.tv_body);
        }

        private void settingImage(String title,String body) {
            mTitle.setText(title);
            mBody.setText(body);
        }
    }
}
