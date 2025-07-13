package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.musicplayer.MainActivity.musicFiles;

public class AlbumDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView albumphoto;
    String albumname;
    AlbumDetailsAdapter albumDetailsAdapter ;
    ArrayList<MusicFiles> albumsong = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView = findViewById(R.id.recyclerview);
        albumphoto = findViewById(R.id.album_photo);
        albumname = getIntent().getStringExtra("AlbumName");
        int j = 0;
        for(int i = 0 ; i < musicFiles.size() ; i++)
        {
            if(albumname.equals(musicFiles.get(i).getAlbum()))
            {
                albumsong.add( j  , musicFiles.get(i));
                j++;

            }
        }
        byte[] image = getAlbumArt(albumsong.get(0).getPath());
        if(image != null)
        {
            Glide.with(this)
                    .load(image)
                    .into(albumphoto);
        }
        else
        {
            Glide.with(this)
                    .load(R.drawable.test)
                    .into(albumphoto);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumsong.size() < 1))
        {
            albumDetailsAdapter = new AlbumDetailsAdapter(this , albumsong);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this , RecyclerView.VERTICAL , false));
        }
    }

    private byte[] getAlbumArt(String uri)
    {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}