package com.explosion204.tabatatimer.ui.activities

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.Constants.EXTRA_COLOR
import com.explosion204.tabatatimer.Constants.EXTRA_CYCLES
import com.explosion204.tabatatimer.Constants.EXTRA_DESCRIPTION
import com.explosion204.tabatatimer.Constants.EXTRA_ID
import com.explosion204.tabatatimer.Constants.EXTRA_PREP
import com.explosion204.tabatatimer.Constants.EXTRA_REST
import com.explosion204.tabatatimer.Constants.EXTRA_TITLE
import com.explosion204.tabatatimer.Constants.EXTRA_WORKOUT
import com.explosion204.tabatatimer.viewmodels.TimerDetailViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import petrov.kristiyan.colorpicker.ColorPicker
import javax.inject.Inject

class TimerDetailActivity : DaggerAppCompatActivity(), View.OnClickListener {
    private lateinit var titleEditText: EditText
    private lateinit var descEditText: EditText
    private lateinit var prepEditText: EditText
    private lateinit var workoutEditText: EditText
    private lateinit var restEditText: EditText
    private lateinit var cyclesEditText: EditText
    private lateinit var chosenColorTextVew: TextView

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel : TimerDetailViewModel by viewModels {
        viewModelFactory
    }

    private lateinit var toolbar: ActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer_detail)

        toolbar = supportActionBar!!

        titleEditText = findViewById(R.id.timer_title)
        descEditText = findViewById(R.id.timer_desc)
        prepEditText = findViewById(R.id.prep_display)
        workoutEditText = findViewById(R.id.workout_display)
        restEditText = findViewById(R.id.rest_display)
        cyclesEditText = findViewById(R.id.cycles_display)
        chosenColorTextVew = findViewById(R.id.chosen_color)

        title = if (intent.hasExtra(EXTRA_ID)) {
            getString(R.string.edit_timer)
        } else {
            getString(R.string.new_timer)
        }

        initViewModel()
        setOnFocusChangedListeners()
        setOnClickListeners()
        setOnEditTextChangedListeners()
    }

    private fun initViewModel() {
        viewModel.id = intent.getIntExtra(EXTRA_ID, 0)

        titleEditText.setText(intent.getStringExtra(EXTRA_TITLE))
        viewModel.title = titleEditText.text.toString()

        descEditText.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
        viewModel.desc = descEditText.text.toString()

        prepEditText.setText(intent.getIntExtra(EXTRA_PREP, 0).toString())
        viewModel.prep = prepEditText.text.toString().toInt()

        workoutEditText.setText(intent.getIntExtra(EXTRA_WORKOUT, 1).toString())
        viewModel.workout = workoutEditText.text.toString().toInt()

        restEditText.setText(intent.getIntExtra(EXTRA_REST, 0).toString())
        viewModel.rest = restEditText.text.toString().toInt()

        cyclesEditText.setText(intent.getIntExtra(EXTRA_CYCLES, 1).toString())
        viewModel.cycles = cyclesEditText.text.toString().toInt()

        viewModel.color = intent.getIntExtra(EXTRA_COLOR, -10354450) // default blue color
        chosenColorTextVew.setBackgroundColor(viewModel.color)
    }

    private fun setOnClickListeners() {
        findViewById<Button>(R.id.prep_increment).setOnClickListener(this)
        findViewById<Button>(R.id.prep_decrement).setOnClickListener(this)
        findViewById<Button>(R.id.workout_increment).setOnClickListener(this)
        findViewById<Button>(R.id.workout_decrement).setOnClickListener(this)
        findViewById<Button>(R.id.rest_increment).setOnClickListener(this)
        findViewById<Button>(R.id.rest_decrement).setOnClickListener(this)
        findViewById<Button>(R.id.cycles_increment).setOnClickListener(this)
        findViewById<Button>(R.id.cycles_decrement).setOnClickListener(this)
    }

    private fun setOnFocusChangedListeners() {
        prepEditText.onFocusChangeListener = OnEditTextFocusChangeListener()
        workoutEditText.onFocusChangeListener = OnEditTextFocusChangeListener()
        restEditText.onFocusChangeListener = OnEditTextFocusChangeListener()
        cyclesEditText.onFocusChangeListener = OnEditTextFocusChangeListener()
    }

    private fun setOnEditTextChangedListeners() {
        titleEditText.addTextChangedListener(object : TextValidator(titleEditText) {
            override fun validate(newText: String): Boolean {
                viewModel.title = newText
                return true
            }
        })

        descEditText.addTextChangedListener(object : TextValidator(titleEditText) {
            override fun validate(newText: String): Boolean {
                viewModel.desc = newText
                return true
            }
        })

        prepEditText.addTextChangedListener(object : TextValidator(prepEditText) {
            override fun validate(newText: String): Boolean {
                val newVal = newText.toInt()
                if (newVal in 0..9999) {
                    viewModel.prep = newVal

                    return true
                }

                return false
            }
        })

        workoutEditText.addTextChangedListener(object : TextValidator(workoutEditText) {
            override fun validate(newText: String): Boolean {
                val newVal = newText.toInt()
                if (newVal in 1..9999) {
                    viewModel.workout = newVal

                    return true
                }

                return false
            }
        })

        restEditText.addTextChangedListener(object : TextValidator(restEditText) {
            override fun validate(newText: String): Boolean {
                val newVal = newText.toInt()
                if (newVal in 0..9999) {
                    viewModel.rest = newVal

                    return true
                }

                return false
            }
        })

        cyclesEditText.addTextChangedListener(object : TextValidator(cyclesEditText) {
            override fun validate(newText: String): Boolean {
                val newVal = newText.toInt()
                if (newVal in 1..99) {
                    viewModel.cycles = newVal

                    return true
                }

                return false
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbar.setHomeAsUpIndicator(R.drawable.ic_close_24)
        toolbar.setDisplayHomeAsUpEnabled(true)
        menuInflater.inflate(R.menu.detail_menu, menu)
        menu?.getItem(0)?.isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                if (viewModel.saveTimer()) {
                    finish()
                }

                return true
            }
            R.id.color -> {
                val colorPicker = ColorPicker(this)
                colorPicker.setColors(
                    Color.parseColor("#c02519"), // red
                    Color.parseColor("#00af37"), // green
                    Color.parseColor("#6200EE") // blue
                )
                    .setColumns(3)
                    .setRoundColorButton(true)
                    .setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
                        override fun onChooseColor(position: Int, color: Int) {
                            chosenColorTextVew.setBackgroundColor(color)
                            viewModel.color = color

                            colorPicker.dismissDialog()
                        }

                        override fun onCancel() {

                        }

                    })
                    .show()
            }
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.prep_decrement ->
                prepEditText.setText((prepEditText.text.toString().toInt() - 1).toString())
            R.id.prep_increment ->
                prepEditText.setText((prepEditText.text.toString().toInt() + 1).toString())
            R.id.workout_decrement ->
                workoutEditText.setText((workoutEditText.text.toString().toInt() - 1).toString())
            R.id.workout_increment ->
                workoutEditText.setText((workoutEditText.text.toString().toInt() + 1).toString())
            R.id.rest_decrement ->
                restEditText.setText((restEditText.text.toString().toInt() - 1).toString())
            R.id.rest_increment ->
                restEditText.setText((restEditText.text.toString().toInt() + 1).toString())
            R.id.cycles_decrement ->
                cyclesEditText.setText((cyclesEditText.text.toString().toInt() - 1).toString())
            R.id.cycles_increment ->
                cyclesEditText.setText((cyclesEditText.text.toString().toInt() + 1).toString())
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        if (viewModel.title.isNotEmpty() || viewModel.desc.isNotEmpty()) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(getString(R.string.discard_changes))
                .setPositiveButton(getString(R.string.discard)) { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
                .setCancelable(true)
                .create()
                .show()
        }
        else {
            super.onBackPressed()
        }
    }


    inner class OnEditTextFocusChangeListener : View.OnFocusChangeListener {
        override fun onFocusChange(view: View, hasFocus: Boolean) {
            val editText = view as EditText
            if (editText.text.isEmpty()) {
                when (view.id) {
                    R.id.prep_display -> {
                        prepEditText.setText("0")
                    }
                    R.id.workout_display -> {
                        workoutEditText.setText("1")
                    }
                    R.id.rest_display -> {
                        restEditText.setText("0")
                    }
                    R.id.cycles_display -> {
                        cyclesEditText.setText("1")
                    }
                }
            }
        }
    }

    abstract class TextValidator(private val editText: EditText) : TextWatcher {
        private var oldText = ""

        abstract fun validate(newText: String) : Boolean

        override fun afterTextChanged(s: Editable) {
            if (s.isNotEmpty()) {
                if (!validate(s.toString())) {
                    editText.removeTextChangedListener(this)
                    editText.setText(oldText)
                    editText.addTextChangedListener(this)
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            oldText = editText.text.toString()
        }


        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }
}