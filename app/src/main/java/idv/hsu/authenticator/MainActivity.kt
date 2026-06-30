package idv.hsu.authenticator

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import idv.hsu.authenticator.presentation.screen.splash.SplashScreen
import idv.hsu.authenticator.presentation.screen.totplist.TotpListScreen
import idv.hsu.authenticator.presentation.screen.tutorial.TutorialScreen
import idv.hsu.authenticator.presentation.viewmodel.MainIntent
import idv.hsu.authenticator.presentation.viewmodel.MainViewModel
import idv.hsu.authenticator.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    private val qrCodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            handleQRCodeData(result.contents)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = SplashRoute.route) {
                    composable(SplashRoute.route) {
                        SplashScreen(navController = navController)
                    }
                    composable(TutorialRoute.route) {
                        TutorialScreen(
                            navController = navController
                        )
                    }
                    composable(
                        route = TotpListRoute.route,
                    ) {
                        TotpListScreen(
                            scanQrCodeAction = { qrCodeLauncher.launch(ScanOptions()) },
                            navController = navController
                        )
                    }
                }
            }
        }
    }

    private fun handleQRCodeData(qrCodeData: String) {
        Timber.d("QR code scanned")
        viewModel.onIntent(MainIntent.SaveTotpAccount(qrCodeData))
    }
}
