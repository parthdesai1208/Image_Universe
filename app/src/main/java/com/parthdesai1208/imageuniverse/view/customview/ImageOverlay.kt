package com.parthdesai1208.imageuniverse.view.customview

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.parthdesai1208.imageuniverse.R
import com.parthdesai1208.imageuniverse.model.ImageResponse

import kotlinx.android.synthetic.main.layout_overlay.view.*

class ImageOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var onInfoClick: (ImageResponse) -> Unit = {}

    init {
        View.inflate(context, R.layout.layout_overlay, this)
    }

    fun updateView(imageResponse: ImageResponse) {
        img_info_overlay.contentDescription = HtmlCompat.fromHtml(
            String.format(context.getString(R.string.cdInfoIcon), imageResponse.title),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        img_info_overlay.setOnClickListener { onInfoClick(imageResponse) }
    }
}