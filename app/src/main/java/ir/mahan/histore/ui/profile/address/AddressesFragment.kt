package ir.mahan.histore.ui.profile.address

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.address.ResponseProfileAddresses
import ir.mahan.histore.data.model.address.ResponseProfileAddresses.ResponseProfileAddressesItem
import ir.mahan.histore.data.model.profile.favorites.ResponseProfileFavorites
import ir.mahan.histore.databinding.FragmentAddressesBinding
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.event.Event
import ir.mahan.histore.util.event.EventBus
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.AddressViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddressesFragment : BaseFragment() {

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    //Binding
    private var _binding: FragmentAddressesBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel by viewModels<AddressViewModel>()
    // Other
    @Inject
    lateinit var addressesAdapter: AddressesAdapter
    ///////////////////////////////////////////////////////////////////////////
    // user Functions
    ///////////////////////////////////////////////////////////////////////////

    private fun requestForAddresses() {
        if (isNetworkAvailable)
            viewModel.getUserAddresses()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() = binding.apply {
        initToolbar()
    }

    private fun initToolbar() = binding.toolbar.apply {
        toolbarTitleTxt.text = getString(R.string.yourAddresses)
        toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
        //Add
        toolbarOptionImg.apply {
            setImageResource(R.drawable.location_plus)
            setOnClickListener {
                findNavController().navigate(R.id.actionToAddAddress)
            }
        }
    }



    private fun loadScreenData(){
        lifecycleScope.launch {
            EventBus.observe<Event.IsUpdateAddress> {
                requestForAddresses()
            }
        }
        observeUserAddresses()
//        observeDeleteFavoriteResult()
    }

    private fun observeUserAddresses() = binding.apply {
        viewModel.userAddressesLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    addressList.showShimmer()
                }

                is NetworkResult.Success -> {
                    addressList.hideShimmer()
                    result.data?.let {
                        if (it.isNotEmpty()) {
                            initRecycler(it)
                        } else {
                            emptyLay.isVisible = true
                            addressList.isVisible = false
                        }
                    }
                }

                is NetworkResult.Error -> {
                    addressList.hideShimmer()
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun observeDeleteFavoriteResult() = binding.apply {
        viewModel.provinceListLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {}

                is NetworkResult.Success -> {
                    result.data?.let {
                        //requestForAddresses()
                    }
                }

                is NetworkResult.Error -> {
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun initRecycler(list: List<ResponseProfileAddressesItem>) {
        binding.apply {
            addressesAdapter.setData(list)
            addressList.setupRecyclerview(LinearLayoutManager(requireContext()), addressesAdapter)
            //Click
            addressesAdapter.setOnItemClickListener {
                val action = AddressesFragmentDirections.actionToAddAddress().setAddressItem(it)
                findNavController().navigate(action)
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddressesBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestForAddresses()
        setupUI()
        loadScreenData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}