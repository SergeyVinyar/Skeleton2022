package ru.vinyarsky.testapplication.presentation.product

import android.util.Log
import android.view.ContextThemeWrapper
import android.widget.RatingBar
import androidx.compose.foundation.layout.*
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
import ru.vinyarsky.testapplication.R
import ru.vinyarsky.testapplication.appComponent
import ru.vinyarsky.testapplication.domain.models.Product
import ru.vinyarsky.testapplication.presentation.theme.Typography

@Composable
fun ProductScreen(navController: NavHostController, productId: Int) {
    val viewModel: ProductViewModel = viewModel<ProductViewModel>(
        factory = LocalContext.current.appComponent.viewModelFactory
    ).apply { initIfNeeded(productId) }

    when (val uiStateValue = viewModel.uiState.value) {
        is ProductViewModel.UiState.Loading -> {
            LoadingState()
        }
        is ProductViewModel.UiState.Failed -> {
            FailedState(uiStateValue) {
                viewModel.refresh()
            }
        }
        is ProductViewModel.UiState.Success -> {
            SuccessState(uiStateValue)
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
    state: ProductViewModel.UiState.Failed,
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
                    text = state.errorMessage ?: stringResource(R.string.default_error_message),
                    modifier = Modifier.padding(bottom = dimensionResource(R.dimen.card_padding))
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
    state: ProductViewModel.UiState.Success
) {
    val separatorHeight = dimensionResource(id = R.dimen.list_item_separator_height)
    val cardPadding = dimensionResource(id = R.dimen.card_padding)
    val cardElevation = dimensionResource(id = R.dimen.card_elevation)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(separatorHeight),
        elevation = cardElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
                    update = { view -> view.rating = state.data.rating },
                )

                val printablePrice =
                    if (state.data.price != null) {
                        // Of course, a proper way would be to have a separate model for a view
                        stringResource(
                            R.string.readable_price,
                            state.data.price,
                            state.data.currency.name
                        )
                    } else {
                        stringResource(R.string.price_not_available)
                    }
                Text(
                    text = printablePrice,
                    style = Typography.caption
                )
            }

            Text(
                text = state.data.name,
                style = Typography.body1
            )

            Text(
                text = state.data.description ?: "",
                style = Typography.body2
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
    val mockedState = ProductViewModel.UiState.Failed(
        errorMessage = "Something weird has happened"
    )
    FailedState(mockedState) { }
}

@Preview
@Composable
fun SuccessStatePreview() {
    val mockedState = ProductViewModel.UiState.Success(
        data = Product(
            id = 1,
            name = "Some name 1",
            description = "Some description 1",
            price = 99.99f,
            currency = Product.ProductCurrency.EUR,
            available = true,
            rating = 2.5f
        )
    )
    SuccessState(mockedState)
}
