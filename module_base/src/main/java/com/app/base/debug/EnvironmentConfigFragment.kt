package com.app.base.debug

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.android.base.app.fragment.BaseFragment
import com.android.base.utils.android.views.views
import com.android.base.utils.android.views.visibleOrGone
import com.app.base.R
import kotlinx.android.synthetic.main.base_debug_environment.*

/**
 * @author Ztiany
 * Email: ztiany3@gmail.com
 * Date : 2019-08-13 19:17
 */
class EnvironmentConfigFragment : BaseFragment() {

    companion object {
        private const val SHOW_TITLE = "show_title"

        fun newInstance(showTitle: Boolean) = EnvironmentConfigFragment().apply {
            arguments = Bundle().apply {
                putBoolean(SHOW_TITLE, showTitle)
            }
        }
    }

    override fun provideLayout() = R.layout.base_debug_environment

    override fun onViewPrepared(view: View, savedInstanceState: Bundle?) {
        super.onViewPrepared(view, savedInstanceState)

        baseToolbarDebug.visibleOrGone(arguments?.getBoolean(SHOW_TITLE, false) ?: false)

        baseBtnDebugOneKeySwitch.setOnClickListener {
            doOneKeySwitch()
        }

        val allCategory = EnvironmentContext.allCategory()

        allCategory.forEach { (category, list) ->
            val environmentItemLayout = EnvironmentItemLayout(requireContext())
            environmentItemLayout.bindEnvironmentList(category, list)
            baseLlDebugHostContent.addView(environmentItemLayout, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        }
    }

    private fun doOneKeySwitch() {
        val key = EnvironmentContext.allCategory().keys.toList()[0]
        val list = EnvironmentContext.allCategory().getValue(key).map { it.name }

        AlertDialog.Builder(requireContext())
                .setSingleChoiceItems(list.toTypedArray(), list.indexOf(EnvironmentContext.selected(key).name)) { dialog, which ->
                    dialog.dismiss()

                    EnvironmentContext.allCategory().forEach { (category, list) ->
                        EnvironmentContext.select(category, list[which])
                    }

                    baseLlDebugHostContent.views.filterIsInstance<EnvironmentItemLayout>().forEach {
                        it.refresh()
                    }

                }.show()
    }

}