package com.openclassrooms.tajmahal.ui.reviews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.domain.model.Review;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviewsList;

    public void setReviewsList(List<Review> reviews) {
        this.reviewsList = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        if (reviewsList != null && position < reviewsList.size()) {
            Review review = reviewsList.get(position);
            holder.bind(review);
        }
    }

    @Override
    public int getItemCount() {
        return (reviewsList != null) ? reviewsList.size() : 0;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewProfile;
        TextView textViewUsername;
        RatingBar ratingBarReview;
        TextView textViewComment;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProfile = itemView.findViewById(R.id.imageViewProfile);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            ratingBarReview = itemView.findViewById(R.id.ratingBarReview);
            textViewComment = itemView.findViewById(R.id.textViewComment);
        }

        public void bind(Review review) {
            textViewUsername.setText(review.getUsername());
            ratingBarReview.setRating(review.getRate());
            textViewComment.setText(review.getComment());

            // Load profile picture using Picasso (optional)
            if (review.getPicture() != null && !review.getPicture().isEmpty()) {
                Picasso.get()
                        .load(review.getPicture())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(imageViewProfile);
            } else {
                imageViewProfile.setImageResource(R.drawable.ic_profile_placeholder);
            }
        }
    }
}
