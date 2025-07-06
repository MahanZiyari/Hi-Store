package ir.mahan.histore.ui.profile.address.add

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.address.BodySubmitAddress
import ir.mahan.histore.databinding.DialogDeleteAddressBinding
import ir.mahan.histore.databinding.FragmentAddAddressBinding
import ir.mahan.histore.databinding.FragmentAddressesBinding
import ir.mahan.histore.ui.profile.address.AddressesAdapter
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.event.Event
import ir.mahan.histore.util.event.EventBus
import ir.mahan.histore.util.extensions.enableLoading
import ir.mahan.histore.util.extensions.setCustomTint
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.extensions.transparentCorners
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.AddressViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddAddressFragment : BaseFragment() {

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    //Binding
    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel by viewModels<AddressViewModel>()
    // Other
    private val args by navArgs<AddAddressFragmentArgs>()
    private val provincesNamesList = mutableListOf<String>()
    private lateinit var provincesAdapter: ArrayAdapter<String>
    private var provinceId = 0
    private val citiesNamesList = mutableListOf<String>()
    private lateinit var citiesAdapter: ArrayAdapter<String>
    private var addressId = 0
    @Inject
    lateinit var body: BodySubmitAddress
    ///////////////////////////////////////////////////////////////////////////
    // user Functions
    ///////////////////////////////////////////////////////////////////////////

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() = binding.apply {
        initToolbar()
        // Submit Address
        submitBtn.setOnClickListener {
            fillBody()
            if (isNetworkAvailable)
                viewModel.submitAddress(body)
        }
    }

    private fun initToolbar() = binding.toolbar.apply {
        // Back
        toolbarBackImg.setOnClickListener { findNavController().popBackStack() }
        if (args.addressItem != null) {
            toolbarTitleTxt.text = getString(R.string.editAddress)
            toolbarOptionImg.apply {
                setImageResource(R.drawable.trash_can)
                setCustomTint(R.color.red)
                setOnClickListener { showDeleteAddressDialog() }
            }
            fillViews()
        } else {
            toolbarTitleTxt.text = getString(R.string.addNewAddress)
            toolbarOptionImg.isVisible = false
        }

    }

    private fun fillBody() = binding.apply {
        body.receiverFirstname = nameEdt.text.toString()
        body.receiverLastname = familyEdt.text.toString()
        body.receiverCellphone = phoneEdt.text.toString()
        body.floor = floorEdt.text.toString()
        body.plateNumber = plateEdt.text.toString()
        body.postalCode = postalEdt.text.toString()
        body.postalAddress = addressEdt.text.toString()
    }

    private fun fillViews() = binding.apply {
        //Set data
        args.addressItem?.apply {
            addressId = id!!
            body.addressId = id.toString()
            nameEdt.setText(receiverFirstname)
            familyEdt.setText(receiverLastname)
            phoneEdt.setText(receiverCellphone)
            body.provinceId = province?.id.toString()
            provinceAutoTxt.setText(province?.title)
            cityInpLay.isVisible = true
            body.cityId = cityId
            cityAutoTxt.setText(city?.title)
            floorEdt.setText(floor)
            plateEdt.setText(plateNumber)
            postalEdt.setText(postalCode)
            addressEdt.setText(postalAddress)
        }
    }

    private fun showDeleteAddressDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogDeleteAddressBinding.inflate(layoutInflater)
        dialog.transparentCorners()
        dialog.setContentView(dialogBinding.root)

        dialogBinding.noBtn.setOnClickListener { dialog.dismiss() }

        dialogBinding.yesBtn.setOnClickListener {
            dialog.dismiss()
            if (isNetworkAvailable)
                viewModel.deleteAddress(addressId)
        }

        dialog.show()
    }


    private fun loadScreenData(){
        requestForProvinces()
        loadProvincesData()
        loadCitiesData()
        observeSubmitAddressResult()
        observeDeleteAddressResult()
    }

    private fun requestForProvinces() {
        if (isNetworkAvailable)
            viewModel.getProvinceList()
    }
    private fun requestForCities(provinceId: Int) {
        if (isNetworkAvailable)
            viewModel.getCitiesList(provinceId)
    }

    private fun loadProvincesData() {
        binding.apply {
            viewModel.provinceListLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Loading -> {}

                    is NetworkResult.Success -> {
                        response.data?.let { responseProvinceList ->
                            if (responseProvinceList.isNotEmpty()) {
                                responseProvinceList.forEach {
                                    provincesNamesList.add(it.title!!)
                                }
                                provincesAdapter = ArrayAdapter<String>(
                                    requireContext(), R.layout.dropdown_menu_popup_item, provincesNamesList
                                )
                                provinceAutoTxt.apply {
                                    setAdapter(provincesAdapter)
                                    setOnItemClickListener { _, _, position, _ ->
                                        provinceId = responseProvinceList[position].id!!
                                        body.provinceId = provinceId.toString()
                                        requestForCities(provinceId)
                                    }
                                }
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        root.showSnackBar(response.error!!)
                    }
                }
            }
        }
    }

    private fun loadCitiesData() {
        binding.apply {
            viewModel.cityListLiveData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Loading -> {}

                    is NetworkResult.Success -> {
                        response.data?.let { responseCitiesList ->
                            cityInpLay.isVisible = true
                            if (responseCitiesList.isNotEmpty()) {
                                citiesNamesList.clear()
                                responseCitiesList.forEach {
                                    citiesNamesList.add(it.title!!)
                                }
                                citiesAdapter = ArrayAdapter<String>(
                                    requireContext(), R.layout.dropdown_menu_popup_item, citiesNamesList
                                )
                                cityAutoTxt.apply {
                                    setAdapter(citiesAdapter)
                                    setOnItemClickListener { _, _, position, _ ->
                                        body.cityId = responseCitiesList[position].id!!.toString()
                                    }
                                }
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        root.showSnackBar(response.error!!)
                    }
                }
            }
        }
    }

    private fun observeSubmitAddressResult() {
        binding.apply {
            viewModel.submitAddressData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        submitBtn.enableLoading(true)
                    }

                    is NetworkResult.Success -> {
                        submitBtn.enableLoading(false)
                        response.data?.let {
                            lifecycleScope.launch { EventBus.publish(Event.IsUpdateAddress) }
                            findNavController().popBackStack()
                        }
                    }

                    is NetworkResult.Error -> {
                        submitBtn.enableLoading(false)
                        root.showSnackBar(response.error!!)
                    }
                }
            }
        }
    }

    private fun observeDeleteAddressResult() {
        binding.apply {
            viewModel.deleteAddressResult.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkResult.Loading -> {
                        submitBtn.enableLoading(true)
                    }

                    is NetworkResult.Success -> {
                        submitBtn.enableLoading(false)
                        response.data?.let {
                            lifecycleScope.launch { EventBus.publish(Event.IsUpdateAddress) }
                            findNavController().popBackStack()
                        }
                    }

                    is NetworkResult.Error -> {
                        submitBtn.enableLoading(false)
                        root.showSnackBar(response.error!!)
                    }
                }
            }
        }
    }



    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddAddressBinding.inflate(layoutInflater)
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