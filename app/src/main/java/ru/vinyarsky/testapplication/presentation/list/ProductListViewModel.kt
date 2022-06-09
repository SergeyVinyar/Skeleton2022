package ru.vinyarsky.testapplication.presentation.list

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.vinyarsky.testapplication.domain.Interactor
import ru.vinyarsky.testapplication.domain.models.Product

class ProductListViewModel(
    private val interactor: Interactor
) : ViewModel() {

    private val _uiState: MutableState<UiState> = mutableStateOf(UiState.Loading)
    val uiUiState: State<UiState> = _uiState

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
        refreshDataJob = viewModelScope.launch {
            if (!fromSwipeToRefresh) {
                _uiState.value = UiState.Loading
            }
            try {
                if (withCacheInvalidation) {
                    interactor.invalidateCache()
                }
                _uiState.value = UiState.Success(interactor.getProductList())
            } catch (e: Exception) {
                // Yeah, not a good idea to show internal messages to the customer
                _uiState.value = UiState.Failed(e.message)
            }
        }
    }

    fun refresh(fromSwipeToRefresh: Boolean) {
        launchRefreshData(
            withCacheInvalidation = true,
            fromSwipeToRefresh = fromSwipeToRefresh
        )
    }

    sealed class UiState {
        object Loading : UiState()
        data class Failed(val errorMessage: String?) : UiState()
        data class Success(val data: List<Product>) : UiState() {
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