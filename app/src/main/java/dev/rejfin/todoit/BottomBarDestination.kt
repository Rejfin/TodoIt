package dev.rejfin.todoit

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import dev.rejfin.todoit.ui.screens.destinations.GroupsScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.HomeScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.ProfileScreenDestination
import dev.rejfin.todoit.ui.screens.destinations.SettingsScreenDestination

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    @StringRes val label: Int
) {
    Home(HomeScreenDestination, Icons.Default.Home, R.string.home),
    Profile(ProfileScreenDestination, Icons.Default.Person, R.string.profile),
    Groups(GroupsScreenDestination, Icons.Default.Groups, R.string.groups),
    Settings(SettingsScreenDestination, Icons.Default.Settings, R.string.settings)
}