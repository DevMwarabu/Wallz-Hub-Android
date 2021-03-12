package com.e.wallzhub.Constants.Adapters;

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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.e.wallzhub.Constants.Models.ImageModel;
import com.e.wallzhub.ImageDesc;
import com.e.wallzhub.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdapterDEsc extends RecyclerView.Adapter<AdapterDEsc.ViewHolder> {
    private List<ImageModel> imageModels;
    private Context context;
    private String collection;


    public AdapterDEsc(List<ImageModel> imageModels, Context context, String collection) {
        this.imageModels = imageModels;
        this.context = context;
        this.collection = collection;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_desc, parent, false);
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
        try {
            holder.settingImage(imageModels.get(position).getSrc());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImageDesc.class);
                intent.putExtra("src", imageModels.get(position).getSrc().toString());
                intent.putExtra("collection", collection);

                Pair<View, String> p1 = Pair.create((View) holder.mImageView, "image");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, p1);
                v.getContext().startActivity(intent, options.toBundle());
                //finishing activity
                ((Activity) context).finish();

            }
        });

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

        private void settingImage(JSONObject src) throws JSONException {
            Glide.with(context.getApplicationContext()).load(src.getString("medium"))
                    .apply(new RequestOptions()
                            .fitCenter()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .override(Target.SIZE_ORIGINAL))
                    .placeholder(R.mipmap.ic_launcher_foreground)
                    .error(R.mipmap.ic_launcher_foreground)
                    .into(mImageView);
        }
    }
}
