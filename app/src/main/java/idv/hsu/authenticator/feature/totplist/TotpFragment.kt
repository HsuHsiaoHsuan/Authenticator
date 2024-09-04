package idv.hsu.authenticator.feature.totplist

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
import idv.hsu.authenticator.R
import idv.hsu.authenticator.data.local.TOTPAccount
import idv.hsu.authenticator.databinding.FragmentTotpBinding
import idv.hsu.authenticator.widget.MarginItemDecoration
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class TotpFragment : Fragment() {

    private var _binding: FragmentTotpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TotpViewModel by viewModels()

    private val itemList = mutableListOf<TOTPAccount>()

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
        _binding = FragmentTotpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TotpAdapter() { _ ->
            // TODO: handle item click
        }

        binding.recyclerView.apply {
            addItemDecoration(MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.card_view_margin)))
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            this.adapter = adapter
        }

        binding.fab.setOnClickListener {
            qrCodeLauncher.launch(ScanOptions())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateFlow.collect { uiState ->
                    Timber.d("uiState: $uiState")
                    when (uiState) {
                        is TotpUiState.Idle -> Unit
                        is TotpUiState.Loading -> Unit // TODO
                        is TotpUiState.SaveTOTPAccountFailed -> TODO()
                        is TotpUiState.ShowTOTPAccounts -> {
                            adapter.submitList(uiState.accounts)
                            with(itemList) {
                                clear()
                                addAll(uiState.accounts)
                            }
                        }

                        is TotpUiState.SaveTOTPAccountSuccess,
                        is TotpUiState.DeleteTOTPAccountFailed,
                        is TotpUiState.DeleteTOTPAccountSuccess -> {
                            viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
                        }
                    }
                }
            }
        }

        viewModel.onIntent(TotpIntent.LoadTOTPAccounts)
    }

    private fun handleQRCodeData(qrCodeData: String) {
        Timber.d("QR Code Data: $qrCodeData")
        viewModel.onIntent(TotpIntent.SaveTOTPAccount(qrCodeData))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = TotpFragment()
    }
}