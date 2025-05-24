package ir.mahan.histore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.PagerSnapHelper
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.home.ResponseBanners
import ir.mahan.histore.databinding.FragmentHomeBinding
import ir.mahan.histore.ui.home.adapters.BannerAdapter
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.extensions.isVisible
import ir.mahan.histore.util.extensions.loadImage
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.HomeViewmodel
import ir.mahan.histore.viewmodel.ProfileViewmodel
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    //Binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //ViewModels
    private val profileViewModel: ProfileViewmodel by activityViewModels()
    private val viewModel: HomeViewmodel by activityViewModels()
    // Other
    private val pagerSnapHelper by lazy { PagerSnapHelper() }
    @Inject
    lateinit var bannerAdapter: BannerAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Handle UI
        binding.apply {
            avatarImg.setOnClickListener{
                findNavController().navigate(R.id.actionToProfile)
            }
        }
        // Load data
        loadProfileData()
        loadBannerData()
    }

    private fun loadProfileData() = binding.apply {
        profileViewModel.profileData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    avatarLoading.isVisible = true
                }

                is NetworkResult.Success -> {
                    avatarLoading.isVisible = false
                    result.data?.let {
                        if (it.avatar != null) {
                            avatarImg.loadImage(it.avatar)
                            avatarBadgeImg.isVisible = false
                        } else {
                            avatarImg.load(R.drawable.placeholder_user)
                            avatarBadgeImg.isVisible = true
                        }
                    }
                }

                is NetworkResult.Error -> {
                    avatarLoading.isVisible = false
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun initBannerRecycler(data: List<ResponseBanners.ResponseBannersItem>) {
        bannerAdapter.setData(data)
        binding.bannerList.apply {
            adapter = bannerAdapter
            set3DItem(true)
            setAlpha(true)
            setInfinite(false)
        }
        //Indicator
        binding.apply {
            pagerSnapHelper.attachToRecyclerView(bannerList)
            bannerIndicator.attachToRecyclerView(bannerList, pagerSnapHelper)
        }
    }


    private fun loadBannerData() = binding.apply {
        viewModel.bannersLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    bannerLoading.isVisible(true, bannerList)
                }

                is NetworkResult.Success -> {
                    bannerLoading.isVisible(false, bannerList)
                    result.data?.let {
                        if (it.isNotEmpty()) {
                            initBannerRecycler(it)
                        }
                    }
                }

                is NetworkResult.Error -> {
                    bannerLoading.isVisible(false, bannerList)
                    root.showSnackBar(result.error!!)
                }
            }
        }

    }
}