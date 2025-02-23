package idv.hsu.authenticator

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.authenticator.presentation.screen.splash.SplashScreen
import idv.hsu.authenticator.presentation.screen.totplist.TotpScreen
import idv.hsu.authenticator.presentation.screen.tutorial.TutorialScreen
import idv.hsu.authenticator.presentation.utils.PreferencesManager
import idv.hsu.authenticator.presentation.viewmodel.MainIntent
import idv.hsu.authenticator.presentation.viewmodel.MainViewModel
import idv.hsu.authenticator.ui.theme.AppTheme
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val qrCodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
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
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
                var topAppBarTitle by remember { mutableStateOf<String>("Authenticator") }
                var topAppBarActions by remember { mutableStateOf<@Composable (() -> Unit)?>(null) }

                when (currentScreen) {
                    Screen.Splash ->
                        SplashScreen {
                            currentScreen = if (PreferencesManager.isFirstTimeLaunch(this)) {
                                Screen.Tutorial
                            } else {
                                Screen.Main(isEmpty = true)
                            }
                        }

                    Screen.Tutorial -> TutorialScreen {
                        PreferencesManager.setFirstTimeLaunch(this, false)
                        currentScreen = Screen.Main(isEmpty = true)
                    }
                    is Screen.Main -> {
                        Scaffold(
                            topBar = {
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
                            },
                            floatingActionButton = {
                                if ((currentScreen as? Screen.Main)?.isEmpty == false) {
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
                            Timber.d("paddingValues: $paddingValues")
                            Timber.d("paddingValues #1-1: ${paddingValues.calculateBottomPadding()}")

                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background,
                                tonalElevation = 5.dp
                            ) {
                                TotpScreen(
                                    modifier = Modifier.padding(paddingValues),
                                    onUpdateTopAppBar = { title, actions ->
                                        topAppBarTitle = title
                                        topAppBarActions = actions
                                    },
                                    onEmptyStateChanged = { isEmptyList ->
                                        currentScreen = Screen.Main(isEmptyList)
                                    },
                                    onStartNowAction = { qrCodeLauncher.launch(ScanOptions()) }
                                )
                            }
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

sealed class Screen {
    data object Splash : Screen()
    data object Tutorial : Screen()
    data class Main(val isEmpty: Boolean = true) : Screen()
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun MainPreview() {
    val fakePaddingValues = PaddingValues(16.dp)
    Surface {
        TotpScreen(
            modifier = Modifier.padding(fakePaddingValues),
            onUpdateTopAppBar = { _, _ -> },
            onEmptyStateChanged = { },
            onStartNowAction = { }
        )
    }
}