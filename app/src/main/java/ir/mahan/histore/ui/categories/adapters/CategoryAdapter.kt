package ir.mahan.histore.ui.categories.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.mahan.histore.data.model.categories.ResponseCategories
import ir.mahan.histore.databinding.ItemCategoriesBinding
import ir.mahan.histore.util.base.BaseDiffUtils
import ir.mahan.histore.util.extensions.setupRecyclerview
import javax.inject.Inject

class CategoryAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var items = emptyList<ResponseCategories.ResponseCategoriesItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemCategoriesBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: ResponseCategories.ResponseCategoriesItem) {
            binding.apply {
                itemTitle.text = item.title
                //Sub category
                if (item.subCategories!!.isNotEmpty()) {
                    itemSubCatsList.isVisible = true
                    subCategoriesList(item.subCategories, binding)
                } else {
                    itemSubCatsList.isVisible = false
                }
            }
        }
    }

    private fun subCategoriesList(list: List<ResponseCategories.ResponseCategoriesItem.SubCategory>, binding: ItemCategoriesBinding) {
        val subCategoryAdapter = SubCategoryAdapter()
        subCategoryAdapter.setData(list)
        binding.itemSubCatsList.setupRecyclerview(
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, true), subCategoryAdapter
        )
        //Send slug
        subCategoryAdapter.getSlug { slug->
            onItemClickListener?.let { it(slug) }
        }
    }

    private var onItemClickListener: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<ResponseCategories.ResponseCategoriesItem>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}