package com.openclassrooms.tajmahal.ui.reviews;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment that displays a list of reviews and allows users to add new reviews.
 */
@AndroidEntryPoint
public class ReviewsFragment extends Fragment {

    private ReviewsViewModel reviewsViewModel;
    private ReviewsAdapter reviewsAdapter;

    private EditText editTextReview;
    private RatingBar ratingBar;
    private Button buttonSubmit;
    private RecyclerView recyclerViewReviews;
    private TextView textViewEmpty;
    private ImageButton buttonBack;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    public static ReviewsFragment newInstance() {
        return new ReviewsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve the Hilt-injected ViewModel
        reviewsViewModel = new ViewModelProvider(this).get(ReviewsViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);

        editTextReview = view.findViewById(R.id.editTextReview);
        ratingBar = view.findViewById(R.id.ratingBar);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        recyclerViewReviews = view.findViewById(R.id.recyclerViewReviews);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        buttonBack = view.findViewById(R.id.buttonBack);

        reviewsAdapter = new ReviewsAdapter();
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewReviews.setAdapter(reviewsAdapter);

        buttonSubmit.setOnClickListener(v -> submitReview());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerViewReviews.addItemDecoration(dividerItemDecoration);

        // Observe the reviews from the ViewModel
        reviewsViewModel.getReviews().observe(getViewLifecycleOwner(), reviews -> {
            reviewsAdapter.setReviewsList(reviews);
            recyclerViewReviews.scrollToPosition(0);

            if (reviews.isEmpty()) {
                textViewEmpty.setVisibility(View.VISIBLE);
            } else {
                textViewEmpty.setVisibility(View.GONE);
            }
        });

        // Observe errors
        reviewsViewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Back button logic
        buttonBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    /**
     * Handles the submission of a new review.
     */
    private void submitReview() {
        String reviewText = editTextReview.getText().toString().trim();
        int rating = (int) ratingBar.getRating();
        String username = getCurrentUsername();
        String picture = getCurrentUserProfilePicture();

        if (TextUtils.isEmpty(reviewText)) {
            Toast.makeText(getContext(), "Please enter a review.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rating == 0) {
            Toast.makeText(getContext(), "Please provide a rating.", Toast.LENGTH_SHORT).show();
            return;
        }

        reviewsViewModel.addReview(username, picture, reviewText, rating);

        editTextReview.setText("");
        ratingBar.setRating(0);
    }

    private String getCurrentUsername() {
        // Placeholder
        return "CurrentUser";
    }

    private String getCurrentUserProfilePicture() {
        // Placeholder
        return "";
    }
}
