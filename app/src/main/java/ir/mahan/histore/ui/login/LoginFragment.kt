package ir.mahan.histore.ui.login

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.datastore.SessionManager
import ir.mahan.histore.data.model.login.BodyLogin
import ir.mahan.histore.databinding.FragmentLoginBinding
import ir.mahan.histore.ui.MainActivity
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.constants.IS_CALLED_VERIFY
import ir.mahan.histore.util.extensions.enableLoading
import ir.mahan.histore.util.extensions.hideKeyboard
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.LoginViewmodel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : BaseFragment() {
    //Binding
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var body: BodyLogin


    // Viewmodel
    private val viewmodel: LoginViewmodel by viewModels()

    // Other
    private val parentActivity by lazy { (activity as MainActivity) }
    private var phone = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        IS_CALLED_VERIFY = false
        //InitViews
        binding.apply {
            //Bottom image
            bottomImg.load(R.drawable.bg_circle)
            //Get hash code
            body.hashCode = parentActivity.hashcode
            //Click
            sendPhoneBtn.setOnClickListener {
                root.hideKeyboard()
                sendPhoneBtn.enableLoading(true)
                //Phone
                phone = phoneEdt.text.toString()
                if (phone.length == 11 && isNetworkAvailable) {
                    body.login = phone
                    IS_CALLED_VERIFY = true
                    viewmodel.requestToLoginUser(body)
                }
            }
        }
        //Load data
        loadLoginData()
        handleAnimation()
    }

    private fun loadLoginData() {
        binding.apply {
            viewmodel.loginData.observe(viewLifecycleOwner) { result->
                when(result) {
                    is NetworkResult.Loading -> {
                        sendPhoneBtn.enableLoading(true)
                    }
                    is NetworkResult.Success -> {
                        sendPhoneBtn.enableLoading(false)
                        if (result.data != null) {
                            if (IS_CALLED_VERIFY) {
                                val  direction = LoginFragmentDirections.actionLoginToVerify(phone)
                                findNavController().navigate(direction)
                            }
                        }
                    }
                    is NetworkResult.Error -> {
                        sendPhoneBtn.enableLoading(false)
                        root.showSnackBar(result.error!!)
                    }
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}