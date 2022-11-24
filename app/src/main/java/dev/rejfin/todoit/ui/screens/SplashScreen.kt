package dev.rejfin.todoit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.rejfin.todoit.R
import dev.rejfin.todoit.ui.screens.destinations.HomeScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.LoginScreenDestination
import kotlinx.coroutines.delay


@RootNavGraph(start = true)
@Destination
@Composable
fun SplashScreen(navigator: DestinationsNavigator){
    val firebaseAuth by remember { mutableStateOf(FirebaseAuth.getInstance()) }

    LaunchedEffect(key1 = Unit, block = {
        delay(1200)
        if(firebaseAuth.currentUser != null){
            navigator.navigate(HomeScreenDestination)
        }else{
            navigator.navigate(LoginScreenDestination)
        }

    })

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.todoit_icon),
            contentDescription = "Icon",
            modifier = Modifier
                .size(150.dp, 150.dp)
        )
    }
}