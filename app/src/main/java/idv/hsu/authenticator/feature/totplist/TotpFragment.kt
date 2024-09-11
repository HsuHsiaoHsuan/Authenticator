package idv.hsu.authenticator.feature.totplist

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.authenticator.R
import idv.hsu.authenticator.databinding.FragmentTotpBinding
import idv.hsu.authenticator.model.TotpDataItem
import idv.hsu.authenticator.widget.MarginItemDecoration
import java.util.Collections.addAll
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class TotpFragment : Fragment() {

//    private var _binding: FragmentTotpBinding? = null
//    private val binding get() = _binding!!

    private val viewModel: TotpViewModel by viewModels()

    private var itemList = listOf<TotpDataItem>()
    private val handler = Handler(Looper.getMainLooper())

    private var isCounting = true
    private val totpLifecycle = 30L
    private val remainingSeconds: Long
        get() {
            val currentTime = System.currentTimeMillis() / 1000
            return totpLifecycle - currentTime % totpLifecycle
        }
    private val countdownRunnable = object : Runnable {
        override fun run() {
            if (isCounting) {
//                updateCountdowns()  // 每秒更新倒计时
                Timber.d("Countdown in Fragment: $remainingSeconds")
                handler.postDelayed(this, 1000)
            }
        }
    }

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
//        _binding = FragmentTotpBinding.inflate(inflater, container, false)
//        return binding.root
        return ComposeView(requireContext()).apply {
            setContent {
                TotpScreen(
//                    viewModel = viewModel,
                    onFabClick = {
                        qrCodeLauncher.launch(ScanOptions())
                    }
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val adapter = TotpAdapter() { _ ->
//            // TODO: handle item click
//        }
//
//        binding.recyclerView.apply {
//            addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.card_view_margin)))
//            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
//            this.adapter = adapter
//        }
//
//        binding.fab.setOnClickListener {
//            qrCodeLauncher.launch(ScanOptions())
//        }
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.uiStateFlow.collect { uiState ->
//                    Timber.d("uiState: $uiState")
//                    when (uiState) {
//                        is TotpUiState.Idle -> Unit
//                        is TotpUiState.Loading -> Unit // TODO
//                        is TotpUiState.SaveTOTPAccountFailed -> TODO()
//                        is TotpUiState.ShowTOTPAccounts -> {
//                            val tmpRemainingSeconds = remainingSeconds
//                            val tmpItemList = uiState.accounts.map { account ->
//                                TotpDataItem(
//                                    account.id,
//                                    account.accountName,
//                                    account.secret,
//                                    account.issuer,
//                                    tmpRemainingSeconds
//                                )
//                            }
//                            adapter.submitList(tmpItemList)
//                            itemList = tmpItemList
//                        }
//
//                        is TotpUiState.SaveTOTPAccountSuccess,
//                        is TotpUiState.DeleteTOTPAccountFailed,
//                        is TotpUiState.DeleteTOTPAccountSuccess -> {
//                            viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
//                        }
//                    }
//                }
//            }
//        }
//
//        viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
//        handler.post(countdownRunnable)
    }

    private fun handleQRCodeData(qrCodeData: String) {
        Timber.d("QR Code Data: $qrCodeData")
        viewModel.onIntent(TotpIntent.SaveTOTPAccount(qrCodeData))
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        isCounting = false
//        handler.removeCallbacks(countdownRunnable)
//        _binding = null
    }

    companion object {
        fun newInstance() = TotpFragment()
    }
}