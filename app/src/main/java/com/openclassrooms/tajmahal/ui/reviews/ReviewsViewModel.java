package com.openclassrooms.tajmahal.ui.reviews;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewsViewModel extends ViewModel {

    private final MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    /**
     * Returns the LiveData containing the list of reviews.
     */
    public LiveData<List<Review>> getReviews() {
        return reviewsLiveData;
    }

    /**
     * Returns the LiveData containing error messages.
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Adds a new review to the list after validating the input.
     */
    public void addReview(String username, String picture, String comment, int rate) {
        // Input Validation
        if (username == null || username.isEmpty()) {
            errorLiveData.setValue("Username cannot be empty.");
            return;
        }
        if (comment == null || comment.isEmpty()) {
            errorLiveData.setValue("Comment cannot be empty.");
            return;
        }
        if (rate < 1 || rate > 5) {
            errorLiveData.setValue("Rating must be between 1 and 5.");
            return;
        }

        // Create a new Review object
        Review newReview = new Review(username, picture, comment, rate);

        // Get the current list of reviews
        List<Review> currentReviews = reviewsLiveData.getValue();
        if (currentReviews != null) {
            // Add to the top of the list
            currentReviews.add(0, newReview);
            reviewsLiveData.setValue(currentReviews);
        } else {
            List<Review> newList = new ArrayList<>();
            newList.add(newReview);
            reviewsLiveData.setValue(newList);
        }
    }
}
