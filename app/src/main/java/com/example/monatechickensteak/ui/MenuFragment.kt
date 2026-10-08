package com.example.monatechickensteak.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.CartManager
import com.example.monatechickensteak.data.MockData
import com.example.monatechickensteak.databinding.FragmentMenuBinding
import com.example.monatechickensteak.model.FoodItem
import com.example.monatechickensteak.util.MenuFilter
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FoodAdapter
    private var selectedCategory = "All"
    private var query = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FoodAdapter { food -> addToCart(food) }
        binding.rvMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMenu.adapter = adapter

        setupChips()

        binding.etSearch.doAfterTextChanged {
            query = it?.toString().orEmpty()
            refreshList()
        }

        refreshList()
    }

    private fun setupChips() {
        MockData.categories.forEachIndexed { index, category ->
            val chip = layoutInflater.inflate(
                R.layout.item_category_chip, binding.chipGroup, false
            ) as Chip
            chip.id = View.generateViewId()
            chip.text = category
            chip.tag = category
            chip.isChecked = index == 0 // "All" starts selected
            binding.chipGroup.addView(chip)
        }

        binding.chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            val chip = checkedIds.firstOrNull()?.let { group.findViewById<Chip>(it) }
            selectedCategory = chip?.tag as? String ?: "All"
            refreshList()
        }
    }

    private fun refreshList() {
        val results = MenuFilter.filter(MockData.menu, selectedCategory, query)
        adapter.submitList(results)
        binding.tvEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun addToCart(food: FoodItem) {
        CartManager.add(food)
        Snackbar.make(binding.root, "${food.name} added to cart", Snackbar.LENGTH_SHORT)
            .setAction("View cart") { findNavController().navigate(R.id.cartFragment) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}