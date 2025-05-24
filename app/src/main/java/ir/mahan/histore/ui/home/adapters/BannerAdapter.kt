package ir.mahan.histore.ui.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.mahan.histore.data.model.home.ResponseBanners.ResponseBannersItem
import ir.mahan.histore.databinding.ItemBannersBinding
import ir.mahan.histore.util.base.BaseDiffUtils
import ir.mahan.histore.util.constants.BASE_URL_IMAGE_WITH_STORAGE
import ir.mahan.histore.util.extensions.loadImage
import javax.inject.Inject

class BannerAdapter @Inject constructor() : RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    private var currentItems = emptyList<ResponseBannersItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBannersBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = currentItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentItems[position])
    }

    inner class ViewHolder(private val binding: ItemBannersBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseBannersItem) = binding.apply {
            itemTitle.text = item.title
            //Image
            val imageUrl = "$BASE_URL_IMAGE_WITH_STORAGE${item.image}"
            itemImg.loadImage(imageUrl)
            //Click
            root.setOnClickListener {

            }
        }
    }

    private var onItemClickListener: ((String, String) -> Unit)? = null

    fun setOnItemClickListener(listener: (String, String) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(newItems: List<ResponseBannersItem>) {
        val adapterDiffUtils = BaseDiffUtils(currentItems, newItems)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        currentItems = newItems
        diffUtils.dispatchUpdatesTo(this)
    }
}
