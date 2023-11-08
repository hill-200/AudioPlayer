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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.myViewHolder>{

    Context context;
    RecyclerViewInterface recyclerViewInterface;

    public RecyclerViewAdapter(Context context, ArrayList<Model> songsList,RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.model = songsList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    ArrayList<Model> model = new ArrayList<>();

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_layout,parent,false);
        return new RecyclerViewAdapter.myViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.textView.setText(model.get(position).getTitle());
        try {
            byte[] image = getAlbumArt(model.get(position).getPath());
            if(image != null) {
                Glide.with(context)
                        .asBitmap()
                        .load(image)
                        .into(holder.imageView);
            }else{
                Glide.with(context)
                        .load(R.drawable.music2)
                        .into(holder.imageView);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private final RecyclerViewInterface recyclerViewInterface;

        TextView textView;
        ImageView imageView;


        public myViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_view);
            imageView = itemView.findViewById(R.id.image_view);
            this.recyclerViewInterface = recyclerViewInterface;
            itemView.setOnClickListener(this);

        }



        @Override
        public void onClick(View v) {
            recyclerViewInterface.onItemClicked(getAdapterPosition());
        }
    }
    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;

    }

    public interface RecyclerViewInterface{
        public void onItemClicked(int position);
    }

}
