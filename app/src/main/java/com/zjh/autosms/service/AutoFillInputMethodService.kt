package com.zjh.autosms.service

import android.content.ClipboardManager
import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.zjh.autosms.R
import com.zjh.autosms.event.BusEvent
import com.zjh.autosms.utils.SPUtils
import com.zjh.autosms.utils.showToast
import org.greenrobot.eventbus.EventBus

class AutoFillInputMethodService : InputMethodService(), View.OnClickListener {
    companion object {
        private val TAG = AutoFillInputMethodService::class.java.simpleName
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_number, null)
        view.findViewById<View>(R.id.tv0).setOnClickListener(this)
        view.findViewById<View>(R.id.tv1).setOnClickListener(this)
        view.findViewById<View>(R.id.tv2).setOnClickListener(this)
        view.findViewById<View>(R.id.tv3).setOnClickListener(this)
        view.findViewById<View>(R.id.tv4).setOnClickListener(this)
        view.findViewById<View>(R.id.tv5).setOnClickListener(this)
        view.findViewById<View>(R.id.tv6).setOnClickListener(this)
        view.findViewById<View>(R.id.tv7).setOnClickListener(this)
        view.findViewById<View>(R.id.tv8).setOnClickListener(this)
        view.findViewById<View>(R.id.tv9).setOnClickListener(this)
        view.findViewById<View>(R.id.tv_paste).setOnClickListener(this)
        view.findViewById<View>(R.id.tv_remove).setOnClickListener(this)
        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        Log.d(TAG, "onStartInputView")
        val code = SPUtils.getInstance().getString(SPUtils.KEY_AUTO_FILL_CODE)
        if (code?.isNotEmpty() == true) {
            SPUtils.getInstance().putString(SPUtils.KEY_AUTO_FILL_CODE, "")
            currentInputConnection.commitText(code, 1)
            EventBus.getDefault().post(BusEvent(BusEvent.SERVICE_FINISH_INPUT))
            requestHideSelf(0)
        }
    }

    override fun onFinishInput() {
        super.onFinishInput()
        Log.d(TAG, "onFinishInput")
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_paste -> {
                (getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
                    if (it.hasPrimaryClip()) {
                        currentInputConnection.commitText(it.primaryClip?.getItemAt(0)?.text, 1)
                    } else {
                        showToast("剪切板为空")
                    }
                }
            }
            R.id.tv_remove -> {
                currentInputConnection.deleteSurroundingText(1, 0)
            }
            else -> {
                if (v is TextView) {
                    val text = v.text.toString().trim()
                    currentInputConnection.commitText(text, 1)
                }
            }
        }
    }
}