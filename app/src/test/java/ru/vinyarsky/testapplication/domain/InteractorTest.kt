package ru.vinyarsky.testapplication.domain

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Test
import ru.vinyarsky.testapplication.domain.models.Product

@ExperimentalCoroutinesApi
class InteractorTest {

    private val expectedProduct1 = Product(
        id = 1,
        name = "Some name 1",
        description = "Some description 1",
        price = 99.99f,
        currency = Product.ProductCurrency.RUB,
        available = true,
        rating = 1.8f
    )

    private val expectedProduct2 = Product(
        id = 2,
        name = "Some name 2",
        description = "Some description 2",
        price = 11.22f,
        currency = Product.ProductCurrency.USD,
        available = false,
        rating = 5.0f
    )

    private val expectedProductList = listOf<Product>(
        expectedProduct1,
        expectedProduct2
    )

    private val repository = mockk<Repository> {
        coEvery { getProducts() } returns expectedProductList
    }

    @Test
    fun `getProductList invokes repository and then uses cache`() = runTest {
        // Prepare
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val interactor = Interactor(
            repository = repository,
            dispatcherIO = dispatcher
        )

        // Do
        val actualProductList1 = interactor.getProductList()
        val actualProductList2 = interactor.getProductList()

        // Verify
        assertTrue(actualProductList1 === expectedProductList)
        assertTrue(actualProductList2 === expectedProductList)
        coVerify(exactly = 1) { repository.getProducts() }
    }

    @Test
    fun `getProductList invokes repository once after cache invalidating`() = runTest {
        // Prepare
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val interactor = Interactor(
            repository = repository,
            dispatcherIO = dispatcher
        )

        // Do
        val actualProductList1 = interactor.getProductList()
        val actualProductList2 = interactor.getProductList()

        interactor.invalidateCache()

        val actualProductList3 = interactor.getProductList()
        val actualProductList4 = interactor.getProductList()

        // Verify
        assertEquals(expectedProductList, actualProductList1)
        assertEquals(expectedProductList, actualProductList2)
        assertEquals(expectedProductList, actualProductList3)
        assertEquals(expectedProductList, actualProductList4)
        coVerify(exactly = 2) { repository.getProducts() }
    }

    @Test
    fun `getProductById invokes Repository and filters out required product using cache`() = runTest {
        // Prepare
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val interactor = Interactor(
            repository = repository,
            dispatcherIO = dispatcher
        )

        // Do
        val actualProduct1 = interactor.getProductById(productId = 1)
        val actualProduct2 = interactor.getProductById(productId = 2)

        // Verify
        assertEquals(expectedProduct1, actualProduct1)
        assertEquals(expectedProduct2, actualProduct2)
        coVerify(exactly = 1) { repository.getProducts() }
    }

    @Test
    fun `getProductById invokes repository once after cache invalidating`() = runTest {
        // Prepare
        val dispatcher = StandardTestDispatcher(testScheduler)
        Dispatchers.setMain(dispatcher)

        val interactor = Interactor(
            repository = repository,
            dispatcherIO = dispatcher
        )

        // Do
        val actualProduct1 = interactor.getProductById(productId = 1)
        val actualProduct2 = interactor.getProductById(productId = 2)

        interactor.invalidateCache()

        val actualProduct3 = interactor.getProductById(productId = 1)
        val actualProduct4 = interactor.getProductById(productId = 2)

        // Verify
        assertEquals(expectedProduct1, actualProduct1)
        assertEquals(expectedProduct2, actualProduct2)
        assertEquals(expectedProduct1, actualProduct3)
        assertEquals(expectedProduct2, actualProduct4)
        coVerify(exactly = 2) { repository.getProducts() }
    }
}
