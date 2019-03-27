package com.zyta.zflikz.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.zyta.zflikz.R;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    ArrayList<String> videoList;
    Context context;


    public VideoAdapter(Context context, ArrayList<String> youtubeVideos) {
        this.context = context;
        this.videoList = youtubeVideos;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        holder.videoWebView.loadData(videoList.get(position),"text/html","utf-8" );
    }


    @Override
    public int getItemCount() {
        return videoList.size();
    }


    public class VideoViewHolder extends RecyclerView.ViewHolder {
        WebView videoWebView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoWebView = (WebView) itemView.findViewById(R.id.webview_video);
            videoWebView.getSettings().setJavaScriptEnabled(true);
            videoWebView.setWebChromeClient(new WebChromeClient(){

            });

        }
    }
}
