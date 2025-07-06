package ir.mahan.histore.ui.profile.edit

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.hamsaa.persiandatepicker.PersianDatePickerDialog
import ir.hamsaa.persiandatepicker.api.PersianPickerDate
import ir.hamsaa.persiandatepicker.api.PersianPickerListener
import ir.mahan.histore.R
import ir.mahan.histore.data.model.profile.BodyUpdateProfile
import ir.mahan.histore.databinding.FragmentEditProfileBinding
import ir.mahan.histore.util.event.Event
import ir.mahan.histore.util.event.EventBus
import ir.mahan.histore.util.extensions.enableLoading
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.ProfileViewmodel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : BottomSheetDialogFragment() {
    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    //Binding
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    // View Model
    private val viewModel by viewModels<ProfileViewmodel>()
    // Other
    @Inject
    lateinit var body: BodyUpdateProfile
    ///////////////////////////////////////////////////////////////////////////
    // user Functions
    ///////////////////////////////////////////////////////////////////////////

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() = binding.apply {
        closeImg.setOnClickListener { this@EditProfileFragment.dismiss() }
        //Open date picker
        birthDateEdt.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                openDatePicker()
            }
            false
        }
        //Submit data
        submitBtn.setOnClickListener {
            if (nameEdt.text.isNullOrEmpty().not())
                body.firstName = nameEdt.text.toString()
            if (familyEdt.text.isNullOrEmpty().not())
                body.lastName = familyEdt.text.toString()
            if (idNumberEdt.text.isNullOrEmpty().not())
                body.idNumber = idNumberEdt.text.toString()
            //Call api
            viewModel.updateProfileInfo(body)
        }
    }

    private fun openDatePicker() {
        PersianDatePickerDialog(requireContext())
            .setTodayButtonVisible(true)
            .setMinYear(1300)
            .setMaxYear(1400)
            .setInitDate(1370, 3, 13)
            .setTitleType(PersianDatePickerDialog.DAY_MONTH_YEAR)
            .setShowInBottomSheet(true)
            .setListener(object : PersianPickerListener {
                override fun onDateSelected(pDate: PersianPickerDate) {
                    val birthDate = "${pDate.gregorianYear}-${pDate.gregorianMonth}-${pDate.gregorianDay}"
                    val birthDatePersian = "${pDate.persianYear}-${pDate.persianMonth}-${pDate.persianDay}"
                    body.gregorianDate = birthDate
                    binding.birthDateEdt.setText(birthDatePersian)
                }

                override fun onDismissed() {}
            }).show()
    }

    private fun loadScreenData(){
        loadProfileData()
        observeProfileUpdateResult()
    }


    private fun loadProfileData() = binding.apply {
        viewModel.profileData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    loading.isVisible = true
                }

                is NetworkResult.Success -> {
                    loading.isVisible = false
                    result.data?.let { responseProfile ->
                        if (responseProfile.firstname.isNullOrEmpty().not())
                            nameEdt.setText(responseProfile.firstname)
                        if (responseProfile.lastname.isNullOrEmpty().not())
                            familyEdt.setText(responseProfile.lastname)
                        if (responseProfile.idNumber.isNullOrEmpty().not())
                            idNumberEdt.setText(responseProfile.idNumber)
                        if (responseProfile.birthDate.isNullOrEmpty().not())
                            birthDateEdt.setText(responseProfile.birthDate!!.split("T")[0])
                    }
                }

                is NetworkResult.Error -> {
                    loading.isVisible = false
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun observeProfileUpdateResult() = binding.apply {
        viewModel.updateProfileData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    submitBtn.enableLoading(true)
                }

                is NetworkResult.Success -> {
                    submitBtn.enableLoading(false)
                    result.data?.let {
                        lifecycleScope.launch {
                            EventBus.publish(Event.IsUpdateProfile)
                        }
                        this@EditProfileFragment.dismiss()
                    }
                }

                is NetworkResult.Error -> {
                    submitBtn.enableLoading(false)
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    //Theme
    override fun getTheme() = R.style.RemoveDialogBackground

    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(layoutInflater)
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