package idv.hsu.authenticator

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import idv.hsu.authenticator.databinding.ActivityMainBinding
import idv.hsu.authenticator.feature.splash.SplashFragment
import idv.hsu.authenticator.feature.totplist.TotpFragment

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, TotpFragment.newInstance())
                .commitNow()
        }
    }
}