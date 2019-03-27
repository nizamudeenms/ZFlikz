package com.zyta.zflikz.model;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.zyta.zflikz.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    private ArrayList<Review> reviewArrayList;
    Context context;

    public ReviewAdapter(Context context, ArrayList<Review> reviewsList) {
        this.reviewArrayList = reviewsList;
        this.context = context;
    }


    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.content_review, parent, false);
        return new ReviewHolder(view, context, reviewArrayList);
    }

    @Override
    public void onBindViewHolder(ReviewHolder holder, int position) {
        System.out.println("position = " + position);
        reviewArrayList.get(position);
        TextView reviewerName = holder.reviewerName;
        TextView reviewerContent = holder.reviewContent;
        reviewerName.setText(reviewArrayList.get(position).getAuthor());
        reviewerContent.setText(reviewArrayList.get(position).getContent());
//        holder.reviewerName.setText(reviewArrayList.get(position).getAuthor());
//        holder.reviewContent.setText(reviewArrayList.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        if (null == reviewArrayList) return 0;
        return reviewArrayList.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView reviewerName;
        public TextView reviewContent;
        FrameLayout reviewFrameLayout;
        ArrayList<Review> reviewArrayList;
        Context mContext;
        int collapsedHeight, expandedHeight;


        public ReviewHolder(View itemView, Context context, ArrayList<Review> reviewArrayList) {
            super(itemView);
            this.reviewArrayList = reviewArrayList;
            this.mContext = context;
            itemView.setOnClickListener(this);

            System.out.println("reviewArrayList" + reviewArrayList.size());
            reviewerName = itemView.findViewById(R.id.review_reviewer_name);
            reviewContent = itemView.findViewById(R.id.review_reviewer_data);
            reviewFrameLayout = itemView.findViewById(R.id.review_frame_layout);
            
//            reviewFrameLayout.setForeground(mContext.getApplicationContext().getResources().getDrawable(R.drawable.gradient_shape));
            reviewContent.post(new Runnable() {
                @Override
                public void run() {
                    collapsedHeight = reviewContent.getMeasuredHeight();
                }
            });
        }


        @Override
        public void onClick(View v) {


            if (reviewContent.getMaxLines() == 7) {
                // expand
                reviewContent.setMaxLines(Integer.MAX_VALUE);
                reviewContent.measure(View.MeasureSpec.makeMeasureSpec(reviewContent.getMeasuredWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED));
                expandedHeight = reviewContent.getMeasuredHeight();
                ObjectAnimator animation = ObjectAnimator.ofInt(reviewContent, "height", collapsedHeight, expandedHeight);
                animation.setDuration(250).start();

            } else {
                // collapse
                ObjectAnimator animation = ObjectAnimator.ofInt(reviewContent, "height", expandedHeight, collapsedHeight);
                animation.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        // number of max lines when collapsed
                        reviewContent.setMaxLines(7);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animation.setDuration(250).start();
//                reviewFrameLayout.setForeground(null);
            }

        }
    }
}

