package com.explosion204.tabatatimer.ui.activities

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import com.explosion204.tabatatimer.R
import com.explosion204.tabatatimer.ui.Constants.EXTRA_CYCLES
import com.explosion204.tabatatimer.ui.Constants.EXTRA_DESCRIPTION
import com.explosion204.tabatatimer.ui.Constants.EXTRA_ID
import com.explosion204.tabatatimer.ui.Constants.EXTRA_PREP
import com.explosion204.tabatatimer.ui.Constants.EXTRA_REST
import com.explosion204.tabatatimer.ui.Constants.EXTRA_TITLE
import com.explosion204.tabatatimer.ui.Constants.EXTRA_WORKOUT
import com.explosion204.tabatatimer.viewmodels.TimerDetailViewModel
import com.explosion204.tabatatimer.viewmodels.ViewModelFactory
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class TimerDetailActivity : DaggerAppCompatActivity(), View.OnClickListener {
    private lateinit var titleEditText: EditText
    private lateinit var descEditText: EditText
    private lateinit var prepEditText: EditText
    private lateinit var workoutEditText: EditText
    private lateinit var restEditText: EditText
    private lateinit var cyclesEditText: EditText

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

        workoutEditText.setText(intent.getIntExtra(EXTRA_WORKOUT, 0).toString())
        viewModel.workout = workoutEditText.text.toString().toInt()

        restEditText.setText(intent.getIntExtra(EXTRA_REST, 0).toString())
        viewModel.rest = restEditText.text.toString().toInt()

        cyclesEditText.setText(intent.getIntExtra(EXTRA_CYCLES, 0).toString())
        viewModel.cycles = cyclesEditText.text.toString().toInt()
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
        titleEditText.addTextChangedListener(EditTextWatcher(titleEditText))
        descEditText.addTextChangedListener(EditTextWatcher(descEditText))
        prepEditText.addTextChangedListener(EditTextWatcher(prepEditText))
        workoutEditText.addTextChangedListener(EditTextWatcher(workoutEditText))
        restEditText.addTextChangedListener(EditTextWatcher(restEditText))
        cyclesEditText.addTextChangedListener(EditTextWatcher(cyclesEditText))
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        toolbar.setHomeAsUpIndicator(R.drawable.ic_close_24)
        toolbar.setDisplayHomeAsUpEnabled(true)
        menuInflater.inflate(R.menu.detail_menu, menu)
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
            android.R.id.home -> {
                finish()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.prep_decrement -> decrement(prepEditText)
            R.id.prep_increment -> increment(prepEditText)
            R.id.workout_decrement -> decrement(workoutEditText)
            R.id.workout_increment -> increment(workoutEditText)
            R.id.rest_decrement -> decrement(restEditText)
            R.id.rest_increment -> increment(restEditText)
            R.id.cycles_decrement -> decrement(cyclesEditText)
            R.id.cycles_increment -> increment(cyclesEditText)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun increment(editText: EditText) {
        val newVal = editText.text.toString().toInt() + 1

        if (newVal in 0..9999) {
            editText.setText(newVal.toString())
        }
    }

    private fun decrement(editText: EditText) {
        val newVal = editText.text.toString().toInt() - 1

        if (newVal in 0..9999) {
            editText.setText(newVal.toString())
        }
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
                        workoutEditText.setText("0")
                    }
                    R.id.rest_display -> {
                        restEditText.setText("0")
                    }
                    R.id.cycles_display -> {
                        cyclesEditText.setText("0")
                    }
                }
            }
        }
    }

    inner class EditTextWatcher(private val editText: EditText) : TextWatcher {
        override fun afterTextChanged(newText: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val newVal = editText.text.toString()

            if (newVal.isNotEmpty()) {
                when (editText.id) {
                    R.id.timer_title -> viewModel.title = newVal
                    R.id.timer_desc -> viewModel.desc = newVal
                    R.id.prep_display -> viewModel.prep = newVal.toInt()
                    R.id.workout_display -> viewModel.workout = newVal.toInt()
                    R.id.rest_display -> viewModel.rest = newVal.toInt()
                    R.id.cycles_display -> viewModel.cycles = newVal.toInt()
                }
            }
        }

    }
}