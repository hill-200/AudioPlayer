package com.gwallaz.audioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

public class AlbumActivity extends AppCompatActivity {
    RecyclerView recyclerView2;
    ImageView imageView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        recyclerView2 = findViewById(R.id.album_recyclerview);
        imageView2 = findViewById(R.id.album_activity_image);
        recyclerView2.setLayoutManager(new GridLayoutManager());
        recyclerView2.setAdapter(new AlbumAdapter());
    }
}