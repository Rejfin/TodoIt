package dev.rejfin.todoit

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.ramcosta.composedestinations.DestinationsNavHost
import dev.rejfin.todoit.ui.components.BottomBar
import dev.rejfin.todoit.ui.screens.NavGraphs
import dev.rejfin.todoit.ui.screens.appCurrentDestinationAsState
import dev.rejfin.todoit.ui.screens.destinations.*
import dev.rejfin.todoit.ui.theme.CustomJetpackComposeTheme
import dev.rejfin.todoit.ui.theme.CustomTheme
import dev.rejfin.todoit.ui.theme.CustomThemeManager


class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /** This block of code is for AppCheck from Firebase */
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        loadTheme(this)
        setContent {
            CustomJetpackComposeTheme {

                val navController = rememberNavController()
                val listOfDestinationWithoutNavBar = listOf(
                    LoginScreenDestination.route,
                    RegisterScreenDestination.route,
                    NewTaskScreenDestination.route,
                    SplashScreenDestination.route,
                    ForgottenPasswordScreenDestination.route
                )

                navController.appCurrentDestinationAsState().value

                Scaffold(
                    bottomBar = {
                        if(!listOfDestinationWithoutNavBar.contains(navController.appCurrentDestinationAsState().value?.route)){
                            BottomBar(navController)
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

    private fun loadTheme(context: Context){
        val pref = applicationContext.getSharedPreferences("TodoItPref", MODE_PRIVATE)
        val nightModeFlags: Int = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(pref.contains("dark_mode")) {
            if (pref.getString("dark_mode", "") == "Light") {
                CustomThemeManager.customTheme = CustomTheme.LIGHT
                return
            } else if (pref.getString("dark_mode", "") == "Dark") {
                CustomThemeManager.customTheme = CustomTheme.DARK
                return
            }else{
                when (nightModeFlags) {
                    Configuration.UI_MODE_NIGHT_YES -> CustomThemeManager.customTheme = CustomTheme.DARK
                    Configuration.UI_MODE_NIGHT_NO -> CustomThemeManager.customTheme = CustomTheme.LIGHT
                    Configuration.UI_MODE_NIGHT_UNDEFINED -> CustomThemeManager.customTheme = CustomTheme.LIGHT
                }
                return
            }
        }

        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> CustomThemeManager.customTheme = CustomTheme.DARK
            Configuration.UI_MODE_NIGHT_NO -> CustomThemeManager.customTheme = CustomTheme.LIGHT
            Configuration.UI_MODE_NIGHT_UNDEFINED -> CustomThemeManager.customTheme = CustomTheme.LIGHT
        }
    }
}