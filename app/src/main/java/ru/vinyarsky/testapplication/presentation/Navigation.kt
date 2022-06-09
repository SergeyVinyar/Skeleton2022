package ru.vinyarsky.myfitnesser.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.vinyarsky.testapplication.presentation.list.ProductListScreen
import ru.vinyarsky.testapplication.presentation.product.ProductScreen

private const val productListDestination = "productList"
private const val productDestination = "product"

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = productListDestination
    ) {
        composable(productListDestination) {
            ProductListScreen(navController)
        }
        composable(
            "$productDestination/{productId}",
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            ProductScreen(
                navController,
                productId = backStackEntry.arguments?.getInt("productId") ?:
                    throw IllegalArgumentException("productDestination: productId is empty")
            )
        }
    }
}

fun NavHostController.navigateToProduct(productId: Int) {
    navigate("$productDestination/$productId")
}
