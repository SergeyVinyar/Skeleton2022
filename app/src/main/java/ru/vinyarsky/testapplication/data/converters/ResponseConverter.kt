package ru.vinyarsky.testapplication.data.converters

import ru.vinyarsky.testapplication.data.models.NetworkResponse
import ru.vinyarsky.testapplication.domain.models.Product

interface ResponseConverter {

    fun convert(response: NetworkResponse): List<Product>
}
