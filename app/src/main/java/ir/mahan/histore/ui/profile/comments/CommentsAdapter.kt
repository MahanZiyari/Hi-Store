package ir.mahan.histore.ui.profile.comments


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ir.mahan.histore.data.model.profile.userComment.ResponseProfileComments
import ir.mahan.histore.data.model.profile.userComment.ResponseProfileComments.Data
import ir.mahan.histore.databinding.ItemMyCommentsBinding
import ir.mahan.histore.util.base.BaseDiffUtils
import ir.mahan.histore.util.constants.BASE_URL_IMAGE
import ir.mahan.histore.util.extensions.loadImage
import javax.inject.Inject

class CommentsAdapter @Inject constructor() : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var items = emptyList<Data>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsAdapter.ViewHolder {
        val binding = ItemMyCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentsAdapter.ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemMyCommentsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Data) {
            binding.apply {
                item.product?.let {
                    itemTitle.text = it.title
                    //Image
                    val imageUrl = "$BASE_URL_IMAGE${it.image}"
                    itemImg.loadImage(imageUrl)
                }
                itemRating.rating = item.rate.toString().toFloat()
                itemCommentTxt.text = item.comment
                //Click
                itemTrashImg.setOnClickListener {
                    onItemClickListener?.let { it(item.id!!) }
                }
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<Data>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}