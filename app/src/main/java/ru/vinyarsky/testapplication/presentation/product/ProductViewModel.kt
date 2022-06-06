package ru.vinyarsky.testapplication.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vinyarsky.testapplication.domain.Interactor
import ru.vinyarsky.testapplication.domain.models.Product

class ProductViewModel(
    private val interactor: Interactor
) : ViewModel() {

    private var productId: Int = 0

    fun onCreate(productId: Int) {
        this.productId = productId
        launchRefreshData(withCacheInvalidation = false)
    }

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    private var refreshDataJob: Job? = null

    fun launchRefreshData(withCacheInvalidation: Boolean) {
        refreshDataJob?.cancel()
        refreshDataJob = viewModelScope.launch(Dispatchers.Default) {
            _state.value = State.Loading
            try {
                if (withCacheInvalidation) {
                    interactor.invalidateCache()
                }
                _state.value = State.Success(interactor.getProductById(productId)!!)
            } catch (e: Exception) {
                // Yeah, not a good idea to show internal messages to the customer
                _state.value = State.Failed(e.message)
            }
        }
    }

    fun refresh() {
        launchRefreshData(withCacheInvalidation = true)
    }

    sealed class State {
        object Loading : State()
        data class Failed(val errorMessage: String?) : State()
        data class Success(val data: Product) : State()
    }
}
