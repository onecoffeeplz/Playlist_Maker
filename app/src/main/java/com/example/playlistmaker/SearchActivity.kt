package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    private var _binding: ActivitySearchBinding? = null
    private val binding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivitySearchBinding must not be null!")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchToolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.clearButton.setOnClickListener {
            binding.searchbar.text.clear()
            hideKeyboardAndCursor()
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.clearButton.visibility = View.GONE
                } else {
                    val input = s.toString()
                    binding.clearButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.searchbar.addTextChangedListener(textWatcher)
    }

    private fun hideKeyboardAndCursor() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view: View? = currentFocus
        if (view is EditText) view.clearFocus()
        if (view != null) imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_INPUT, binding.searchbar.text.toString())
        outState.putInt(CURSOR_POSITION, binding.searchbar.selectionStart)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.searchbar.setText(savedInstanceState.getString(SEARCH_INPUT, userInput))
        binding.searchbar.setSelection(savedInstanceState.getInt(CURSOR_POSITION, cursorPosition))
    }

    private var userInput: String = ""
    private var cursorPosition: Int = 0

    companion object {
        const val SEARCH_INPUT = "SEARCH_INPUT"
        const val CURSOR_POSITION = "CURSOR_POSITION"
    }

}