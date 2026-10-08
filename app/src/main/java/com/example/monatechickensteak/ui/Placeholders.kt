package com.example.monatechickensteak.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.SessionManager

/**
 * Temporary screens so navigation works. We will replace each of these
 * with the real screen in later steps (and delete its line from this file).
 */
open class PlaceholderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_placeholder, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val label = findNavController().currentDestination?.label?.toString() ?: ""
        view.findViewById<TextView>(R.id.tvPlaceholder).text = "$label\n(coming soon)"

        // Temporary logout button so we can test the login flow
        val logout = view.findViewById<Button>(R.id.btnLogout)
        if (label == "Profile") {
            logout.visibility = View.VISIBLE
            logout.setOnClickListener {
                SessionManager.logout()
                findNavController().navigate(R.id.action_global_logout)
            }
        }
    }
}


class HomeFragment : PlaceholderFragment()

