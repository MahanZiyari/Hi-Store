package ir.mahan.histore.ui.profile.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.profile.order.ResponseProfileOrdersList
import ir.mahan.histore.databinding.FragmentAddAddressBinding
import ir.mahan.histore.databinding.FragmentProfileOrdersBinding
import ir.mahan.histore.ui.profile.address.add.AddAddressFragmentArgs
import ir.mahan.histore.ui.profile.orders.adapters.OrdersAdapter
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.constants.CANCELED
import ir.mahan.histore.util.constants.DELIVERED
import ir.mahan.histore.util.constants.PENDING
import ir.mahan.histore.util.extensions.setCustomTint
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.AddressViewModel
import ir.mahan.histore.viewmodel.ProfileOrdersViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ProfileOrdersFragment : BaseFragment() {

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    //Binding
    private var _binding: FragmentProfileOrdersBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel by viewModels<ProfileOrdersViewModel>()
    // Args
    private val args by navArgs<ProfileOrdersFragmentArgs>()
    private var status = ""

    @Inject
    lateinit var ordersAdapter: OrdersAdapter
    ///////////////////////////////////////////////////////////////////////////
    // UI Functions
    ///////////////////////////////////////////////////////////////////////////

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() = binding.apply {
        //Args
        args.orderStatus.let {
            status = it
        }
        initToolbar()
    }

    private fun loadScreenData() {
        if (isNetworkAvailable)
            viewModel.getUserOrders(status)
        loadOrdersData()
    }

    private fun initToolbar() = binding.toolbar.apply {
        toolbarTitleTxt.text = when (status) {
            DELIVERED -> getString(R.string.delivered)
            PENDING -> getString(R.string.pending)
            CANCELED -> getString(R.string.canceled)
            else -> ""
        }
        toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
        toolbarOptionImg.isVisible = false
    }

    private fun loadOrdersData() {
        binding.apply {
            viewModel.userOrdersLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        ordersList.showShimmer()
                    }

                    is NetworkResult.Success -> {
                        ordersList.hideShimmer()
                        response.data?.let { responseProfileOrders ->
                            if (responseProfileOrders.data.isNotEmpty()) {
                                initRecycler(responseProfileOrders.data)
                            } else {
                                emptyLay.isVisible = true
                                ordersList.isVisible = false
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        ordersList.hideShimmer()
                        root.showSnackBar(response.error!!)
                    }
                }
            }
        }
    }

    private fun initRecycler(data: List<ResponseProfileOrdersList.Data>) {
        binding.apply {
            ordersAdapter.setData(data)
            ordersList.setupRecyclerview(LinearLayoutManager(requireContext()), ordersAdapter)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileOrdersBinding.inflate(layoutInflater)
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