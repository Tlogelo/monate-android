package com.example.monatechickensteak.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.CartManager
import com.example.monatechickensteak.data.CatalogRepository
import com.example.monatechickensteak.data.ReviewRepository
import com.example.monatechickensteak.databinding.FragmentItemDetailBinding
import com.example.monatechickensteak.databinding.ItemReviewBinding
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.util.toRand
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
import kotlinx.coroutines.launch

/** Details of one menu item: description, price, quantity, add to cart and its reviews. */
class ItemDetailFragment : Fragment() {

    companion object {
        const val ARG_ITEM_ID = "itemId"
        private const val MAX_QUANTITY = 20
    }

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!

    private var food: FoodItem? = null
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantity()
            }
        }
        binding.btnPlus.setOnClickListener {
            if (quantity < MAX_QUANTITY) {
                quantity++
                updateQuantity()
            }
        }
        binding.btnAddToCart.setOnClickListener { addToCart() }

        val itemId = arguments?.getInt(ARG_ITEM_ID, -1) ?: -1
        viewLifecycleOwner.lifecycleScope.launch {
            val item = CatalogRepository.menuItem(itemId)
            if (item == null) {
                findNavController().popBackStack()
                return@launch
            }
            food = item
            binding.tvEmoji.text = item.emoji
            binding.tvCategory.text = item.category
            binding.tvName.text = item.name
            binding.tvPrice.text = item.price.toRand()
            binding.tvDescription.text = item.description
            updateQuantity()
            showReviews(item)
        }
    }

    private fun updateQuantity() {
        binding.tvQty.text = quantity.toString()
        val item = food ?: return
        binding.btnAddToCart.text = "Add $quantity to cart  •  ${(item.price * quantity).toRand()}"
    }

    private fun addToCart() {
        val item = food ?: return
        CartManager.add(item, quantity)
        Snackbar.make(binding.root, "$quantity × ${item.name} added to cart", Snackbar.LENGTH_SHORT)
            .setAction("View cart") { findNavController().navigate(R.id.cartFragment) }
            .show()
        quantity = 1
        updateQuantity()
    }

    private suspend fun showReviews(item: FoodItem) {
        val reviews = ReviewRepository.all().filter { it.itemName == item.name }
        binding.tvRating.text = if (reviews.isEmpty()) {
            "No reviews yet"
        } else {
            String.format(
                Locale.US, "%.1f ★  (%d reviews)",
                ReviewRepository.averageRating(reviews), reviews.size
            )
        }

        binding.reviewsContainer.removeAllViews()
        if (reviews.isEmpty()) {
            val empty = TextView(requireContext())
            empty.text = "Be the first to review this item from Profile > Reviews & ratings."
            empty.setTextColor(ContextCompat.getColor(requireContext(), R.color.monate_grey))
            binding.reviewsContainer.addView(empty)
            return
        }
        reviews.take(5).forEach { review ->
            val row = ItemReviewBinding.inflate(layoutInflater, binding.reviewsContainer, false)
            row.tvItem.text = review.itemName
            row.tvStars.text = "★".repeat(review.rating) + "☆".repeat(5 - review.rating)
            row.tvComment.text = review.comment
            row.tvMeta.text = "${review.author}  •  ${review.date}"
            binding.reviewsContainer.addView(row.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
