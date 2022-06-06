package ru.vinyarsky.testapplication.presentation.product

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import ru.vinyarsky.testapplication.R
import ru.vinyarsky.testapplication.appComponent
import ru.vinyarsky.testapplication.presentation.list.ProductListAdapter
import ru.vinyarsky.testapplication.presentation.list.ProductListViewModel

class ProductFragment : Fragment() {

    companion object {
        const val PRODUCT_ID_ARG = "productId"
    }

    private val viewModel: ProductViewModel by viewModels() {
        requireContext().appComponent.viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.onCreate(requireArguments().getInt(PRODUCT_ID_ARG))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    onNewState(view, state)
                }
            }
        }
    }

    private fun onNewState(view: View, state: ProductViewModel.State) {
        val loadingLayout: FrameLayout = view.findViewById(
            R.id.fragment_product_loading_layout
        )
        val failedLayout: FrameLayout = view.findViewById(
            R.id.fragment_product_failed_layout
        )
        val dataLayout: CardView = view.findViewById(
            R.id.fragment_product_data_layout
        )

        when (state) {
            is ProductViewModel.State.Loading -> {
                failedLayout.visibility = View.INVISIBLE
                dataLayout.visibility = View.INVISIBLE
                loadingLayout.visibility = View.VISIBLE
            }
            is ProductViewModel.State.Failed -> {
                dataLayout.visibility = View.INVISIBLE
                loadingLayout.visibility = View.INVISIBLE
                with(failedLayout) {
                    findViewById<TextView>(R.id.fragment_product_error_text_view)
                        .text = state.errorMessage
                    visibility = View.VISIBLE
                }
            }
            is ProductViewModel.State.Success -> {
                failedLayout.visibility = View.INVISIBLE
                loadingLayout.visibility = View.INVISIBLE
                with(dataLayout) {
                    findViewById<RatingBar>(R.id.fragment_product_rating)
                        .rating = state.data.rating
                    findViewById<TextView>(R.id.fragment_product_name)
                        .text = state.data.name
                    findViewById<TextView>(R.id.fragment_product_description)
                        .text = state.data.description
                    findViewById<TextView>(R.id.fragment_product_price)
                        .text = if (state.data.price != null) {
                            dataLayout.resources.getString(
                                R.string.readable_price,
                                state.data.price,
                                state.data.currency.name
                            )
                        } else {
                            dataLayout.resources.getString(R.string.price_not_available)
                        }
                    visibility = View.VISIBLE
                }
            }
        }
    }
}