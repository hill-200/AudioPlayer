package com.gwallaz.audioplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.albumViewHolder> {
    ArrayList<Model> models;
    Context context;
    albumAdapterInterface adapterInterface;

    public AlbumAdapter(ArrayList<Model> models, Context context,albumAdapterInterface adapterInterface) {
        this.models = models;
        this.context = context;
        this.adapterInterface = adapterInterface;
    }



    @NonNull
    @Override
    public albumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.albumrecyclerviewlayout,parent,false);

        return new AlbumAdapter.albumViewHolder(view,adapterInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull albumViewHolder holder, int position) {
        holder.text2.setText(models.get(position).getAlbum());
        try {
            byte[] image2 = album_art(models.get(position).getPath());
            if(image2 != null){
            Glide.with(context)
                    .asBitmap()
                    .load(image2)
                    .into(holder.image2);
            }else{
                Glide.with(context)
                        .load(R.drawable.music2)
                        .into(holder.image2);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class albumViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        ImageView image2;
        TextView text2;
        albumAdapterInterface adapterInterface;

        public albumViewHolder(@NonNull View itemView, albumAdapterInterface adapterInterface) {
            super(itemView);
            image2 = itemView.findViewById(R.id.album_image);
            text2 = itemView.findViewById(R.id.album_name);
            this.adapterInterface = adapterInterface;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            adapterInterface.onItemClicked(getAdapterPosition());

        }
    }
    private  byte[] album_art(String uri) throws IOException {

        MediaMetadataRetriever retriever2 = new MediaMetadataRetriever();
        retriever2.setDataSource(uri);
        byte[] art = retriever2.getEmbeddedPicture();
        retriever2.release();
        return art;
    }

    public interface albumAdapterInterface{
        public void onItemClicked(int position);
    }
}
