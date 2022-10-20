package dev.rejfin.todoit.ui.components

import androidx.compose.foundation.background
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigateTo
import dev.rejfin.todoit.BottomBarDestination
import dev.rejfin.todoit.ui.screens.NavGraphs
import dev.rejfin.todoit.ui.screens.appCurrentDestinationAsState
import dev.rejfin.todoit.ui.screens.startAppDestination
import dev.rejfin.todoit.ui.theme.CustomThemeManager

@Composable
fun BottomBar(
    navController: NavController
) {
    val currentDestination: Any = navController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    BottomNavigation {
        BottomBarDestination.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigateTo(destination.direction) {
                        launchSingleTop = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = stringResource(destination.label))},
                label = { Text(stringResource(destination.label)) },
                modifier = Modifier.background(CustomThemeManager.colors.primaryColor)
            )
        }
    }
}