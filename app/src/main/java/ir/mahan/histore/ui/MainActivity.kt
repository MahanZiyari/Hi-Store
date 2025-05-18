package ir.mahan.histore.ui

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.databinding.ActivityMainBinding
import ir.mahan.histore.util.constants.DEBUG_TAG
import ir.mahan.histore.util.otp.AppSignatureHelper
import ir.mahan.histore.util.otp.SMSReceiver
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var signatureHelper: AppSignatureHelper
    @Inject
    lateinit var smsReceiver: SMSReceiver

    // properties
    private lateinit var navHost: NavHostFragment
    private var intentFilter: IntentFilter? = null
    var hashcode = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Init nav host
        navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        setUpBottomNavigation()
        // Generate Hashcode
        signatureHelper.appSignatures.forEach {
            hashcode = it
            Timber.tag(DEBUG_TAG).d("Hashcode: $it")
        }
        handlingSMSReceiver()
    }

    private fun setUpBottomNavigation() {
        binding.bottomNav.setupWithNavController(navHost.navController)
        binding.bottomNav.setOnItemReselectedListener { }
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.apply {
                when (destination.id) {
                    R.id.homeFragment -> bottomNav.isVisible = true
                    R.id.cartFragment -> bottomNav.isVisible = true
                    R.id.categoriesFragment -> bottomNav.isVisible = true
                    R.id.profileFragment -> bottomNav.isVisible = true
                    else -> bottomNav.isVisible = false
                }
            }
        }
    }

    // Todo: Remove this function later
    private fun handlingSMSReceiver() {
        intentFilter  = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver.setOnMessageReceived {
            Timber.tag(DEBUG_TAG).d("sms: \n $it")
        }
        val client = SmsRetriever.getClient(this)
        client.startSmsRetriever()
    }

    override fun onResume() {
        Timber.tag(DEBUG_TAG).d("OnResume")
        super.onResume()
        registerReceiver(smsReceiver, intentFilter)
    }

    override fun onStop() {
        Timber.tag(DEBUG_TAG).d("OnStop")
        super.onStop()
        unregisterReceiver(smsReceiver)
    }

    override fun onNavigateUp(): Boolean {
        return navHost.navController.navigateUp() || super.onNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}