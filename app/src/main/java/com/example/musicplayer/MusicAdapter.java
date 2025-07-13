package com.example.musicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder>{
    private Context mcontext;
    static ArrayList<MusicFiles> mfiles;

    MusicAdapter(Context mcontext , ArrayList<MusicFiles> mfiles)
    {
        this.mcontext = mcontext;
        this.mfiles = mfiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.music_items , parent , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.file_name.setText(mfiles.get(position).getTitle());
        byte[] image = getAlbumArt(mfiles.get(position).getPath());
        if(image != null)
        {
            Glide.with(mcontext).asBitmap().load(image).into(holder.album_art);

        }
        else{
            Glide.with(mcontext).load(R.drawable.test).into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mcontext , PlayerActivity.class);
                intent.putExtra("position" , position);
                mcontext.startActivity(intent);
            }
        });
        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu = new PopupMenu(mcontext , view);
                popupMenu.getMenuInflater().inflate(R.menu.popup , popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((menuItem) -> {

                    switch ((menuItem.getItemId()))
                    {
                        case R.id.delete:
                            deleteFile(position , view);
                            break;
                    }
                    return true;
                });
            }
        });
    }
    private void deleteFile(int position , View v)
    {
        Uri contenturi = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI ,
                Long.parseLong(mfiles.get(position).getId()));
        File file = new File(mfiles.get(position).getPath());
        boolean deleted = file.delete();
        if (deleted) {
            mcontext.getContentResolver().delete(contenturi , null , null );
            mfiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mfiles.size());
            Snackbar.make(v, "تم الحذف بنجاح", Snackbar.LENGTH_LONG)
                    .show();
        }
        else {
            Snackbar.make(v, " Can't be Delete the File", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public int getItemCount() {
        return mfiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView file_name;
        ImageView album_art , menu_more;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_image);
            menu_more = itemView.findViewById(R.id.menu_more);
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

    void updateList(ArrayList<MusicFiles> musicFilesArrayList)
    {
        mfiles = new ArrayList<>();
        mfiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }


}
