package idv.hsu.authenticator.presentation.screen.totplist

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import idv.hsu.authenticator.databinding.ItemTotpAccountBinding
import idv.hsu.authenticator.data.entities.TotpDataItem
import idv.hsu.authenticator.presentation.utils.generateTOTPWithTime

class TotpAdapter(private val onClick: (TotpDataItem) -> Unit) :
    ListAdapter<TotpDataItem, TotpAdapter.TotpViewHolder>(TotpDiffCallback) {

    inner class TotpViewHolder(
        private val binding: ItemTotpAccountBinding,
        val onClick: (TotpDataItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        var countdownTimer: CountDownTimer? = null

        fun bind(data: TotpDataItem) {
            binding.root.setOnClickListener {
                onClick(data)
            }
            binding.textAccountIssuer.text = data.issuer ?: ""
            binding.textAccountName.text = data.accountName
            val (a, b) = generateTOTPWithTime(data.secret, System.currentTimeMillis() / 1000)
            binding.textPasscode.text = a

            countdownTimer?.cancel()
            countdownTimer = object : CountDownTimer(b * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.progressBar.progress = (millisUntilFinished / 1000).toInt()
                }

                override fun onFinish() {
                    notifyItemChanged(adapterPosition)
                }
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotpViewHolder {
        val binding =
            ItemTotpAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TotpViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: TotpViewHolder, position: Int) {
        val totpAccount = getItem(position)
        holder.bind(totpAccount)
    }

    override fun onViewRecycled(holder: TotpViewHolder) {
        super.onViewRecycled(holder)
        holder.countdownTimer?.cancel()
    }
}

object TotpDiffCallback : DiffUtil.ItemCallback<TotpDataItem>() {
    override fun areItemsTheSame(oldItem: TotpDataItem, newItem: TotpDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TotpDataItem, newItem: TotpDataItem): Boolean {
        return oldItem.issuer == newItem.issuer &&
                oldItem.accountName == newItem.accountName &&
                oldItem.secret == newItem.secret
    }
}