package ir.mahan.histore.ui.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.PickExtension
import com.app.imagepickerlibrary.model.PickerType
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.databinding.FragmentProfileBinding
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.constants.AVATAR_KEY
import ir.mahan.histore.util.constants.HTTP_METHOD_KEY
import ir.mahan.histore.util.constants.HTTP_METHOD_POST
import ir.mahan.histore.util.constants.MULTIPART_FROM_DATA
import ir.mahan.histore.util.constants.UTF_8
import ir.mahan.histore.util.event.Event
import ir.mahan.histore.util.event.EventBus
import ir.mahan.histore.util.extensions.asFilePath
import ir.mahan.histore.util.extensions.loadImage
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.extensions.toMoneyFormat
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.ProfileViewmodel
import ir.mahan.histore.viewmodel.WalletViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URLEncoder

@AndroidEntryPoint
class ProfileFragment : BaseFragment(), ImagePickerResultListener {

    ///////////////////////////////////////////////////////////////////////////
    // Properties
    ///////////////////////////////////////////////////////////////////////////
    // binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // ViewModels
    private val viewModel: ProfileViewmodel by activityViewModels()
    private val walletViewModel: WalletViewModel by viewModels()

    // Other
    private val imagePicker: ImagePicker by lazy { registerImagePicker(this) }
    ///////////////////////////////////////////////////////////////////////////
    // Functions
    ///////////////////////////////////////////////////////////////////////////

    // Called inside onViewCreated()
    private fun loadScreenData() {
        loadProfileData()
        loadWalletAmount()
        loadAvatar()
    }

    // Called inside onViewCreated()
    private fun setupUI() = binding.apply {
        //Choose image
        avatarEditImg.setOnClickListener {
            openImagePicker()
        }
        //Menu items
        menuLay.apply {
            menuEditLay.setOnClickListener { findNavController().navigate(R.id.actionToEditProfile) }
            menuWalletLay.setOnClickListener { findNavController().navigate(R.id.actionToIncreaseWallet) }
            menuCommentsLay.setOnClickListener { findNavController().navigate(R.id.actionToUserComments) }
            menuFavoritesLay.setOnClickListener { findNavController().navigate(R.id.actionToUserFavorites) }
            menuAddressesLay.setOnClickListener { findNavController().navigate(R.id.actionToUserAddresses) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun loadProfileData() = binding.apply {
        viewModel.profileData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    loading.isVisible = true
                }

                is NetworkResult.Success -> {
                    loading.isVisible = false
                    result.data?.let { responseProfile ->
                        if (responseProfile.avatar != null) {
                            avatarImg.loadImage(responseProfile.avatar)
                        } else {
                            avatarImg.load(R.drawable.placeholder_user)
                        }
                        //Name
                        if (responseProfile.firstname.isNullOrEmpty().not())
                            nameTxt.text = "${responseProfile.firstname} ${responseProfile.lastname}"
                        //Info
                        infoLay.apply {
                            phoneTxt.text = responseProfile.cellphone
                            //Birthdate
                            if (responseProfile.birthDate!!.isNotEmpty()) {
                                birthDateTxt.text = responseProfile.birthDate.split("T")[0]
                                    .replace("-", " / ")
                            } else {
                                infoBirthDateLay.isVisible = false
                                line2.isVisible = false
                            }
                        }
                    }
                }

                is NetworkResult.Error -> {
                    loading.isVisible = false
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun loadWalletAmount() = binding.infoLay.apply {
        walletViewModel.walletData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    walletLoading.isVisible = true
                }

                is NetworkResult.Success -> {
                    walletLoading.isVisible = false
                    result.data?.let { responseWallet ->
                        walletTxt.text =
                            responseWallet.wallet.toString().toInt().toMoneyFormat("تومان")
                    }
                }

                is NetworkResult.Error -> {
                    walletLoading.isVisible = false
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun loadAvatar() = binding.apply {
        viewModel.avatarLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    avatarLoading.isVisible = true
                }

                is NetworkResult.Success -> {
                    avatarLoading.isVisible = false
                    if (isNetworkAvailable)
                        viewModel.fetchProfileData()
                }

                is NetworkResult.Error -> {
                    avatarLoading.isVisible = false
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ImagePicker
    ///////////////////////////////////////////////////////////////////////////

    private fun openImagePicker() {
        imagePicker
            .title(getString(R.string.galleryImages))
            .multipleSelection(false)
            .showFolder(true)
            .cameraIcon(true)
            .doneIcon(true)
            .allowCropping(true)
            .compressImage(false)
            .maxImageSize(2.5f)
            .extension(PickExtension.ALL)
        imagePicker.open(PickerType.GALLERY)
    }

    override fun onImagePick(uri: Uri?) {
        // Returns if uri is null
        if (uri == null) return
        // Also returns if provided file path is null
        val selectedImageFilePath = uri.asFilePath(requireContext())?.let { path ->
            File(path)
        } ?: return
        // finding File Name with path
        val fileName = URLEncoder.encode(selectedImageFilePath.absolutePath, UTF_8)
        val requestBodyFile =
            selectedImageFilePath.asRequestBody(MULTIPART_FROM_DATA.toMediaTypeOrNull())

        // Generating a body for uploading Avatar
        val multipartRequestBody: MultipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM) // Set the MIME typ
            .addFormDataPart(HTTP_METHOD_KEY, HTTP_METHOD_POST)
            .addFormDataPart(AVATAR_KEY, fileName, requestBodyFile)
            .build()

        if (isNetworkAvailable)
            viewModel.uploadAvatarToServer(multipartRequestBody)
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
        // Not Needed
    }


    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Handle UI
        setupUI()
        loadScreenData()
        lifecycleScope.launch {
            EventBus.observe<Event.IsUpdateProfile> {
                if (isNetworkAvailable)
                    viewModel.fetchProfileData()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}