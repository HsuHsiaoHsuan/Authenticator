package idv.hsu.authenticator.presentation.screen.totplist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class TotpFragment : Fragment() {

    private val viewModel: TotpViewModel by viewModels()

    private val qrCodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            handleQRCodeData(result.contents)
            Toast.makeText(requireActivity(), "Scanned: " + result.contents, Toast.LENGTH_LONG)
                .show();
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                TotpScreen(
                    onFabClick = {
                        qrCodeLauncher.launch(ScanOptions())
                    }
                )
            }
        }
    }

    private fun handleQRCodeData(qrCodeData: String) {
        Timber.d("QR Code Data: $qrCodeData")
        viewModel.onIntent(TotpIntent.SaveTOTPAccount(qrCodeData))
    }

    companion object {
        fun newInstance() = TotpFragment()
    }
}