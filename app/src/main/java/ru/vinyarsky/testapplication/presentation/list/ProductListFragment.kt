package ru.vinyarsky.testapplication.presentation.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.launch
import ru.vinyarsky.testapplication.R
import ru.vinyarsky.testapplication.appComponent
import ru.vinyarsky.testapplication.presentation.product.ProductFragment.Companion.PRODUCT_ID_ARG

class ProductListFragment : Fragment() {

    private val viewModel: ProductListViewModel by viewModels() {
        requireContext().appComponent.viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_product_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    Log.d("!!!", "onNewState = ${state.toString()}")
                    onNewState(view, state)
                }
            }
        }
        (view as SwipeRefreshLayout).setOnRefreshListener {
            viewModel.refresh(fromSwipeToRefresh = true)
        }
    }

    private fun onNewState(view: View, state: ProductListViewModel.State) {


        val loadingLayout: FrameLayout = view.findViewById(
            R.id.fragment_product_list_loading_layout
        )
        val failedLayout: FrameLayout = view.findViewById(
            R.id.fragment_product_list_failed_layout
        )
        val dataLayout: RecyclerView = view.findViewById(
            R.id.fragment_product_list_data_layout
        )

        Log.d("!!!", "isRefreshing = false")
        (view as SwipeRefreshLayout).isRefreshing = false

        when (state) {
            is ProductListViewModel.State.Loading -> {
                (view as SwipeRefreshLayout).isRefreshing = false
                failedLayout.visibility = View.INVISIBLE
                dataLayout.visibility = View.INVISIBLE
                loadingLayout.visibility = View.VISIBLE
            }
            is ProductListViewModel.State.Failed -> {
                (view as SwipeRefreshLayout).isRefreshing = false
                dataLayout.visibility = View.INVISIBLE
                loadingLayout.visibility = View.INVISIBLE
                with(failedLayout) {
                    findViewById<TextView>(R.id.fragment_product_list_error_text_view)
                        .text = state.errorMessage
                    findViewById<Button>(R.id.fragment_product_list_error_refresh_button)
                        .setOnClickListener {
                            viewModel.refresh(fromSwipeToRefresh = false)
                        }
                    visibility = View.VISIBLE
                }
            }
            is ProductListViewModel.State.Success -> {
                failedLayout.visibility = View.INVISIBLE
                loadingLayout.visibility = View.INVISIBLE
                with(dataLayout) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = ProductListAdapter(
                        data = state.data,
                        onClick = ::navigateToProductScreen
                    )
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun navigateToProductScreen(productId: Int) {
        findNavController()
            .navigate(
                resId = R.id.action_productListFragment_to_productFragment,
                args = bundleOf(PRODUCT_ID_ARG to productId),
            )
    }
}