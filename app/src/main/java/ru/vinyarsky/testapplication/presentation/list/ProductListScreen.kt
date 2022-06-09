package ru.vinyarsky.testapplication.presentation.list

import android.view.ContextThemeWrapper
import android.widget.RatingBar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import ru.vinyarsky.myfitnesser.presentation.navigateToProduct
import ru.vinyarsky.testapplication.R
import ru.vinyarsky.testapplication.appComponent
import ru.vinyarsky.testapplication.domain.models.Product
import ru.vinyarsky.testapplication.presentation.theme.Typography

@Composable
fun ProductListScreen(navController: NavHostController) {
    val viewModel: ProductListViewModel = viewModel(
        factory = LocalContext.current.appComponent.viewModelFactory
    )

    when (val uiStateValue = viewModel.uiUiState.value) {
        is ProductListViewModel.UiState.Loading -> {
            LoadingState()
        }
        is ProductListViewModel.UiState.Failed -> {
            FailedState(uiStateValue) {
                viewModel.refresh(fromSwipeToRefresh = false)
            }
        }
        is ProductListViewModel.UiState.Success -> {
            SuccessState(uiStateValue) { productId ->
                navController.navigateToProduct(productId = productId)
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val cardElevation = dimensionResource(id = R.dimen.card_elevation)
        Card(elevation = cardElevation) {
            CircularProgressIndicator(
                modifier = Modifier.padding(dimensionResource(R.dimen.loader_padding))
            )
        }
    }
}

@Composable
fun FailedState(
    state: ProductListViewModel.UiState.Failed,
    onRefreshClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val cardElevation = dimensionResource(id = R.dimen.card_elevation)
        Card(elevation = cardElevation) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = state.errorMessage ?:
                        stringResource(id = androidx.compose.ui.R.string.default_error_message),
                    modifier = Modifier.padding(
                        bottom = dimensionResource(R.dimen.text_separator_height)
                    )
                )
                Button(
                    onClick = onRefreshClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(stringResource(R.string.refresh_button))
                }
            }
        }
    }
}

@Composable
fun SuccessState(
    state: ProductListViewModel.UiState.Success,
    onItemClick: (Int) -> Unit
) {
    val separatorHeight = dimensionResource(id = R.dimen.list_item_separator_height)
    
    LazyColumn(
        contentPadding = PaddingValues(top = separatorHeight, bottom = separatorHeight)
    ) {
        items(
            count = state.data.size,
            key = { index -> state.data[index].id }
        ) { index ->
            ProductItem(
                product = state.data[index],
                onClick = { onItemClick(state.data[index].id) }
            )
        }
    }
}

@Composable
fun ProductItem(
    product: Product,
    onClick: () -> Unit
) {
    val separatorHeight = dimensionResource(id = R.dimen.list_item_separator_height)
    val cardPadding = dimensionResource(id = R.dimen.card_padding)
    val cardElevation = dimensionResource(id = R.dimen.card_elevation)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(separatorHeight)
            .clickable(onClick = onClick),
        elevation = cardElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding)
        ) {
            Text(
                text = product.name,
                style = Typography.body1
            )
            val printablePrice =
                if (product.price != null) {
                    // Of course, a proper way would be to have a separate model for a view
                    stringResource(
                        R.string.readable_price,
                        product.price,
                        product.currency.name
                    )
                } else {
                    stringResource(R.string.price_not_available)
                }
            Text(
                text = printablePrice,
                style = Typography.caption
            )
            AndroidView(
                factory = { context ->
                    RatingBar(
                        ContextThemeWrapper(
                            context,
                            com.google.android.material.R.style.Widget_AppCompat_RatingBar_Small
                        ), null, 0
                    ).apply {
                        numStars = 5
                    }
                },
                update = { view -> view.rating = product.rating },
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Preview
@Composable
fun LoadingStatePreview() {
    LoadingState()
}

@Preview
@Composable
fun FailedStatePreview() {
    val mockedState = ProductListViewModel.UiState.Failed(
        errorMessage = "Something weird has happened"
    )
    FailedState(mockedState) { }
}

@Preview
@Composable
fun SuccessStatePreview() {
    val mockedState = ProductListViewModel.UiState.Success(
        data = listOf(
            Product(
                id = 1,
                name = "Some name 1",
                description = "Some description 1",
                price = 99.99f,
                currency = Product.ProductCurrency.EUR,
                available = true,
                rating = 2.5f
            ),
            Product(
                id = 2,
                name = "Some name 2",
                description = "Some description 2",
                price = 12.50f,
                currency = Product.ProductCurrency.RUB,
                available = false,
                rating = 5.0f
            ),
            Product(
                id = 4,
                name = "Some name 3",
                description = null,
                price = 1999.99f,
                currency = Product.ProductCurrency.USD,
                available = true,
                rating = 1.5f
            ),
        )
    )
    SuccessState(mockedState) { }
}
