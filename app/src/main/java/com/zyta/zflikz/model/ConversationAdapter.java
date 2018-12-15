package com.zyta.zflikz.model;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.zyta.zflikz.FullscreenImageActivity;
import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.R;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConvViewHolder> {
    List<ConversationMessage> conversationMessages;
    Context cContext;


    public ConversationAdapter(Context context, List<ConversationMessage> conversationMessages) {
        cContext = context;
        this.conversationMessages = conversationMessages;
    }


    @NonNull
    @Override
    public ConvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        cContext = parent.getContext();
        View itemView = LayoutInflater.from(cContext).inflate(R.layout.content_converse, parent, false);

        return new ConvViewHolder(itemView, cContext, conversationMessages);
    }

    @Override
    public void onBindViewHolder(@NonNull ConvViewHolder holder, int position) {
        conversationMessages.get(position);
        ImageView shareImage = holder.shareImage;
        ImageView postImage = holder.postPhotoImageView;
        TextView postText = holder.postMessageTextView;
        ImageView profileImage = holder.authorPPImageview;
        TextView profileName = holder.authorNameTextView;
        TextView profilePostTime = holder.authorPostdateTextView;

//        System.out.println("Uri sis _________"+mProfileImage);


        if (conversationMessages.get(position).getPostImageUrl() != null) {
            GlideApp.with(cContext).load(conversationMessages.get(position).getPostImageUrl()).centerCrop().error(R.drawable.no_image_available).into(postImage);
            postImage.setVisibility(View.VISIBLE);
        }else {
            postImage.setVisibility(View.GONE);
        }
        Glide.with(cContext).load(conversationMessages.get(position).getPostAuthorImageUrl()).apply((RequestOptions.bitmapTransform(new CircleCrop())).placeholder(R.drawable.zlikx_logo)).into(profileImage);
        postText.setText(conversationMessages.get(position).getPostMessage());
        profileName.setText(conversationMessages.get(position).getPostAuthor());
        profilePostTime.setText(conversationMessages.get(position).getPostDate());


        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cContext, FullscreenImageActivity.class);
                intent.putExtra("post_image_path", conversationMessages.get(position).getPostImageUrl());
                cContext.startActivity(intent);

            }
        });

        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Zlikx");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey check out this conversation at: https://www.zlikz.com\n");
                cContext.startActivity(Intent.createChooser(intent, "choose one"));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (null == conversationMessages) return 0;
        return conversationMessages.size();
    }


    public class ConvViewHolder extends RecyclerView.ViewHolder {
        TextView postMessageTextView;
        ImageView postPhotoImageView, authorPPImageview, shareImage;
        TextView authorNameTextView, authorPostdateTextView;
        Context cContext;
        List<ConversationMessage> conversationMessages;

        public ConvViewHolder(View itemView, Context cContext, List<ConversationMessage> conversationMessages) {
            super(itemView);
            this.cContext = cContext;
            this.conversationMessages = conversationMessages;
            postMessageTextView = itemView.findViewById(R.id.con_post_text_view_msg);
            postPhotoImageView = itemView.findViewById(R.id.con_post_image_view_pic);
            authorPPImageview = itemView.findViewById(R.id.con_profile_image_view);
            authorNameTextView = itemView.findViewById(R.id.con_profile_text_view_name);
            authorPostdateTextView = itemView.findViewById(R.id.con_profile_text_view_date);
            shareImage = itemView.findViewById(R.id.post_share_icon_view);
        }
    }
}
