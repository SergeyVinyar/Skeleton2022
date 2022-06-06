package ru.vinyarsky.testapplication.data.converters

import junit.framework.Assert.assertEquals
import junit.framework.TestCase
import org.junit.Test
import ru.vinyarsky.testapplication.data.models.NetworkResponse
import ru.vinyarsky.testapplication.domain.models.Product

class ResponseConverterImplTest {

    private val converter = ResponseConverterImpl()

    @Test
    fun testConvert() {
        // Prepare

        val response = NetworkResponse(
            products = listOf(
                NetworkResponse.Item(
                    id = 1,
                    name = "Some name 1",
                    description = "Some description 1",
                    price = NetworkResponse.Price(
                        value = 12.99f,
                        currency = NetworkResponse.PriceCurrency.EUR
                    ),
                    available = true,
                    rating = 2.5f
                ),
                NetworkResponse.Item(
                    id = 2,
                    name = "Some name 2",
                    description = null,
                    price = NetworkResponse.Price(
                        value = 5.45f,
                        currency = NetworkResponse.PriceCurrency.USD
                    ),
                    available = false,
                    rating = 5.0f
                )
            )
        )

        val expected = listOf<Product>(
            Product(
                id = 1,
                name = "Some name 1",
                description = "Some description 1",
                price = 12.99f,
                currency = Product.ProductCurrency.EUR,
                available = true,
                rating = 2.5f
            ),
            Product(
                id = 2,
                name = "Some name 2",
                description = null,
                price = 5.45f,
                currency = Product.ProductCurrency.USD,
                available = false,
                rating = 5.0f
            )
        )

        // Do
        val actual = converter.convert(response)

        // Verify
        assertEquals(expected, actual)
    }
}