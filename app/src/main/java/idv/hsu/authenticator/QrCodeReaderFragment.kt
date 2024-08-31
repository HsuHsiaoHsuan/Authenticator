package idv.hsu.authenticator

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import idv.hsu.authenticator.databinding.FragmentQrCodeReaderBinding
import timber.log.Timber

class QrCodeReaderFragment : Fragment() {

    private var _binding: FragmentQrCodeReaderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QrCodeReaderViewModel by viewModels()

    //    private val qrCodeLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        val intentResult = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
//        if (intentResult != null) {
//            if (intentResult.contents == null) {
//                Toast.makeText(context, "Cancelled", Toast.LENGTH_LONG).show()
//            } else {
//                val qrCodeData = intentResult.contents
////                handleQRCodeData(qrCodeData)
//            }
//        }
//    }
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
    }

    private fun handleQRCodeData(qrCodeData: String) {
        Timber.d( "QR Code Data: $qrCodeData")
        if (qrCodeData.startsWith("otpauth://totp/")) {
            val uri = Uri.parse(qrCodeData)
            val accountName = uri.path?.substring(1) // 去掉前面的 "/"
            val secret = uri.getQueryParameter("secret")
            val issuer = uri.getQueryParameter("issuer")

            if (secret != null && accountName != null) {
                Timber.d("accountName: $accountName")
                Timber.d("secret: $secret")
                Timber.d("issuer: $issuer")
            } else {
                Toast.makeText(context, "Invalid QR Code", Toast.LENGTH_LONG).show()
            }
            saveTOTPAccount()
        } else {
            Toast.makeText(context, "Unsupported QR Code format", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        fun newInstance() = QrCodeReaderFragment()
    }
}