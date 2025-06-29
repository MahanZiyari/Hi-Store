package ir.mahan.histore.util.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.google.android.material.snackbar.Snackbar
import ir.mahan.histore.R
import java.text.DecimalFormat

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.showKeyboard(activity: Activity) {
    requestFocus()
    post {
        WindowCompat.getInsetsController(activity.window, this).show(WindowInsetsCompat.Type.ime())
    }
}

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun ImageView.loadImage(url: String) {
    this.load(url) {
        crossfade(true)
        crossfade(500)
        diskCachePolicy(CachePolicy.ENABLED)
        error(R.drawable.placeholder)
    }
}

fun View.isVisible(isShownLoading: Boolean, container: View) {
    if (isShownLoading) {
        this.isVisible = true
        container.isVisible = false
    } else {
        this.isVisible = false
        container.isVisible = true
    }
}

fun Int.toMoneyFormat(currency: String): String {
    return "${DecimalFormat("#,###.##").format(this)} $currency"
}

fun Int.toMoneyFormat(): String {
    return DecimalFormat("#,###.##").format(this)
}

fun RecyclerView.setupRecyclerview(myLayoutManager: RecyclerView.LayoutManager, myAdapter: RecyclerView.Adapter<*>) {
    this.apply {
        layoutManager = myLayoutManager
        setHasFixedSize(true)
        adapter = myAdapter
    }
}

fun Dialog.transparentCorners() {
    this.window!!.setBackgroundDrawableResource(android.R.color.transparent)
}


/**
 * Created Extension Functions to Export File Path from provided Uri
 *
 * @param context
 * @return
 */
@SuppressLint("Range")
fun Uri.asFilePath(context: Context): String? {
    var  filePath: String? = null
    val  cursor = context.contentResolver.query(this, null, null, null)
    if (cursor == null)
        filePath = this.path
    else {
        if (cursor.moveToFirst())
            filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
        cursor.close()
    }
    return filePath
}