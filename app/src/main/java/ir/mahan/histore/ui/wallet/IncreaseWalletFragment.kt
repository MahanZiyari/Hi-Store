package ir.mahan.histore.ui.wallet

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.profile.BodyUpdateProfile
import ir.mahan.histore.data.model.wallet.BodyIncreaseWallet
import ir.mahan.histore.databinding.FragmentEditProfileBinding
import ir.mahan.histore.databinding.FragmentIncreaseWalletBinding
import ir.mahan.histore.util.extensions.enableLoading
import ir.mahan.histore.util.extensions.openBrowser
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.ProfileViewmodel
import ir.mahan.histore.viewmodel.WalletViewModel
import javax.inject.Inject
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import ir.mahan.histore.util.extensions.toMoneyFormat

@AndroidEntryPoint
class IncreaseWalletFragment : BottomSheetDialogFragment() {
    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    //Binding
    private var _binding: FragmentIncreaseWalletBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel by viewModels<WalletViewModel>()
    // Other
    @Inject
    lateinit var body: BodyIncreaseWallet
    ///////////////////////////////////////////////////////////////////////////
    // user Functions
    ///////////////////////////////////////////////////////////////////////////
    //Theme
    override fun getTheme() = R.style.RemoveDialogBackground

    private fun setupUI() = binding.apply {
        closeImg.setOnClickListener { this@IncreaseWalletFragment.dismiss() }
        //Money separating
        amountEdt.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                amountTxt.text = it.toString().trim().toInt().toMoneyFormat("تومان")
            } else {
                amountTxt.text = ""
            }
        }
        //Click
        submitBtn.setOnClickListener {
            val amount = amountEdt.text.toString()
            if (amount.isNotEmpty()) {
                body.amount = amount
                viewModel.requestIncreaseWallet(body)
            }
        }

    }

    private fun loadScreenData(){
        loadWalletData()
    }

    private fun loadWalletData() = binding.apply {
        viewModel.increaseWalletData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    submitBtn.enableLoading(true)

                }

                is NetworkResult.Success -> {
                    submitBtn.enableLoading(false)
                    result.data?.let { responseIncreaseWallet ->
                        responseIncreaseWallet.startPay!!.toUri().openBrowser(requireContext())
                        this@IncreaseWalletFragment.dismiss()
                    }
                }

                is NetworkResult.Error -> {
                    submitBtn.enableLoading(false)
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentIncreaseWalletBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadScreenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}