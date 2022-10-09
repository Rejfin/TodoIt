package dev.rejfin.todoit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ramcosta.composedestinations.DestinationsNavHost
import dev.rejfin.todoit.screens.NavGraphs
import dev.rejfin.todoit.ui.theme.TodoItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodoItTheme {
                // A surface container using the 'background' color from the theme
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}