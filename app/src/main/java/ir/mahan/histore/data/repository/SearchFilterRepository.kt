package ir.mahan.histore.data.repository


import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.mahan.histore.R
import ir.mahan.histore.data.model.search.filter.FilterModel
import ir.mahan.histore.util.constants.CHEEP
import ir.mahan.histore.util.constants.EXPENSIVE
import ir.mahan.histore.util.constants.HITS
import ir.mahan.histore.util.constants.NEW
import ir.mahan.histore.util.constants.RATE
import ir.mahan.histore.util.constants.SELL
import javax.inject.Inject

class SearchFilterRepository @Inject constructor(@ApplicationContext private val context: Context) {
    fun fillFilterData(): MutableList<FilterModel> {
        val list = mutableListOf<FilterModel>()
        list.add(FilterModel(1, context.getString(R.string.filterNew), NEW))
        list.add(FilterModel(2, context.getString(R.string.filterExpensive), EXPENSIVE))
        list.add(FilterModel(3, context.getString(R.string.filterCheep), CHEEP))
        list.add(FilterModel(4, context.getString(R.string.filterRate), RATE))
        list.add(FilterModel(5, context.getString(R.string.filterSell), SELL))
        list.add(FilterModel(6, context.getString(R.string.filterHits), HITS))
        return list
    }
}