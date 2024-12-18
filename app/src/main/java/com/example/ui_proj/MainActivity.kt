package com.example.ui_proj

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

import com.example.ui_proj.utils.toggleTheme
import com.example.ui_proj.features.setupImageSlider
import com.example.ui_proj.utils.setupTimeSelectionDialog

val hourArray = arrayOf("09:00", "11:00", "13:00", "15:30", "18:20", "21:00", "00:30")

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val bookingDetails: MutableMap<String, Any> = mutableMapOf(
            "timePicked" to "",
            "numberOfTickets" to 0,
            "totalCost" to 0,
            "ticketsType" to "",
            "theater" to ""
        )
        // Image slider setup
        setupImageSlider(    this,
            findViewById(R.id.image_album),
            findViewById(R.id.prev),
            findViewById(R.id.next))

        // Time selection dialog setup
        setupTimeSelectionDialog(this,
            layoutInflater,
            findViewById(R.id.textSelectedDate),
            findViewById(R.id.buttonSelectDate),
            ) { selectedTime -> bookingDetails["timePicked"] = selectedTime }

        // Ticket number picker setup
        setupTicketNumberPicker {ticketCount -> bookingDetails["numberOfTickets"] = ticketCount }

        // Ticket type radio setup
        setupTicketsType {selectedType -> bookingDetails["ticketsType"] = selectedType}

        // Theater spinner setup
        setupTheaterSpinner {selectedOption -> bookingDetails["theater"] = selectedOption}

        // Order Confirmation dialog setup
        setupConfirmationDialog(bookingDetails)

        val themeToggle = findViewById<ImageButton>(R.id.themeSwitch)
        themeToggle.bringToFront()

        // Set initial state based on current theme
        val currentMode = AppCompatDelegate.getDefaultNightMode()
        themeToggle.isSelected = currentMode == AppCompatDelegate.MODE_NIGHT_YES

        // Set listener to manually switch between modes
        themeToggle.setOnClickListener {
            toggleTheme(this, themeToggle)
        }
    }

    private fun setupTicketsType(selectedType: (String) -> Unit) {
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroupTicketType)
        radioGroup.setOnCheckedChangeListener { _, _ ->
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
                val selectedText = selectedRadioButton.text.toString()
                selectedType(selectedText)
        }

    }
        }

    // Function to set up the time selection dialog

    // Function to set up the ticket number picker
    private fun setupTicketNumberPicker(onTicketCountSelected: (Int) -> Unit) {
        val numberPickerTickets = findViewById<NumberPicker>(R.id.numberPickerTickets)
        val ticketTextView = findViewById<TextView>(R.id.textSelectedTickets)

        // Configure the number picker for tickets
        numberPickerTickets.minValue = 0
        numberPickerTickets.maxValue = 30

        numberPickerTickets.setOnValueChangedListener { _, _, newVal ->
            ticketTextView.text = getString(R.string.tickets, newVal)
            onTicketCountSelected(newVal)
        }
    }

    private fun setupTheaterSpinner(onItemSelected: (String) -> Unit) {
        val spinner = findViewById<Spinner>(R.id.spinnerTheater)
        val theaterOptions = listOf(getString(R.string.glilot), getString(R.string.c_Telaviv), getString(R.string.yes_planet), getString(R.string.cinimatek))

        // Create an ArrayAdapter
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            theaterOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Dropdown layout
        spinner.adapter = adapter

        // Set a listener to handle item selection
        spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = theaterOptions[position]
                onItemSelected(selectedOption)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun submitOrder(bookingDetails: MutableMap<String, Any>): String{
        val missingFields = mutableListOf<String>()

        // Validate each field
        if ((bookingDetails["timePicked"] as? String).isNullOrBlank()) {
            missingFields.add(getString(R.string.time))
        }
        if ((bookingDetails["numberOfTickets"] as? Int ?: 0) <= 0) {
            missingFields.add(getString(R.string.number_of_tickets))
        }
        if ((bookingDetails["ticketsType"] as? String).isNullOrBlank()) {
            missingFields.add(getString(R.string.ticket_type_title))
        }
        if ((bookingDetails["theater"] as? String).isNullOrBlank()) {
            missingFields.add(getString(R.string.teather_text))
        }

        // Check if there are missing fields
        return if (missingFields.isEmpty()) {
            ""
        } else {
            // return missing fields
            "${getString(R.string.missing_fields)} ${missingFields.joinToString(", ")}"
        }
    }

    private fun setupConfirmationDialog(bookingDetails: MutableMap<String, Any>){
        val submitBtn = findViewById<Button>(R.id.buttonSubmitOrder)

        submitBtn.setOnClickListener {
            val fieldsResult: String = submitOrder(bookingDetails)
            if(fieldsResult !== ""){
                Snackbar.make(findViewById(android.R.id.content), fieldsResult, Snackbar.LENGTH_LONG).show()
            } else{
                bookingDetails["totalAmount"] =  if(bookingDetails["ticketsType"] == "Adult"){
                    (bookingDetails["numberOfTickets"] as Int) * 30
                } else (bookingDetails["numberOfTickets"] as Int) * 15

                val dialogView = layoutInflater.inflate(R.layout.order_confirmation, null)
                val builder = AlertDialog.Builder(this)
                builder.setView(dialogView)

                val dialog = builder.create()
                dialog.window?.setBackgroundDrawableResource(R.drawable.rounded_dialog)

                dialogView.findViewById<TextView>(R.id.tickets_val).text = bookingDetails["numberOfTickets"].toString()
                dialogView.findViewById<TextView>(R.id.time_val).text = bookingDetails["timePicked"].toString()
                dialogView.findViewById<TextView>(R.id.tickets_type_val).text = bookingDetails["ticketsType"].toString()
                dialogView.findViewById<TextView>(R.id.theater_val).text = bookingDetails["theater"].toString()
                dialogView.findViewById<TextView>(R.id.order_sum_val).text = getString(R.string.currency, bookingDetails["totalAmount"].toString())

                dialog.show()
                konfettiAnimation(dialogView, dialog)

            }
        }
    }

    // button rotation animation, then konfetti animation once it ends.
    private fun konfettiAnimation(dialogView: View, dialog: Dialog){
        val confirmOrderBtn = dialogView.findViewById<MaterialButton>(R.id.confirm_order)
        val exitBtn = dialogView.findViewById<ImageButton>(R.id.exit_conf_dialog)
        exitBtn.setOnClickListener{
            dialog.dismiss()
        }
        confirmOrderBtn.setOnClickListener {
            val rotateAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.rotate_360)
            confirmOrderBtn.startAnimation(rotateAnim)
            rotateAnim.setAnimationListener(object: AnimationListener {
                override fun onAnimationStart(p0: Animation?) {
                }

                override fun onAnimationEnd(p0: Animation?) {
                    confirmOrderBtn.text = getString(R.string.order_confirmed)
                    confirmOrderBtn.isEnabled = false
                    confirmOrderBtn.setBackgroundColor(resources.getColor(R.color.green, theme))
                    confirmOrderBtn.setTextColor(resources.getColor(R.color.white, theme))
                    confirmOrderBtn.icon = null

                    val konfettiView = dialogView.findViewById<KonfettiView>(R.id.konfettiView)
                    konfettiView.start(
                        Party(
                            speed = 15f,
                            maxSpeed = 20f,
                            damping = 0.9f,
                            spread = 1500,
                            colors = listOf(0xFFE57373.toInt(), 0xFF81C784.toInt(), 0xFF64B5F6.toInt()),
                            size = listOf(Size.SMALL, Size.LARGE),
                            timeToLive = 3000L,
                            position = Position.Relative(0.5, 0.5),
                            emitter = Emitter(duration = 1, TimeUnit.SECONDS).max(100)

                        )
                    )
                }

                override fun onAnimationRepeat(p0: Animation?) {
                }
            })

        }
    }

}

