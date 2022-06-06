package ru.vinyarsky.testapplication.domain.models

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Float?,
    val currency: ProductCurrency,
    val available : Boolean,
    val rating: Float,
) {
    enum class ProductCurrency {
        EUR, USD, RUB
    }
}
