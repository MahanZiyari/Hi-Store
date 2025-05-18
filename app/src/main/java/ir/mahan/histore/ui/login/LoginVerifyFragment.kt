package ir.mahan.histore.ui.login

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.IntentFilter
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.goodiebag.pinview.Pinview
import com.google.android.gms.auth.api.phone.SmsRetriever
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.datastore.SessionManager
import ir.mahan.histore.data.model.login.BodyLogin
import ir.mahan.histore.databinding.FragmentLoginVerifyBinding
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.constants.DEBUG_TAG
import ir.mahan.histore.util.extensions.enableLoading
import ir.mahan.histore.util.extensions.hideKeyboard
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.util.otp.SMSReceiver
import ir.mahan.histore.viewmodel.LoginViewmodel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


/**
 * Login verify fragment
 * @constructor Create empty Login verify fragment
 */
@AndroidEntryPoint
class LoginVerifyFragment : BaseFragment() {

    //Binding
    private var _binding: FragmentLoginVerifyBinding? = null
    private val binding get() = _binding!!


    @Inject
    lateinit var smsReceiver: SMSReceiver

    @Inject
    lateinit var body: BodyLogin

    @Inject
    lateinit var sessionManager: SessionManager

    //Other
    private val viewModel by viewModels<LoginViewmodel>()
    private val args by navArgs<LoginVerifyFragmentArgs>()
    private var intentFilter: IntentFilter? = null

    // Viewmodel
    private val viewmodel: LoginViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginVerifyBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBroadcast()
        smsListener()
        args?.let {
            body.login = it.phoneNumber
        }
        //InitViews
        binding.apply {
            //Bottom image
            bottomImg.load(R.drawable.bg_circle)
            //Customize pin view text color
            pinView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            //Complete code
            pinView.setPinViewEventListener(object : Pinview.PinViewEventListener {
                override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {
                    body.code = pinview?.value?.toInt()
                    if (isNetworkAvailable)
                        viewModel.verifyPhoneNumber(body)
                }
            })
            //Send again code
            sendAgainBtn.setOnClickListener {
                if (isNetworkAvailable)
                    viewModel.requestToLoginUser(body)
                handleTimer().cancel()
            }
        }
        //Start timer
        lifecycleScope.launch {
            delay(500)
            handleTimer().start()
        }
        //Load data
        handleAnimation()
        loadLoginData()
        loadVerifyData()
    }

    private fun loadLoginData() {
        binding.apply {
            viewModel.loginData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        sendAgainBtn.enableLoading(true)
                    }

                    is NetworkResult.Success -> {
                        sendAgainBtn.enableLoading(false)
                        result.data?.let {
                            handleTimer().start()
                        }
                    }

                    is NetworkResult.Error -> {
                        sendAgainBtn.enableLoading(false)
                        root.showSnackBar(result.error!!)
                    }
                }
            }
        }
    }

    private fun loadVerifyData() {
        binding.apply {
            viewmodel.verifyData.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        Timber.tag(DEBUG_TAG).d("Loading")
                        timerLay.alpha = 0.3f
                    }

                    is NetworkResult.Success -> {
                        timerLay.alpha = 1f
                        result.data?.let {
                            lifecycleScope.launch {
                                Timber.tag(DEBUG_TAG).d("${it.accessToken}")
                                sessionManager.saveToken(it.accessToken.toString())
                            }
                            root.hideKeyboard()
                            findNavController().popBackStack(R.id.loginVerifyFragment, true)
                            findNavController().popBackStack(R.id.loginFragment, true)
                            //Home
                            findNavController().navigate(R.id.actionToMain)
                        }
                    }

                    is NetworkResult.Error -> {
                        timerLay.alpha = 1f
                        Timber.tag(DEBUG_TAG).d("Error: ${result.error}")
                        root.showSnackBar(result.error!!)
                    }
                }
            }
        }
    }

    private fun handleTimer(): CountDownTimer {
        binding.apply {
            return object : CountDownTimer(60_000, 1_000) {
                @SuppressLint("SetTextI18n")
                override fun onTick(millis: Long) {
                    sendAgainBtn.isVisible = false
                    timerTxt.isVisible = true
                    timerProgress.isVisible = true
                    timerTxt.text = "${millis / 1000} ${getString(R.string.second)}"
                    timerProgress.setProgressCompat((millis / 1000).toInt(), true)
                    if (millis < 1000)
                        timerProgress.progress = 0
                }

                override fun onFinish() {
                    sendAgainBtn.isVisible = true
                    timerTxt.isVisible = false
                    timerProgress.isVisible = false
                    timerProgress.progress = 0
                }
            }
        }
    }

    private fun handleAnimation() {
        binding.animationView.apply {
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    lifecycleScope.launch {
                        delay(2000)
                        playAnimation()
                    }
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun initBroadcast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver.setOnMessageReceived {
            val code = it.split(":")[1].trim().take(4)
            binding.pinView.value = code.toString()
        }
    }

    private fun smsListener() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsRetriever()
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(smsReceiver, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        //handleTimer().cancel()
        requireContext().unregisterReceiver(smsReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}