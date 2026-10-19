package com.example.monatechickensteak.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.AuthRepository
import com.example.monatechickensteak.data.AuthResult
import com.example.monatechickensteak.data.CartManager
import com.example.monatechickensteak.data.SessionManager
import com.example.monatechickensteak.databinding.FragmentProfileBinding
import com.example.monatechickensteak.util.CheckoutOptions
import com.example.monatechickensteak.util.OrderNotifier
import com.example.monatechickensteak.util.Validators
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    /** Android 13+ asks the customer before an app may show notifications. */
    private val notificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted && _binding != null) {
                binding.switchNotifications.isChecked = false
                snack("Notifications are blocked. Allow them in the phone's app settings.")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshHeader()
        setupSettings()

        // Google accounts have no password, so hide the change-password section
        val isGoogle = SessionManager.provider() == AuthRepository.PROVIDER_GOOGLE
        binding.cardChangePassword.visibility = if (isGoogle) View.GONE else View.VISIBLE
        binding.tvProvider.visibility = if (isGoogle) View.VISIBLE else View.GONE
        binding.tvProvider.text = "Signed in with Google"

        binding.btnSaveName.setOnClickListener { saveName() }
        binding.btnChangePassword.setOnClickListener { changePassword() }
        binding.btnLogout.setOnClickListener { confirmLogout() }
        binding.btnDeleteAccount.setOnClickListener { confirmDelete() }
        binding.btnLoyalty.setOnClickListener { findNavController().navigate(R.id.loyaltyFragment) }
        binding.btnReviews.setOnClickListener { findNavController().navigate(R.id.reviewsFragment) }
        binding.btnSupport.setOnClickListener { findNavController().navigate(R.id.supportFragment) }
        binding.etName.doAfterTextChanged { binding.tilName.error = null }
        binding.etCurrent.doAfterTextChanged { binding.tilCurrent.error = null }
        binding.etNew.doAfterTextChanged { binding.tilNew.error = null }
        binding.etConfirm.doAfterTextChanged { binding.tilConfirm.error = null }
    }

    private fun setupSettings() {
        // Notifications switch (set the starting value BEFORE attaching the listener)
        binding.switchNotifications.isChecked = SessionManager.notificationsEnabled()
        binding.switchNotifications.setOnCheckedChangeListener { _, checked ->
            SessionManager.setNotificationsEnabled(checked)
            if (checked && !OrderNotifier.hasPermission(requireContext()) &&
                Build.VERSION.SDK_INT >= 33
            ) {
                notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            snack(if (checked) "Order notifications turned on" else "Order notifications turned off")
        }

        // Defaults that are filled in for the customer at checkout
        binding.dropdownDefaultTime.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, CheckoutOptions.collectionTimes)
        )
        binding.dropdownDefaultTime.setText(
            CheckoutOptions.validTime(SessionManager.defaultCollectionTime()), false
        )
        binding.dropdownDefaultTime.setOnItemClickListener { _, _, position, _ ->
            SessionManager.setDefaultCollectionTime(CheckoutOptions.collectionTimes[position])
            snack("Default collection time saved")
        }

        binding.dropdownDefaultPayment.setAdapter(
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, CheckoutOptions.paymentMethods)
        )
        val savedPayment = CheckoutOptions.validPaymentOrNull(SessionManager.defaultPayment())
        if (savedPayment != null) binding.dropdownDefaultPayment.setText(savedPayment, false)
        binding.dropdownDefaultPayment.setOnItemClickListener { _, _, position, _ ->
            SessionManager.setDefaultPayment(CheckoutOptions.paymentMethods[position])
            snack("Default payment method saved")
        }
    }

    private fun refreshHeader() {
        val name = SessionManager.userName()
        val email = SessionManager.userEmail()
        binding.tvName.text = name
        binding.tvEmail.text = email
        binding.tvAvatar.text = name.trim().firstOrNull()?.uppercase() ?: "?"
        binding.etName.setText(name)
        viewLifecycleOwner.lifecycleScope.launch {
            binding.tvPoints.text = AuthRepository.loyaltyPoints(SessionManager.userId()).toString()
        }
    }

    private fun saveName() {
        val name = binding.etName.text?.toString().orEmpty()
        val error = Validators.nameError(name)
        binding.tilName.error = error
        if (error != null) return

        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = AuthRepository.updateName(SessionManager.userId(), name)) {
                is AuthResult.Success -> {
                    SessionManager.saveSession(result.user)
                    refreshHeader()
                    snack("Name updated")
                }
                is AuthResult.Error -> snack(result.message)
            }
        }
    }

    private fun changePassword() {
        val current = binding.etCurrent.text?.toString().orEmpty()
        val newPassword = binding.etNew.text?.toString().orEmpty()
        val confirm = binding.etConfirm.text?.toString().orEmpty()

        val currentError = Validators.loginPasswordError(current)
        val newError = Validators.newPasswordError(newPassword)
        val confirmError = Validators.confirmPasswordError(newPassword, confirm)

        binding.tilCurrent.error = currentError
        binding.tilNew.error = newError
        binding.tilConfirm.error = confirmError
        if (listOf(currentError, newError, confirmError).any { it != null }) return

        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = AuthRepository.changePassword(
                SessionManager.userId(), current, newPassword
            )) {
                is AuthResult.Success -> {
                    binding.etCurrent.setText("")
                    binding.etNew.setText("")
                    binding.etConfirm.setText("")
                    snack("Password updated")
                }
                is AuthResult.Error -> {
                    if (result.message.startsWith("Current")) {
                        binding.tilCurrent.error = result.message
                    } else {
                        snack(result.message)
                    }
                }
            }
        }
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Log out?")
            .setMessage("You will need to log in again to place orders.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Log out") { _, _ ->
                SessionManager.logout()
                CartManager.clear()
                findNavController().navigate(R.id.action_global_logout)
            }
            .show()
    }

    private fun confirmDelete() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete your account?")
            .setMessage("Your account, orders and loyalty points will be removed. This cannot be undone.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ -> deleteAccount() }
            .show()
    }

    private fun deleteAccount() {
        val userId = SessionManager.userId()
        viewLifecycleOwner.lifecycleScope.launch {
            val deleted = AuthRepository.deleteAccount(userId)
            if (deleted) {
                SessionManager.logout()
                CartManager.clear()
                findNavController().navigate(R.id.action_global_logout)
            } else {
                snack("Could not delete the account. Please log in again.")
            }
        }
    }

    private fun snack(message: String) {
        _binding?.let { Snackbar.make(it.root, message, Snackbar.LENGTH_SHORT).show() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
