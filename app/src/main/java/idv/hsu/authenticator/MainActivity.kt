package idv.hsu.authenticator

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.authenticator.presentation.screen.totplist.TotpScreen
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
            Toast.makeText(this, "Scanned: " + result.contents, Toast.LENGTH_LONG)
                .show();
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                var topAppBarTitle by remember { mutableStateOf<String>("Authenticator") }
                var topAppBarActions by remember { mutableStateOf<@Composable (() -> Unit)?>(null) }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = topAppBarTitle) },
                            actions = { topAppBarActions?.invoke() },
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { qrCodeLauncher.launch(ScanOptions()) },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                        }
                    }
                ) { paddingValues ->
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
                        )
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

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    name = "DefaultPreviewDark"
//)

//@Preview(
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    name = "DefaultPreviewLight"
//)

//@Composable
//fun MainPreview() {
//    AppTheme {
//        TotpScreen(
//            onFabClick = {}
//        )
//    }
//}