package com.example.monatechickensteak

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.monatechickensteak.data.CartManager
import com.example.monatechickensteak.data.SessionManager
import com.example.monatechickensteak.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SessionManager.init(this)

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController

        // If already logged in, skip the login screen
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(
            if (SessionManager.isLoggedIn()) R.id.homeFragment else R.id.loginFragment
        )
        navController.graph = graph

        binding.bottomNav.setupWithNavController(navController)

        // Hide the bottom bar on the login and register screens
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val hide = destination.id == R.id.loginFragment ||
                    destination.id == R.id.registerFragment
            binding.bottomNav.visibility = if (hide) View.GONE else View.VISIBLE
        }

        // Red badge on the Cart tab showing how many items are in the cart
        CartManager.items.observe(this) {
            val count = CartManager.itemCount()
            val badge = binding.bottomNav.getOrCreateBadge(R.id.cartFragment)
            badge.isVisible = count > 0
            badge.number = count
        }
    }
}