package com.example.monatechickensteak.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.CatalogRepository
import com.example.monatechickensteak.data.SessionManager
import com.example.monatechickensteak.data.SupportRepository
import com.example.monatechickensteak.databinding.FragmentSupportBinding
import com.example.monatechickensteak.databinding.ItemFaqBinding
import com.example.monatechickensteak.util.IntentHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/** Help system: quick how-to guides, FAQs (from the database), contact shortcuts and a message form. */
class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    /** Short step-by-step guides. The full manual is docs/USER_GUIDE.md. */
    private val guides = listOf(
        "How to place an order" to
                "1. Open the Menu tab and tap an item to see its details.\n" +
                "2. Tap Add (or choose a quantity and tap Add to cart).\n" +
                "3. Open the Cart tab, pick a collection time and a payment method.\n" +
                "4. Tap Place order. You earn loyalty points straight away.",
        "How to track an order" to
                "Open the Orders tab. Your newest order is at the top and its progress bar moves from Received to Preparing, Ready and Delivered. Turn on notifications in Profile > Settings to get a message on every change.",
        "How to log in with Google" to
                "On the login screen tap Continue with Google and choose your account. The first time, an account is created for you automatically.",
        "How to use my loyalty points" to
                "Go to Profile > Loyalty & rewards, choose a reward you have enough points for and tap Redeem. Show the code at the till.",
        "How to change my settings" to
                "Go to Profile > Settings. You can switch order notifications on or off and choose your default collection time and payment method, which are filled in for you at checkout."
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        guides.forEach { (title, body) -> addExpandable(binding.guideContainer, title, body) }

        viewLifecycleOwner.lifecycleScope.launch {
            CatalogRepository.faqs().forEach { faq ->
                addExpandable(binding.faqContainer, faq.question, faq.answer)
            }
        }

        binding.btnCall.setOnClickListener {
            open(IntentHelper.dial(requireContext(), getString(R.string.business_phone)))
        }
        binding.btnEmail.setOnClickListener {
            open(IntentHelper.email(requireContext(), getString(R.string.business_email), "Monate app support"))
        }

        binding.etMessage.doAfterTextChanged { binding.tilMessage.error = null }
        binding.btnSend.setOnClickListener { sendMessage() }
    }

    /** A card showing [title]; tap it to open or close [body]. */
    private fun addExpandable(container: ViewGroup, title: String, body: String) {
        val row = ItemFaqBinding.inflate(layoutInflater, container, false)
        row.tvQuestion.text = title
        row.tvAnswer.text = body
        row.root.setOnClickListener {
            val open = row.tvAnswer.visibility == View.VISIBLE
            row.tvAnswer.visibility = if (open) View.GONE else View.VISIBLE
            row.tvArrow.text = if (open) "▾" else "▴"
        }
        container.addView(row.root)
    }

    private fun open(opened: Boolean) {
        if (!opened) {
            Snackbar.make(binding.root, "No app found to do that on this phone", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun sendMessage() {
        val message = binding.etMessage.text?.toString().orEmpty()
        val error = SupportRepository.messageError(message)
        if (error != null) {
            binding.tilMessage.error = error
            return
        }
        val userId = SessionManager.userId()
        viewLifecycleOwner.lifecycleScope.launch {
            SupportRepository.send(userId, message)
            binding.etMessage.setText("")
            Snackbar.make(binding.root, "Message sent. We'll reply within 24 hours.", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
