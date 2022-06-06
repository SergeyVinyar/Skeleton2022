package ru.vinyarsky.testapplication.presentation.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vinyarsky.testapplication.domain.Interactor
import ru.vinyarsky.testapplication.domain.models.Product

class ProductListViewModel(
    private val interactor: Interactor
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private var refreshDataJob: Job? = null

    init {
        launchRefreshData(
            withCacheInvalidation = false,
            fromSwipeToRefresh = false
        )
    }

    private fun launchRefreshData(
        withCacheInvalidation: Boolean,
        fromSwipeToRefresh: Boolean
    ) {
        refreshDataJob?.cancel()
        refreshDataJob = viewModelScope.launch(Dispatchers.Default) {
            if (!fromSwipeToRefresh) {
                _state.value = State.Loading
            }
            try {
                if (withCacheInvalidation) {
                    interactor.invalidateCache()
                }
                _state.value = State.Success(interactor.getProductList())
            } catch (e: Exception) {
                // Yeah, not a good idea to show internal messages to the customer
                _state.value = State.Failed(e.message)
            }
        }
    }

    fun refresh(fromSwipeToRefresh: Boolean) {
        launchRefreshData(
            withCacheInvalidation = true,
            fromSwipeToRefresh = fromSwipeToRefresh
        )
    }

    sealed class State {
        object Loading : State()
        data class Failed(val errorMessage: String?) : State()
        data class Success(val data: List<Product>) : State() {
            override fun equals(other: Any?): Boolean {
                // Weird but states with the same content are not same states.
                // We could have avoided it, but SwipeRefreshLayout
                // doesn't follow the principle of MVI. It tries to rule UI independently.
                return false
            }
            override fun hashCode(): Int {
                // We want to avoid a warning, that we don't override hashCode
                // after overriding equals.
                return super.hashCode()
            }
        }
    }
}