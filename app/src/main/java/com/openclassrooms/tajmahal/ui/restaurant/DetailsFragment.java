package com.openclassrooms.tajmahal.ui.restaurant;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.openclassrooms.tajmahal.R;
import com.openclassrooms.tajmahal.databinding.FragmentDetailsBinding;
import com.openclassrooms.tajmahal.domain.model.Restaurant;
import com.openclassrooms.tajmahal.domain.model.Review;
import com.openclassrooms.tajmahal.ui.reviews.ReviewsFragment;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * DetailsFragment is the entry point of the application and serves as the primary UI.
 * It displays details about a restaurant and provides functionality to open its location
 * in a map, call its phone number, or view its website, and also to open the reviews screen.
 */
@AndroidEntryPoint
public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;
    private DetailsViewModel detailsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     */
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called immediately after onCreateView().
     * Perform final setup (listeners, observers, etc.) once the views are created.
     */
    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {
        super.onViewCreated(view, savedInstanceState);

        setupUI();
        setupViewModel();

        // Observe restaurant data
        detailsViewModel.getTajMahalRestaurant().observe(
                getViewLifecycleOwner(),
                this::updateUIWithRestaurant
        );

        // Observe the list of reviews
        detailsViewModel.getReviews().observe(
                getViewLifecycleOwner(),
                this::updateUIWithReviews
        );

        // When user taps "Laisser un avis", open the ReviewsFragment
        binding.review2Count.setOnClickListener(v -> {
            openReviewsFragment();
        });
    }

    /**
     * Sets up the UI-specific properties, such as system UI flags and status bar color.
     */
    private void setupUI() {
        Window window = requireActivity().getWindow();
        window.getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * Initializes the ViewModel for this Fragment.
     */
    private void setupViewModel() {
        detailsViewModel = new ViewModelProvider(this).get(DetailsViewModel.class);
    }

    /**
     * Opens the ReviewsFragment via a manual FragmentTransaction.
     */
    private void openReviewsFragment() {
        ReviewsFragment reviewsFragment = new ReviewsFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, reviewsFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Updates the UI components with the provided restaurant data.
     */
    private void updateUIWithRestaurant(Restaurant restaurant) {
        if (restaurant == null) return;

        Log.d("DetailsFragment", "updateUIWithRestaurant: " + restaurant.getName());
        // Basic info
        binding.tvRestaurantName.setText(restaurant.getName());
        binding.tvRestaurantDay.setText(detailsViewModel.getCurrentDay(requireContext()));
        binding.tvRestaurantType.setText(
                String.format("%s %s", getString(R.string.restaurant), restaurant.getType())
        );
        binding.tvRestaurantHours.setText(restaurant.getHours());
        binding.tvRestaurantAddress.setText(restaurant.getAddress());
        binding.tvRestaurantWebsite.setText(restaurant.getWebsite());
        binding.tvRestaurantPhoneNumber.setText(restaurant.getPhoneNumber());

        // Visibility for chips
        binding.chipOnPremise.setVisibility(restaurant.isDineIn() ? View.VISIBLE : View.GONE);
        binding.chipTakeAway.setVisibility(restaurant.isTakeAway() ? View.VISIBLE : View.GONE);

        // Buttons / ImageViews that open maps, phone dialer, browser
        binding.buttonAdress.setOnClickListener(v -> openMap(restaurant.getAddress()));
        binding.buttonPhone.setOnClickListener(v -> dialPhoneNumber(restaurant.getPhoneNumber()));
        binding.buttonWebsite.setOnClickListener(v -> openBrowser(restaurant.getWebsite()));
    }

    /**
     * Updates the UI based on the list of reviews:
     *  - Average rating
     *  - Total count
     *  - Distribution among 1- to 5-star
     */
    private void updateUIWithReviews(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            // You might handle empty reviews differently (e.g. hide rating, show a message, etc.)
            binding.ratingText.setText("0.0");
            binding.ratingBar.setRating(0f);
            binding.reviewCount.setText("(0)");
            // Reset bars to 0 if you wish:
            binding.progressBar1.setProgress(0);
            binding.progressBar2.setProgress(0);
            binding.progressBar3.setProgress(0);
            binding.progressBar4.setProgress(0);
            binding.progressBar5.setProgress(0);
            return;
        }

        // 1) Calculate average rating
        float sum = 0f;
        for (Review r : reviews) {
            sum += r.getRate();
        }
        float averageRating = sum / reviews.size();

        // 2) Display average rating
        binding.ratingText.setText(String.format("%.1f", averageRating));
        binding.ratingBar.setRating(averageRating);

        // 3) Display total number of reviews
        binding.reviewCount.setText(String.format("(%d)", reviews.size()));

        // 4) Calculate distribution of each star rating
        //    starCounts[0] => number of 5-star reviews
        //    starCounts[1] => number of 4-star reviews
        //    starCounts[2] => number of 3-star reviews
        //    starCounts[3] => number of 2-star reviews
        //    starCounts[4] => number of 1-star reviews
        int[] starCounts = new int[5];
        for (Review r : reviews) {
            int rating = r.getRate(); // e.g. 1 to 5
            if (rating >= 1 && rating <= 5) {
                // Convert rating -> index (5-star = index 0, 4-star = index 1, etc.)
                starCounts[5 - rating]++;
            }
        }

        // 5) Set the progress for each bar as a percentage of total
        //    progressBar1 => 5-star
        //    progressBar2 => 4-star
        //    progressBar3 => 3-star
        //    progressBar4 => 2-star
        //    progressBar5 => 1-star
        binding.progressBar1.setProgress((int) (starCounts[0] * 100f / reviews.size()));
        binding.progressBar2.setProgress((int) (starCounts[1] * 100f / reviews.size()));
        binding.progressBar3.setProgress((int) (starCounts[2] * 100f / reviews.size()));
        binding.progressBar4.setProgress((int) (starCounts[3] * 100f / reviews.size()));
        binding.progressBar5.setProgress((int) (starCounts[4] * 100f / reviews.size()));
    }

    /**
     * Opens the provided address in Google Maps or shows an error if not available.
     */
    private void openMap(String address) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireActivity(), R.string.maps_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Dials the provided phone number or shows an error if no dialer is available.
     */
    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireActivity(), R.string.phone_not_found, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens the provided website URL in a browser or shows an error if no browser is available.
     */
    private void openBrowser(String websiteUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteUrl));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(requireActivity(), R.string.no_browser_found, Toast.LENGTH_SHORT).show();
        }
    }

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }
}
