package ru.vinyarsky.testapplication.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ru.vinyarsky.testapplication.data.converters.ResponseConverter
import ru.vinyarsky.testapplication.domain.Repository
import ru.vinyarsky.testapplication.domain.models.Product

class RepositoryImpl(
    private val networkService: NetworkService,
    private val responseConverter: ResponseConverter,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : Repository {

    override suspend fun getProducts(): List<Product> {
        val response = withContext(dispatcherIO) {
            // Just to look at a loading screen, not a production code
            delay(500)
            networkService.getProducts()
        }
        return responseConverter.convert(response)
    }
}