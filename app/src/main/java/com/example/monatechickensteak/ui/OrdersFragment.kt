package com.example.monatechickensteak.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monatechickensteak.R
import com.example.monatechickensteak.data.CartManager
import com.example.monatechickensteak.data.OrderRepository
import com.example.monatechickensteak.data.SessionManager
import com.example.monatechickensteak.databinding.FragmentOrdersBinding
import com.example.monatechickensteak.model.Order
import com.example.monatechickensteak.util.OrderNotifier
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = OrderAdapter(
            onAdvance = { advance(it) },
            onReorder = { reorder(it) }
        )
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadOrders()
    }

    private fun loadOrders() {
        viewLifecycleOwner.lifecycleScope.launch {
            val orders = OrderRepository.ordersFor(SessionManager.userId())
            adapter.submitList(orders)
            binding.tvEmpty.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    /** Demo helper: acts like the kitchen moving the order along, and notifies the customer. */
    private fun advance(order: Order) {
        viewLifecycleOwner.lifecycleScope.launch {
            val updated = OrderRepository.advanceStatus(order.id)
            if (updated != null && updated.status != order.status) {
                OrderNotifier.notifyStatus(requireContext().applicationContext, updated)
            }
            loadOrders()
        }
    }

    private fun reorder(order: Order) {
        order.items.forEach { line -> CartManager.add(line.item, line.quantity) }
        Snackbar.make(binding.root, "Items from order #${order.id} added to cart", Snackbar.LENGTH_LONG)
            .setAction("View cart") { findNavController().navigate(R.id.cartFragment) }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
