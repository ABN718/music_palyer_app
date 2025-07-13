package com.example.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {
    View view;
    private Context mcontext;
    ArrayList<MusicFiles> albummfiles;

    public AlbumAdapter(Context mcontext, ArrayList<MusicFiles> albummfiles) {
        this.mcontext = mcontext;
        this.albummfiles = albummfiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view  = LayoutInflater.from(mcontext).inflate(R.layout.album_item , parent , false);
        return new MyHolder(view
        );
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.album_name.setText(albummfiles.get(position).getAlbum());
        byte[] image = getAlbumArt(albummfiles.get(position).getPath());
        if(image != null)
        {
            Glide.with(mcontext).asBitmap().load(image).into(holder.album_image);

        }
        else{
            Glide.with(mcontext).load(R.drawable.test).into(holder.album_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext , AlbumDetails.class);
                intent.putExtra("AlbumName" , albummfiles.get(position).getAlbum());
                mcontext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return albummfiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image = view.findViewById(R.id.album_image);
            album_name = view.findViewById(R.id.album_name);

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
