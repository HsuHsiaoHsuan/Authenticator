package idv.hsu.authenticator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.authenticator.presentation.screen.splash.SplashScreen
import idv.hsu.authenticator.presentation.screen.totplist.TotpScreen
import idv.hsu.authenticator.presentation.screen.tutorial.TutorialScreen
import idv.hsu.authenticator.presentation.viewmodel.MainIntent
import idv.hsu.authenticator.presentation.viewmodel.MainViewModel
import idv.hsu.authenticator.ui.theme.AppTheme
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val qrCodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            handleQRCodeData(result.contents)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                val scope = rememberCoroutineScope()
                var needShowFloatActionButton by remember { mutableStateOf(false) }
                var topAppBarTitle by remember { mutableStateOf<String>("Authenticator") }
                var topAppBarActions by remember { mutableStateOf<@Composable (() -> Unit)?>(null) }
                val isFirstTimeOpen by viewModel.isFirstTime.collectAsStateWithLifecycle()

                Scaffold(
                    topBar =
                        {
                            if (currentRoute != Screen.Splash.route) {
                                TopAppBar(
                                    title = { Text(text = topAppBarTitle) },
                                    actions = { topAppBarActions?.invoke() },
                                    colors = TopAppBarColors(
                                        containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                                        scrolledContainerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                                        navigationIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primaryContainer else Color.White,
                                        titleContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black,
                                        actionIconContentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onPrimaryContainer else Color.Black
                                    )
                                )
                            }
                        },
                    floatingActionButton = {
                        if (needShowFloatActionButton && currentRoute == Screen.Totp.route) {
                            FloatingActionButton(
                                onClick = { qrCodeLauncher.launch(ScanOptions()) },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                            }
                        }
                    },
                ) { paddingValues ->
                    NavHost(navController = navController, startDestination = Screen.Splash.route) {
                        composable(Screen.Splash.route) {
                            SplashScreen(navController = navController, isFirstTime = isFirstTimeOpen)
                        }
                        composable(Screen.Tutorial.route) {
                            TutorialScreen(
                                modifier = Modifier.padding(paddingValues),
                                navController = navController,
                                onNavigateToNext = {
                                    scope.launch {
                                        viewModel.markFirstTimeDone()
                                    }
                                })
                        }
                        composable(Screen.Totp.route) {
                            TotpScreen(
                                modifier = Modifier.padding(paddingValues),
                                onUpdateTopAppBar = { title, actions -> },
                                needShowFloatActionButton = { value ->
                                    needShowFloatActionButton = value
                                },
                                onStartNowAction = { qrCodeLauncher.launch(ScanOptions()) },
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleQRCodeData(qrCodeData: String) {
        Timber.d("QR Code Data: $qrCodeData")
        viewModel.onIntent(MainIntent.SaveTotpAccount(qrCodeData))
    }
}

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Tutorial : Screen("tutorial")
    object Totp : Screen("totp")
}