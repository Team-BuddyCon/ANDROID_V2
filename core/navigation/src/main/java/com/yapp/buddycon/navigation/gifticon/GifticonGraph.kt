package com.yapp.buddycon.navigation.gifticon

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.yapp.buddycon.gifticon.GifticonScreeen
import com.yapp.buddycon.gifticon.detail.GifticonDetailScreen
import com.yapp.buddycon.gifticon.register.GifticonRegisterScreen

private const val GIFTICON_GRAPH = "gifticon_graph"

fun NavGraphBuilder.gifticonGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = GifticonDestination.Gifticon.route,
        route = GIFTICON_GRAPH
    ) {
        composable(GifticonDestination.Gifticon.route) {
            val gifticonId = 33
            GifticonScreeen {
                navHostController.navigate(GifticonDestination.Register.route)
            }
        }

        composable(GifticonDestination.Register.route) { entry ->
            val parentEntry = remember(entry) { navHostController.getBackStackEntry(GifticonDestination.Gifticon.route) }
            GifticonRegisterScreen(
                gifticonViewModel = hiltViewModel(parentEntry),
                onNavigateToGifticonDetail = { id ->
                    val fromRegister = true
                    navHostController.navigate("${GifticonDestination.Detail.route}/$id/$fromRegister") {
                        popUpTo(GifticonDestination.Gifticon.route) {
                            inclusive = false
                        }
                    }
                },
                onBack = { navHostController.popBackStack() }
            )
        }

        composable(
            route = GifticonDestination.Detail.routeWithArg,
            arguments = GifticonDestination.Detail.arguments
        ) { entry ->
            val gifticonId = entry.arguments?.getInt(GifticonDestination.Detail.gifticonIdArg)
            val fromRegister = entry.arguments?.getBoolean(GifticonDestination.Detail.fromRegisterArg)
            GifticonDetailScreen(
                gifticonId = gifticonId,
                fromRegister = fromRegister,
                onBack = { navHostController.popBackStack() }
            )
        }
    }
}
