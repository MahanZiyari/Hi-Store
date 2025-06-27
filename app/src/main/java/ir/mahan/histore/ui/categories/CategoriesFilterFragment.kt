package ir.mahan.histore.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.slider.LabelFormatter
import com.google.android.material.slider.RangeSlider
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.search.filter.FilterModel
import ir.mahan.histore.databinding.FragmentCategoriesFilterBinding
import ir.mahan.histore.ui.categories.adapters.CategoryAdapter
import ir.mahan.histore.util.extensions.toMoneyFormat
import ir.mahan.histore.viewmodel.CatProductsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFilterFragment : BottomSheetDialogFragment() {

    //Binding
    private var _binding: FragmentCategoriesFilterBinding? = null
    private val binding get() = _binding!!

    // View Model
    private val viewModel: CatProductsViewModel by activityViewModels()

    // Adapters
    @Inject
    lateinit var categoriesAdapter: CategoryAdapter

    //Other Props
    private var minPrice: String? = null
    private var maxPrice: String? = null
    private var sort: String? = null
    private var search: String? = null
    private var available: Boolean? = null

    // Called inside onViewCreated()
    private fun setupUI() = binding.apply {
        closeImg.setOnClickListener { this@CategoriesFilterFragment.dismiss() }
        initPriceRange()
        //Rtl scrollview
        lifecycleScope.launch {
            delay(100)
            sortScroll.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
        }
        //Click
        submitBtn.setOnClickListener {
            //Search
            if (searchEdt.text.isNotEmpty()) {
                search = searchEdt.text.toString()
            }
            //Available
            available = availableCheck.isChecked
            //Send data
            viewModel.setSelectedFilers(sort, search, minPrice, maxPrice, available)
            //Close
            this@CategoriesFilterFragment.dismiss()
        }
    }

    private fun initFiltersChipGroup(filters: List<FilterModel>) {
        var tempList = mutableListOf<FilterModel>()
        tempList.clear()
        tempList = filters.toMutableList()
        tempList.forEach {
            val chip = Chip(requireContext())
            val chipDrawable = ChipDrawable.createFromAttributes(
                requireContext(),
                null,
                0,
                R.style.FilterChipsBackground
            )
            chip.setChipDrawable(chipDrawable)
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            chip.text = it.faName
            chip.id = it.id
            chip.setTextAppearanceResource(R.style.FilterChipsText)
            binding.sortChipGroup.addView(chip)
        }

        binding.sortChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            sort = tempList[group.checkedChipId - 1].enName
        }
    }

    private fun initPriceRange() {
        val labelFormatter = LabelFormatter {
            it.toInt().toMoneyFormat("تومان")
        }
        binding.priceRange.apply {
            setValues(7000000f, 21000000f)
            setLabelFormatter(labelFormatter)
            // Touch Listener
            addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: RangeSlider) {
                    // Don't Need
                }

                override fun onStopTrackingTouch(slider: RangeSlider) {
                    val values = slider.values
                    minPrice = values[0].toInt().toString()
                    maxPrice = values[1].toInt().toString()
                }

            })
        }
    }

    private fun loadScreenData() {
        showSortOptions()
    }

    private fun showSortOptions() {
        viewModel.filterData.observe(viewLifecycleOwner) {
            initFiltersChipGroup(it)
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Lifecycle Methods
    ///////////////////////////////////////////////////////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCategoriesFilterBinding.inflate(layoutInflater)
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