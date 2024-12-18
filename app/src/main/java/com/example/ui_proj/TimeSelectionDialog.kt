package com.example.ui_proj.utils

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.ui_proj.R
import com.example.ui_proj.hourArray
import java.util.*

fun setupTimeSelectionDialog(context: Context, layoutInflater: LayoutInflater, dateTextView: TextView, dateBtn: Button, onTimeSelected: (String) -> Unit) {
    var timePicked: String
    val dialogView = layoutInflater.inflate(R.layout.time_dialog, null)
    val numberPickerHour = dialogView.findViewById<NumberPicker>(R.id.numberPickerHour)
    val pickTimeBtn = dialogView.findViewById<Button>(R.id.pick_hour_btn)
    val dialogDateTextView = dialogView.findViewById<TextView>(R.id.dialog_date)

    // Configure the number picker for hours
    numberPickerHour.minValue = 0
    numberPickerHour.maxValue = hourArray.size - 1
    numberPickerHour.displayedValues = hourArray

    val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
    builder.setView(dialogView)

    val dialog = builder.create()
    dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)

    // Date selection button
    dateBtn.setOnClickListener {
        val cal = Calendar.getInstance()
        val today = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 7)
        val nextWeek = cal.timeInMillis

        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val selectedDate = "${dayOfMonth}/${month + 1}/${year}"
            dateTextView.text = selectedDate
            dialogDateTextView.text = "- ${selectedDate} -"
            dialog.show()
        }

        val datePickerDialog = DatePickerDialog(
            context,
            datePickerListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = today
        datePickerDialog.datePicker.maxDate = nextWeek
        datePickerDialog.show()
    }

    // Time picker button
    pickTimeBtn.setOnClickListener {
        val hourPicked = hourArray[numberPickerHour.value]
        timePicked = "${dateTextView.text} - $hourPicked"
        dateTextView.text = timePicked
        onTimeSelected(timePicked)
        dialog.dismiss()
    }
}


