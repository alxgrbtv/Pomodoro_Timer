package org.hyperskill.pomodoro

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class SettingDialogFragment() : DialogFragment() {

    // Use this instance of the interface to deliver action events
    @Suppress("MemberVisibilityCanBePrivate")
    internal lateinit var listener: SettingDialogListener

    /* The activity that creates an instance of this dialog fragment must
     * Implement this interface to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface SettingDialogListener {
        fun onDialogPositiveClick(newWorkTime: Int?, newRestTime: Int?)
    }

    // Override the Fragment.onAttach() method to instantiate the SettingDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as SettingDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            // Pass null as the parent view because its going in the dialog layout
            builder.setView(inflater.inflate(R.layout.fragment_setting_dialog, null))
                    .setPositiveButton(R.string.button_ok) { _, _ ->
                        val workTimeEditText = dialog!!.findViewById<EditText>(R.id.workTime)
                        val restTimeEditText = dialog!!.findViewById<EditText>(R.id.restTime)
                        val newWorkTime = workTimeEditText.text.toString().toIntOrNull()
                        val newRestTime = restTimeEditText.text.toString().toIntOrNull()
                        listener.onDialogPositiveClick(newWorkTime, newRestTime)
                    }
                    .setNegativeButton(R.string.button_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}