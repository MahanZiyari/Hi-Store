package ir.mahan.histore.ui.home.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.mahan.histore.R
import ir.mahan.histore.data.model.home.ResponseDiscount.ResponseDiscountItem
import ir.mahan.histore.databinding.ItemDiscountBinding
import ir.mahan.histore.util.base.BaseDiffUtils
import ir.mahan.histore.util.constants.BASE_URL_IMAGE
import ir.mahan.histore.util.extensions.loadImage
import ir.mahan.histore.util.extensions.toMoneyFormat
import javax.inject.Inject

class DiscountAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    RecyclerView.Adapter<DiscountAdapter.ViewHolder>() {

    private var items = emptyList<ResponseDiscountItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountAdapter.ViewHolder {
        val binding = ItemDiscountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscountAdapter.ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = position

    override fun getItemId(position: Int) = position.toLong()

    inner class ViewHolder(private val binding: ItemDiscountBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseDiscountItem) {
            binding.apply {
                itemTitle.text = item.title
                //Image
                val imageUrl = "$BASE_URL_IMAGE${item.image}"
                itemImg.loadImage(imageUrl)
                //Quantity
                itemProgress.progress = item.quantity.toString().toInt()
                //Final price
                itemPriceDiscount.text = item.finalPrice.toString().toInt().toMoneyFormat("تومان")
                //Discount
                itemPrice.apply {
                    text = item.discountedPrice.toString().toInt().toMoneyFormat("تومان")
                    paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    setTextColor(ContextCompat.getColor(context, R.color.salmon))
                }
                //Click
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(item.id!!)
                    }
                }
            }
        }
    }

    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(data: List<ResponseDiscountItem>) {
        val adapterDiffUtils = BaseDiffUtils(items, data)
        val diffUtils = DiffUtil.calculateDiff(adapterDiffUtils)
        items = data
        diffUtils.dispatchUpdatesTo(this)
    }
}