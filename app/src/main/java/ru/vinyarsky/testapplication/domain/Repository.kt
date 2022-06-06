package ru.vinyarsky.testapplication.domain

import ru.vinyarsky.testapplication.domain.models.Product

interface Repository {

    suspend fun getProducts(): List<Product>
}