package ru.vinyarsky.testapplication.domain

import kotlinx.coroutines.*
import ru.vinyarsky.testapplication.domain.models.Product
import java.util.concurrent.atomic.AtomicReference

class Interactor(
    private val repository: Repository,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) {

    private var cache = AtomicReference<Deferred<List<Product>>>(null)

    private suspend fun getCacheOrNetwork(): List<Product> = withContext(dispatcherIO) {
        cache.updateAndGet { oldValue ->
            oldValue ?: async(start = CoroutineStart.LAZY) {
                repository.getProducts()
            }
        }.await()
    }

    suspend fun getProductList(): List<Product> = getCacheOrNetwork()

    suspend fun getProductById(productId: Int): Product? =
        getCacheOrNetwork().firstOrNull { it.id == productId }

    fun invalidateCache() {
        cache.set(null)
    }
}