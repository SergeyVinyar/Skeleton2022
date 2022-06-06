package ru.vinyarsky.testapplication.data

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertTrue
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Test
import ru.vinyarsky.testapplication.data.converters.ResponseConverter
import ru.vinyarsky.testapplication.data.models.NetworkResponse
import ru.vinyarsky.testapplication.domain.models.Product

@ExperimentalCoroutinesApi
class RepositoryImplTest {

    private val networkResponse = NetworkResponse(products = null)
    private val expectedProductList = emptyList<Product>()

    @Test
    fun `getProducts invokes NetworkService and then ResponseConverter`() = runTest {
        // Prepare
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val networkService = mockk<NetworkService> {
            coEvery { getProducts() } returns networkResponse
        }

        val responseConverter = mockk<ResponseConverter> {
            coEvery { convert(any()) } returns expectedProductList
        }

        val repository = RepositoryImpl(
            networkService = networkService,
            responseConverter = responseConverter,
            dispatcherIO = dispatcher
        )

        // Do
        val actualProductList = repository.getProducts()

        // Verify
        assertTrue(actualProductList === expectedProductList)
        verify { responseConverter.convert(networkResponse) }
    }
}