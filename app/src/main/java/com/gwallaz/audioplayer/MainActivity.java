package com.gwallaz.audioplayer;

import static com.gwallaz.audioplayer.R.id;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
     TabLayout tabLayout;
     ViewPager2 viewPager2;
     static ArrayList<Model> model = new ArrayList<>();
     static boolean shuffle = false;
     static boolean repeat = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();
        String[] projection =  {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
        };



        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,null,null,null ,null);

        if(cursor != null){
            while(cursor.moveToNext()){
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String data = cursor.getString(3);
                String artist = cursor.getString(4);

                Model model2 = new Model(title,data,artist,album,duration);
                model.add(model2);
            }
                cursor.close();
        }
        initViewPager();

    }

    public void initViewPager(){

        tabLayout = findViewById(id.tab_layout);
        viewPager2 = findViewById(id.view_pager2);
        ViewpagerAdapter  viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager(), getLifecycle());
        viewpagerAdapter.addFragments(new SongsFragment(),"Songs");
        viewpagerAdapter.addFragments(new AlbumFragment(),"Albums");
        viewPager2.setAdapter(viewpagerAdapter);
        new TabLayoutMediator(tabLayout,viewPager2,(tab, position) -> {
            for (String title : viewpagerAdapter.titles) {
                if(position == 0){
                    tab.setText("Songs");
                }
                else{
                    tab.setText("Albums");
                }

            }

        })
        .attach();



    }

     public static class ViewpagerAdapter extends FragmentStateAdapter {

         ArrayList<Fragment> fragments;
         ArrayList<String> titles;

         public ViewpagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
             super(fragmentManager, lifecycle);
             this.fragments = new ArrayList<>();
             this.titles = new ArrayList<>();
         }

         void addFragments(Fragment fragment, String title) {
             fragments.add(fragment);
             titles.add(title);

         }

         @NonNull
         @Override
         public Fragment createFragment(int position) {
             return fragments.get(position);
         }

         @Override
         public int getItemCount() {
             return fragments.size();
         }

     }
     private final int STORAGE_PERMISSION_CODE = 1;

     private void permission(){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);

        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
     }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission successfully granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
}