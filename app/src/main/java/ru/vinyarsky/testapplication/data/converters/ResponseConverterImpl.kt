package ru.vinyarsky.testapplication.data.converters

import ru.vinyarsky.testapplication.data.models.NetworkResponse
import ru.vinyarsky.testapplication.domain.models.Product

class ResponseConverterImpl : ResponseConverter {

    override fun convert(response: NetworkResponse): List<Product> =
        response.products?.map {
            Product(
                id = it.id ?: throw IllegalArgumentException("id is empty"),
                name = it.name ?: throw IllegalArgumentException("id: ${it.id}: name is empty"),
                description = it.description,
                price = it.price?.value,
                currency = when (it.price?.currency) {
                    NetworkResponse.PriceCurrency.EUR -> Product.ProductCurrency.EUR
                    NetworkResponse.PriceCurrency.USD -> Product.ProductCurrency.USD
                    NetworkResponse.PriceCurrency.RUB -> Product.ProductCurrency.RUB
                    else -> throw IllegalArgumentException(
                        "id: ${it.id}: currency ${it.price?.currency} is invalid"
                    )
                },
                available = it.available ?: false,
                rating = it.rating ?: 0.0f
            )
        } ?: emptyList()
}
