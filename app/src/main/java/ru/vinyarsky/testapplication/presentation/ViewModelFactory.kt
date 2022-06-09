package ru.vinyarsky.testapplication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vinyarsky.testapplication.domain.Interactor
import ru.vinyarsky.testapplication.presentation.list.ProductListViewModel
import ru.vinyarsky.testapplication.presentation.product.ProductViewModel
import java.lang.IllegalArgumentException

class ViewModelFactory(
    private val interactor: Interactor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when (modelClass) {
            ProductListViewModel::class.java -> ProductListViewModel(interactor)
            ProductViewModel::class.java -> ProductViewModel(interactor)
            else -> throw IllegalArgumentException()
        } as T
}