package com.openclassrooms.tajmahal.ui.reviews;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.tajmahal.R;

/**
 * A fragment that displays a list of reviews and allows users to add new reviews.
 */
public class ReviewsFragment extends Fragment {

    private ReviewsViewModel reviewsViewModel;
    private ReviewsAdapter reviewsAdapter;

    private EditText editTextReview;
    private RatingBar ratingBar;
    private Button buttonSubmit;
    private RecyclerView recyclerViewReviews;
    private TextView textViewEmpty;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance(String param1, String param2) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        // Add parameters to bundle if needed
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        reviewsViewModel = new ViewModelProvider(this).get(ReviewsViewModel.class);
        // If you have arguments to process, handle them here
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        // Initialize UI components
        editTextReview = view.findViewById(R.id.editTextReview);
        ratingBar = view.findViewById(R.id.ratingBar);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);
        textViewEmpty = view.findViewById(R.id.textViewEmpty); // Make sure this matches the new TextView ID

        // Set up RecyclerView
        reviewsAdapter = new ReviewsAdapter();
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReviews.setAdapter(reviewsAdapter);

        // Set up Submit button click listener
        buttonSubmit.setOnClickListener(v -> submitReview());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Add the divider for each list item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.HORIZONTAL
        );
        recyclerViewReviews.addItemDecoration(dividerItemDecoration);

        // 2) Observe reviews LiveData
        reviewsViewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
            // Update RecyclerView
            reviewsAdapter.setReviewsList(reviews);
            // Scroll to top when a new review is added
            recyclerViewReviews.scrollToPosition(0);

            // Handle Empty State
            if (reviews.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
            }
        });

        // 3) Observe error LiveData
        reviewsViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Handles the submission of a new review.
     */
    private void submitReview() {
        String reviewText = editTextReview.getText().toString().trim();
        int rating = (int) ratingBar.getRating();
        String username = getCurrentUsername(); // Implement or replace with actual user name logic
        String picture = getCurrentUserProfilePicture(); // Implement if needed

        if (TextUtils.isEmpty(reviewText)) {
            Toast.makeText(getContext(), "Please enter a review.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(getContext(), "Please provide a rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add the review via ViewModel
        reviewsViewModel.addReview(username, picture, reviewText, rating);

        // Clear input fields
        editTextReview.setText("");
        ratingBar.setRating(0);
    }

    /**
     * Retrieves the current user's username.
     * TODO: Replace with real logic if needed.
     */
    private String getCurrentUsername() {
        return "CurrentUser"; // Placeholder
    }

    /**
     * Retrieves the current user's profile picture URL or path.
     * TODO: Replace with real logic if needed.
     */
    private String getCurrentUserProfilePicture() {
        return ""; // Placeholder (empty string means no picture)
    }
}
