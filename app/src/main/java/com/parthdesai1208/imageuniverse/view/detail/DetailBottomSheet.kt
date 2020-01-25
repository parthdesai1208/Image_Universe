package com.parthdesai1208.imageuniverse.view.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.parthdesai1208.imageuniverse.R
import com.parthdesai1208.imageuniverse.model.ImageResponse
import com.parthdesai1208.obvious.utils.makeUpDate

import kotlinx.android.synthetic.main.layout_bottom_sheet.*

class DetailBottomSheet : BottomSheetDialogFragment() {

    private var model: ImageResponse? = null

    companion object {
        private val ModelKey: String? = null
        fun passModelClass(model: ImageResponse): DetailBottomSheet {
            Bundle().apply { putParcelable(ModelKey, model) }
            val args = Bundle()
            args.putParcelable(ModelKey, model)
            val fragment =
                DetailBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getTheme(): Int =
        R.style.bottomSheetDialogFragmentStyle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.run {
            model = getParcelable(ModelKey)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        title_bottom_sheet.text = model?.title
        date_bottom_sheet.text =
            makeUpDate(model?.date.toString())
        explanation_bottom_sheet.text = model?.explanation
    }
}