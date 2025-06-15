package ir.mahan.histore.ui.home

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import coil.load
import com.todkars.shimmer.ShimmerRecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.home.ResponseBanners
import ir.mahan.histore.data.model.home.ResponseDiscount
import ir.mahan.histore.data.model.home.ResponseProducts
import ir.mahan.histore.data.model.home.ResponseProducts.SubCategory.Products.Data
import ir.mahan.histore.databinding.DialogCheckVpnBinding
import ir.mahan.histore.databinding.FragmentHomeBinding
import ir.mahan.histore.ui.home.adapters.BannerAdapter
import ir.mahan.histore.ui.home.adapters.DiscountAdapter
import ir.mahan.histore.ui.home.adapters.ProductsAdapter
import ir.mahan.histore.util.ProductsCategories
import ir.mahan.histore.util.base.BaseFragment
import ir.mahan.histore.util.constants.DEBUG_TAG
import ir.mahan.histore.util.extensions.isVisible
import ir.mahan.histore.util.extensions.loadImage
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.util.extensions.showSnackBar
import ir.mahan.histore.util.extensions.transparentCorners
import ir.mahan.histore.util.network.NetworkResult
import ir.mahan.histore.viewmodel.HomeViewmodel
import ir.mahan.histore.viewmodel.ProfileViewmodel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
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
    private lateinit var discountTimer: CountDownTimer
    @Inject
    lateinit var bannerAdapter: BannerAdapter
    @Inject
    lateinit var discountAdapter: DiscountAdapter
    @Inject
    lateinit var mobileProductsAdapter: ProductsAdapter

    @Inject
    lateinit var shoesProductsAdapter: ProductsAdapter

    @Inject
    lateinit var stationeryProductsAdapter: ProductsAdapter

    @Inject
    lateinit var laptopProductsAdapter: ProductsAdapter

    @Inject
    lateinit var vpnStatus: Flow<Boolean>


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
            handleToolbarUserInteraction()
            // Restoring last scroll state
            viewModel.lastScrollState?.run {
                scrollLay.onRestoreInstanceState(this)
            }
        }
        // Load data
        loadScreenData()
        checkVpnStatus()
    }

    private fun handleToolbarUserInteraction() = binding.apply {
        avatarImg.setOnClickListener {
            findNavController().navigate(R.id.actionToProfile)
        }
        searchImg.setOnClickListener {
            findNavController().navigate(R.id.actionToSearch)
        }
    }

    private fun loadScreenData() {
        loadProfileData()
        loadBannerData()
        loadDiscountItems()
        loadProductsSubSections()
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

    private fun loadDiscountItems() = binding.apply {
        viewModel.discountItemsLiveData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is NetworkResult.Loading -> {
                    discountList.showShimmer()
                }

                is NetworkResult.Success -> {
                    discountList.hideShimmer()
                    result.data?.let {
                        if (it.isNotEmpty()) {
                            initDiscountRecycler(it)
                            // Discount Timer
                            val endTime = it[0].endTime!!.split("T")[0]
                            calculateDiscountItemsRemainingTime(endTime)
                            discountTimer.start()
                        } else {
                            //TODO("Show a good UI for no item at discount")
                        }
                    }
                }

                is NetworkResult.Error -> {
                    discountList.hideShimmer()
                    root.showSnackBar(result.error!!)
                }
            }
        }
    }

    private fun initDiscountRecycler(data: List<ResponseDiscount.ResponseDiscountItem>) {
        Timber.tag(DEBUG_TAG).d("Discount Items: ${data.size}")
        discountAdapter.setData(data)
        binding.discountList.setupRecyclerview(
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true), discountAdapter
        )
    }

    private fun calculateDiscountItemsRemainingTime(endData: String) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val date: Date = formatter.parse(endData) as Date
        val currentTimeMillis = System.currentTimeMillis()
        val remainingTimeMillis = date.time - currentTimeMillis
        discountTimer = object : CountDownTimer(remainingTimeMillis, 1_000) {
            override fun onTick(millisUntilFinished: Long) {
                //Calculate time
                var timer = millisUntilFinished
                val day: Long = TimeUnit.MILLISECONDS.toDays(timer)
                timer -= TimeUnit.DAYS.toMillis(day)
                val hours: Long = TimeUnit.MILLISECONDS.toHours(timer)
                timer -= TimeUnit.HOURS.toMillis(hours)
                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(timer)
                timer -= TimeUnit.MINUTES.toMillis(minutes)
                val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(timer)
                //View
                try {
                    binding.timerLay.apply {
                        if (day > 0) {
                            dayLay.isVisible = true
                            dayTxt.text = day.toString()
                        } else {
                            dayLay.isVisible = false
                        }
                        hourTxt.text = hours.toString()
                        minuteTxt.text = minutes.toString()
                        secondTxt.text = seconds.toString()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {

            }
        }
    }

    private fun loadProductsSubSections() = binding.apply {
        //Mobile
        if (mobileLay.parent != null) {
            val mobileInflate = mobileLay.inflate()
            viewModel.getProductsSpecifiedBy(ProductsCategories.MOBILE).observe(viewLifecycleOwner) {
                handleProductsNetworkResponse(it, mobileInflate.findViewById(R.id.mobileProductsList), mobileProductsAdapter)
            }
        }
        //Shoes
        if (shoesLay.parent != null) {
            val shoesInflate = shoesLay.inflate()
            viewModel.getProductsSpecifiedBy(ProductsCategories.SHOES).observe(viewLifecycleOwner) {
                handleProductsNetworkResponse(it, shoesInflate.findViewById(R.id.menShoesProductsList), shoesProductsAdapter)
            }
        }
        //Stationery
        val stationeryInflate = stationeryLay.inflate()
        viewModel.getProductsSpecifiedBy(ProductsCategories.STATIONERY).observe(viewLifecycleOwner) {
            handleProductsNetworkResponse(it, stationeryInflate.findViewById(R.id.stationeryProductsList), stationeryProductsAdapter)
        }
        //Laptop
        val laptopInflate = laptopLay.inflate()
        viewModel.getProductsSpecifiedBy(ProductsCategories.LAPTOP).observe(viewLifecycleOwner) {
            handleProductsNetworkResponse(it, laptopInflate.findViewById(R.id.laptopProductsList), laptopProductsAdapter)
        }
    }

    private fun handleProductsNetworkResponse(networkResult: NetworkResult<ResponseProducts>, recyclerView: ShimmerRecyclerView, adapter: ProductsAdapter){
        when (networkResult) {
            is NetworkResult.Loading -> {
                recyclerView.showShimmer()
            }

            is NetworkResult.Success -> {
                recyclerView.hideShimmer()
                networkResult.data?.let { responseProducts ->
                    responseProducts.subCategory?.let { subCats ->
                        subCats.products?.let { products ->
                            products.data?.let { myData ->
                                if (myData.isNotEmpty()) {
                                    initProductsRecyclers(myData, recyclerView, adapter)
                                }
                            }
                        }
                    }
                }
            }

            is NetworkResult.Error -> {
                recyclerView.hideShimmer()
                binding.root.showSnackBar(networkResult.error!!)
            }
        }
    }

    private fun initProductsRecyclers(
        data: List<Data>,
        recyclerView: ShimmerRecyclerView,
        adapter: ProductsAdapter
    ) {
        adapter.setData(data)
        recyclerView.setupRecyclerview(
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true), adapter
        )
    }

    // Utility functions
    private fun checkVpnStatus() {
        lifecycleScope.launch {
            vpnStatus.collect {
                showVpnDialog()
            }
        }
    }

    private fun showVpnDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogCheckVpnBinding.inflate(layoutInflater)
        dialog.transparentCorners()
        // Always Configure View before setting content view
        dialog.setContentView(dialogBinding.root)

        dialogBinding.yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    //  Lifecycle methods

    override fun onPause() {
        super.onPause()
        viewModel.lastScrollState = binding.scrollLay.onSaveInstanceState()
    }
    override fun onStop() {
        super.onStop()
        if (::discountTimer.isInitialized) discountTimer.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}