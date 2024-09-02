package idv.hsu.authenticator

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.authenticator.databinding.FragmentQrCodeReaderBinding
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class QrCodeReaderFragment : Fragment() {

    private var _binding: FragmentQrCodeReaderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QrCodeReaderViewModel by viewModels()

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
        _binding = FragmentQrCodeReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonScan.setOnClickListener {
            qrCodeLauncher.launch(ScanOptions())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow.collect { uiState ->
                    when (uiState) {
                        is QrCodeReaderUiState.Idle -> {
                        }

                        is QrCodeReaderUiState.Loading -> {
                        }

                        is QrCodeReaderUiState.SaveTOPTDataSuccess -> {
                            Toast.makeText(requireContext(), "Save Success", Toast.LENGTH_LONG)
                                .show()
                        }

                        is QrCodeReaderUiState.SaveTOPTDataFailed -> {
                            Toast.makeText(
                                requireContext(),
                                uiState.errorMessage,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleQRCodeData(qrCodeData: String) {
        Timber.d("QR Code Data: $qrCodeData")
        viewModel.onIntent(QrCodeReaderIntent.SaveTOPTAccount(qrCodeData))
//        if (qrCodeData.startsWith("otpauth://totp/")) {
//            val uri = Uri.parse(qrCodeData)
//            val accountName = uri.path?.substring(1) // 去掉前面的 "/"
//            val secret = uri.getQueryParameter("secret")
//            val issuer = uri.getQueryParameter("issuer")
//
//            if (secret != null && accountName != null) {
//                Timber.d("accountName: $accountName")
//                Timber.d("secret: $secret")
//                Timber.d("issuer: $issuer")
//            } else {
//                Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_LONG).show()
//            }
//        } else {
//            Toast.makeText(context, "Unsupported QR Code format", Toast.LENGTH_LONG).show()
//        }
    }

    companion object {
        fun newInstance() = QrCodeReaderFragment()
    }
}