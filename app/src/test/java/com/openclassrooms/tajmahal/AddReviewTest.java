package com.openclassrooms.tajmahal;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.openclassrooms.tajmahal.data.repository.RestaurantRepository;
import com.openclassrooms.tajmahal.data.service.RestaurantFakeApi;
import com.openclassrooms.tajmahal.domain.model.Review;
import com.openclassrooms.tajmahal.ui.reviews.ReviewsViewModel;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

public class AddReviewTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private RestaurantFakeApi fakeApi;
    private RestaurantRepository repository;
    private ReviewsViewModel viewModel;

    @Before
    public void setUp() {
        // Use the real fake API (no mocking)
        fakeApi = new RestaurantFakeApi();
        repository = new RestaurantRepository(fakeApi);

        // The real ViewModel (production code) with real repository
        viewModel = new ReviewsViewModel(repository);
    }

    /**
     * Test 1: Valid input should add review successfully.
     */
    @Test
    public void testAddReview_validInputs_shouldAddReview() {
        // GIVEN
        int initialSize = fakeApi.getReviews().size();

        // WHEN
        viewModel.addReview("John Doe", "pic.png", "Loved it!", 5);

        // THEN
        Assert.assertNull("No error should be present", viewModel.getError().getValue());

        List<Review> updatedReviews = fakeApi.getReviews();
        Assert.assertEquals("List size should have increased by 1", initialSize + 1, updatedReviews.size());

        // The new item is inserted at index 0 in RestaurantFakeApi
        Review newReview = updatedReviews.get(0);

        Assert.assertEquals("John Doe", newReview.getUsername());
        Assert.assertEquals("pic.png", newReview.getPicture());
        Assert.assertEquals("Loved it!", newReview.getComment());
        Assert.assertEquals(5, newReview.getRate());
    }

    /**
     * Test 2: Empty comment should trigger an error and NOT add a new review.
     */
    @Test
    public void testAddReview_emptyComment_shouldSetError() {
        // GIVEN
        int initialSize = fakeApi.getReviews().size();

        // WHEN
        // Provide an empty comment
        viewModel.addReview("John Doe", "pic.png", "", 5);

        // THEN
        // Expect an error message
        Assert.assertNotNull("An error should be present", viewModel.getError().getValue());
        Assert.assertEquals("Comment cannot be empty.", viewModel.getError().getValue());

        // No new review should be added to the list
        List<Review> updatedReviews = fakeApi.getReviews();
        Assert.assertEquals("List size should remain the same", initialSize, updatedReviews.size());
    }
}
