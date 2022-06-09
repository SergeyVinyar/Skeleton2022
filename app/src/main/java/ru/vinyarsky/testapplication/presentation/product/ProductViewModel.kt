package ru.vinyarsky.testapplication.presentation.product

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vinyarsky.testapplication.domain.Interactor
import ru.vinyarsky.testapplication.domain.models.Product
import ru.vinyarsky.testapplication.presentation.list.ProductListViewModel

class ProductViewModel(
    private val interactor: Interactor
) : ViewModel() {

    private var productId: Int = 0

    fun initIfNeeded(productId: Int) {
        if (this.productId != productId) {
            this.productId = productId
            launchRefreshData(withCacheInvalidation = false)
        }
    }

    private val _uiState: MutableState<UiState> = mutableStateOf(UiState.Loading)
    val uiState: State<UiState> = _uiState

    private var refreshDataJob: Job? = null

    private fun launchRefreshData(withCacheInvalidation: Boolean) {
        refreshDataJob?.cancel()
        refreshDataJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                if (withCacheInvalidation) {
                    interactor.invalidateCache()
                }
                _uiState.value = UiState.Success(interactor.getProductById(productId)!!)
            } catch (e: Exception) {
                // Yeah, not a good idea to show internal messages to the customer
                _uiState.value = UiState.Failed(e.message)
            }
        }
    }

    fun refresh() {
        launchRefreshData(withCacheInvalidation = true)
    }

    sealed class UiState {
        object Loading : UiState()
        data class Failed(val errorMessage: String?) : UiState()
        data class Success(val data: Product) : UiState()
    }
}
