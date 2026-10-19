package com.example.monatechickensteak.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.CatalogRepository
import com.example.monatechickensteak.data.SessionManager
import com.example.monatechickensteak.databinding.FragmentHomeBinding
import com.example.monatechickensteak.databinding.ItemPromoBinding
import com.example.monatechickensteak.util.IntentHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/** The dashboard: greeting, quick actions, loyalty points, specials, and business details. */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstName = SessionManager.userName().trim().substringBefore(' ')
        binding.tvGreeting.text = if (firstName.isEmpty()) "Welcome! 👋" else "Hi, $firstName 👋"

        binding.btnOrderNow.setOnClickListener { findNavController().navigate(R.id.menuFragment) }
        binding.btnTrack.setOnClickListener { findNavController().navigate(R.id.ordersFragment) }
        binding.cardPoints.setOnClickListener { findNavController().navigate(R.id.loyaltyFragment) }

        setupBusinessDetails()

        viewLifecycleOwner.lifecycleScope.launch {
            val promotions = CatalogRepository.promotions()
            binding.promoContainer.removeAllViews()
            promotions.forEach { promo ->
                val card = ItemPromoBinding.inflate(layoutInflater, binding.promoContainer, false)
                card.tvEmoji.text = promo.emoji
                card.tvTitle.text = promo.title
                card.tvDescription.text = promo.description
                binding.promoContainer.addView(card.root)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the points every time the screen is shown (they change after orders and rewards)
        viewLifecycleOwner.lifecycleScope.launch {
            val points = AuthRepository.loyaltyPoints(SessionManager.userId())
            binding.tvPoints.text = points.toString()
        }
    }

    private fun setupBusinessDetails() {
        val phone = getString(R.string.business_phone)
        val email = getString(R.string.business_email)
        val address = getString(R.string.business_address).trim()
        val facebook = getString(R.string.business_facebook_url).trim()
        val instagram = getString(R.string.business_instagram_url).trim()

        binding.tvAbout.text = getString(R.string.business_about)
        binding.tvHours.text = getString(R.string.business_hours)

        binding.btnCall.setOnClickListener { open(IntentHelper.dial(requireContext(), phone)) }
        binding.btnEmail.setOnClickListener {
            open(IntentHelper.email(requireContext(), email, "Monate enquiry"))
        }

        // Details that have not been filled in yet stay hidden
        if (address.isNotEmpty()) {
            binding.tvAddress.text = "📍 $address"
            binding.tvAddress.visibility = View.VISIBLE
            binding.btnDirections.visibility = View.VISIBLE
            binding.btnDirections.setOnClickListener { open(IntentHelper.map(requireContext(), address)) }
        }
        if (facebook.isNotEmpty()) {
            binding.btnFacebook.visibility = View.VISIBLE
            binding.btnFacebook.setOnClickListener { open(IntentHelper.web(requireContext(), facebook)) }
        }
        if (instagram.isNotEmpty()) {
            binding.btnInstagram.visibility = View.VISIBLE
            binding.btnInstagram.setOnClickListener { open(IntentHelper.web(requireContext(), instagram)) }
        }
        if (facebook.isNotEmpty() || instagram.isNotEmpty()) {
            binding.socialRow.visibility = View.VISIBLE
        }
    }

    private fun open(opened: Boolean) {
        if (!opened) {
            Snackbar.make(binding.root, "No app found to do that on this phone", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
