package idv.hsu.authenticator.presentation.screen.qrcodereader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import idv.hsu.authenticator.databinding.FragmentQrCodeReaderBinding
import idv.hsu.authenticator.presentation.viewmodel.QrCodeReaderIntent
import idv.hsu.authenticator.presentation.viewmodel.QrCodeReaderUiState
import idv.hsu.authenticator.presentation.viewmodel.QrCodeReaderViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class QrCodeReaderFragment : Fragment() {

    private var _binding: FragmentQrCodeReaderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QrCodeReaderViewModel by viewModel()

    private val qrCodeLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(
        ScanContract()
    ) { result ->
        if (result.contents == null) {
            Toast.makeText(requireActivity(), "Cancelled", Toast.LENGTH_LONG).show();
        } else {
            handleQRCodeData(result.contents)
//            Toast.makeText(requireActivity(), "Scanned: " + result.contents, Toast.LENGTH_LONG)
//                .show();
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
        viewModel.onIntent(QrCodeReaderIntent.SaveTOTPAccount(qrCodeData))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = QrCodeReaderFragment()
    }
}