package com.openclassrooms.tajmahal.ui.reviews;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.domain.model.Review;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel that holds a list of reviews and handles adding new ones.
 */
@HiltViewModel // <-- Required so Hilt knows how to create this via @Inject constructor
public class ReviewsViewModel extends ViewModel {

    private final RestaurantRepository restaurantRepository;

    private final MutableLiveData<List<Review>> reviewsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    // Hilt-injected constructor (single argument).
    @Inject
    public ReviewsViewModel(RestaurantRepository repository) {
        this.restaurantRepository = repository;

        // Fetch the initial list of reviews from the repository
        repository.getReviews().observeForever(reviews -> {
            // The LiveData from the repository might be posted once,
            // so we just set it to our local LiveData
            reviewsLiveData.setValue(reviews);
        });
    }

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
        // Basic input validation
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

        // 1) Add it to the fake API via repository
        restaurantRepository.addReview(newReview);

        // 2) Fetch the updated list
        restaurantRepository.getReviews().observeForever(updatedReviews ->
                reviewsLiveData.setValue(updatedReviews)
        );
    }
}
