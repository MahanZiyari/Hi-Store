package ir.mahan.histore.ui.search.filter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ir.mahan.histore.R
import ir.mahan.histore.data.model.search.filter.FilterModel
import ir.mahan.histore.databinding.FragmentSearchFilterBinding
import ir.mahan.histore.ui.search.adapters.FilterAdapter
import ir.mahan.histore.util.extensions.setupRecyclerview
import ir.mahan.histore.viewmodel.SearchViewmodel
import javax.inject.Inject


/**
 * Search filter fragment
 *
 * @constructor Create empty Search filter fragment
 */
@AndroidEntryPoint
class SearchFilterFragment : BottomSheetDialogFragment() {
    //Binding
    private var _binding: FragmentSearchFilterBinding? = null
    private val binding get() = _binding!!

    /**
     * Filter adapter
     */
    @Inject
    lateinit var filterAdapter: FilterAdapter


    //Theme
    override fun getTheme() = R.style.RemoveDialogBackground

    //Other
    private val viewModel by activityViewModels<SearchViewmodel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchFilterBinding.inflate(layoutInflater)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Init views
        binding.apply {
            //Close
            closeImg.setOnClickListener { this@SearchFilterFragment.dismiss() }
        }
        //Fill filter data
        viewModel.getFilterData()
        //Load data
        loadFilterData()
    }



    private fun loadFilterData() {
        viewModel.filterData.observe(this) {
            initFilterRecycler(it)
        }
    }


    private fun initFilterRecycler(filters: List<FilterModel>) {
        filterAdapter.setData(filters)
        binding.filtersList.setupRecyclerview(
            LinearLayoutManager(requireContext()),
            filterAdapter
        )

        filterAdapter.setOnItemClickListener {
            viewModel.setActiveFilterTo(it)
            this.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}