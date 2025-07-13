package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class  MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //implements SearchView.OnQueryTextListener
    public static final int REQUEST_CODE = 1 ;
    static ArrayList<MusicFiles> musicFiles;
    static boolean shuffleboolean = false ;
    static boolean repeatboolean = false ;
    static ArrayList<MusicFiles> albums = new ArrayList<>();
    private String My_SORT_PREF = "SortOrder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();
    }

    private void permission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext() , Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
            , REQUEST_CODE);
        }
        else
        {
            musicFiles = getAllAudio(this);
            intiviewpager();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
               musicFiles = getAllAudio(this);
                intiviewpager();
            }
            else
            {
                ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_CODE);
            }
        }
    }
    private void intiviewpager() {
        ViewPager viewPager = findViewById(R.id.viewpager);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        Viewpageradpter viewpageradpter = new Viewpageradpter(getSupportFragmentManager());
        viewpageradpter.addFragment(new SongsFragment() , "Music");
        viewpageradpter.addFragment(new AlbumFragment() , "Albums");
        viewPager.setAdapter(viewpageradpter);
        tabLayout.setupWithViewPager(viewPager);

    }


    public static class Viewpageradpter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles ;
        public Viewpageradpter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragment(Fragment fragment , String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);

        }
    }
    public ArrayList<MusicFiles> getAllAudio(Context context)
    {
        SharedPreferences Preferences = getSharedPreferences(My_SORT_PREF , MODE_PRIVATE);
        String SortOrder = Preferences.getString("storing" , "sortByName");

        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        String order =null;
        
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        switch (SortOrder)
        {
            case "sortByName":
                order = MediaStore.Audio.AudioColumns.DISPLAY_NAME + "," + MediaStore.Audio.AudioColumns.TRACK ;
                break;

            case "sortByDate":
                order = MediaStore.Audio.AudioColumns.DATE_ADDED ;// artist_id *
                break;
            case "sortBySize":
                order = MediaStore.Audio.AudioColumns.SIZE  + "," + MediaStore.Audio.AudioColumns.TRACK;
                break;
        }



        String [] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID };

        //Toast.makeText(context, "***" + order + "***", Toast.LENGTH_SHORT).show();
        //Log.e("main activity" , order);
        Cursor cursor = context.getContentResolver().query(uri , projection , null , null , order);
        if(cursor != null)
        {
            while(cursor.moveToNext())
            {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFiles musicFiles = new MusicFiles(path , title , artist , album , duration , id);
                tempAudioList.add(musicFiles);
                if(!duplicate.contains(album))
                {
                    albums.add(musicFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempAudioList;
    }
// from hir for search bar ********

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search , menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView =(SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }
    // this code for search bar but does'n work beacuse the arabic songs :(
    // Note go to the (on create option menu method to try to fixed ) *****
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userinput = newText.toLowerCase();
        ArrayList<MusicFiles> myfiles = new ArrayList<>();
        for(MusicFiles song: musicFiles)
        {
            if(song.getTitle().toLowerCase().contains(userinput))
            {
                myfiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myfiles);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(My_SORT_PREF , MODE_PRIVATE).edit();
        switch (item.getItemId())
        {
            case R.id.by_name:
                editor.putString("storing" , "sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_date:
                editor.putString("storing" , "sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.by_size:
                editor.putString("storing" , "sortBySize");
                editor.apply();
                this.recreate();
                break;
        }

        return super.onOptionsItemSelected(item);

    }


}