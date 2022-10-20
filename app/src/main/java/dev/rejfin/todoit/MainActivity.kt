package dev.rejfin.todoit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.navigate
import dev.rejfin.todoit.ui.components.BottomBar
import dev.rejfin.todoit.ui.screens.NavGraphs
import dev.rejfin.todoit.ui.screens.appCurrentDestinationAsState
import dev.rejfin.todoit.ui.screens.destinations.HomeScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.LoginScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.NewTaskScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.RegisterScreenDestination
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomJetpackComposeTheme {

                val navController = rememberNavController()
                val listOfDestinationWithoutNavBar = listOf(
                    LoginScreenDestination.route,
                    RegisterScreenDestination.route,
                    NewTaskScreenDestination.route
                )

                val listOfDestinationWithFab = listOf(
                    HomeScreenDestination.route
                )

                navController.appCurrentDestinationAsState().value

                Scaffold(
                    bottomBar = {
                        if(!listOfDestinationWithoutNavBar.contains(navController.appCurrentDestinationAsState().value?.route)){
                            BottomBar(navController)
                        }
                    },
                    floatingActionButton = {
                        if(listOfDestinationWithFab.contains(navController.appCurrentDestinationAsState().value?.route)){
                            FloatingActionButton(onClick = {
                                for (route in listOfDestinationWithFab){
                                    if(route == HomeScreenDestination.route){
                                        navController.navigate(NewTaskScreenDestination)
                                        break
                                    }
                                }
                            }, backgroundColor = CustomThemeManager.colors.secondaryColor) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(id = R.string.new_task),
                                    tint = CustomThemeManager.colors.textColorOnPrimary
                                )
                            }
                        }
                    }
                ){padding->
                    Box(modifier = Modifier.padding(padding)) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}