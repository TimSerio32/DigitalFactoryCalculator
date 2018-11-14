package serio.tim.android.com.digitalfactorycalculator

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import serio.tim.android.com.calculatorlogic.CalculatorLogic
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val RC_SIGN_IN = 1
    private var displayString = ""
    private var displayStringHelper = ""
    private var displayOperators = ""
    private var unary = false
    private var unaryNumber: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null) {
            Log.v("AUTH", auth.currentUser?.email)
        } else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(
                            Arrays.asList(
                                    AuthUI.IdpConfig.FacebookBuilder().build(),
                                    AuthUI.IdpConfig.GoogleBuilder().build()))
                    .build(), RC_SIGN_IN)
        }

        initClickListeners()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putString("displayString", displayString)
        outState?.putString("displayOperators", displayOperators)
        outState?.putString("displayStringHelper", displayStringHelper)
        outState?.putBoolean("unary", unary)
        outState?.putString("unaryNumber", unaryNumber)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        displayString = savedInstanceState?.getString("displayString")!!
        displayOperators = savedInstanceState.getString("displayOperators")!!
        displayStringHelper = savedInstanceState.getString("displayStringHelper")!!
        unary = savedInstanceState.getBoolean("unary")
        unaryNumber = savedInstanceState.getString("unaryNumber")!!

        text_display.text = displayString
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.menu_sign_out -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener {
                            finish()
                        }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == Activity.RESULT_OK) {
                Log.v("AUTH", auth.currentUser?.email)
            } else {
                Log.v("AUTH", "Not authenticated")
            }
        }
    }

    private fun initClickListeners() {
        text_display.bringToFront()

        btn_zero.setOnClickListener {
            numberClick(getString(R.string.zero))
        }

        btn_one.setOnClickListener {
            numberClick(getString(R.string.one))
        }

        btn_two.setOnClickListener {
            numberClick(getString(R.string.two))
        }

        btn_three.setOnClickListener {
            numberClick(getString(R.string.three))
        }

        btn_four.setOnClickListener {
            numberClick(getString(R.string.four))
        }

        btn_five.setOnClickListener {
            numberClick(getString(R.string.five))
        }

        btn_six.setOnClickListener {
            numberClick(getString(R.string.six))
        }

        btn_seven.setOnClickListener {
            numberClick(getString(R.string.seven))
        }

        btn_eight.setOnClickListener {
            numberClick(getString(R.string.eight))
        }

        btn_nine.setOnClickListener {
            numberClick(getString(R.string.nine))
        }

        btn_add.setOnClickListener {
            operatorClick(getString(R.string.add))
        }

        btn_subtract.setOnClickListener {
            operatorClick(getString(R.string.subtract))
        }

        btn_times.setOnClickListener {
            operatorClick(getString(R.string.times))
        }

        btn_divide.setOnClickListener {
            operatorClick(getString(R.string.divide))
        }

        btn_cosine.setOnClickListener {
            unaryOperatorClick(getString(R.string.cos))
        }

        btn_sine.setOnClickListener {
            unaryOperatorClick(getString(R.string.sin))
        }

        btn_tangent.setOnClickListener {
            unaryOperatorClick(getString(R.string.tan))
        }

        btn_equals.setOnClickListener {
            equalClick()
        }

        btn_clear.setOnClickListener {
            clear()
        }
    }

    private fun unaryOperatorClick(operator: String) {
        unary = true
        clear()
        displayString = operator

        text_display.text = "${displayString} ${getString(R.string.open_paren)}"
        displayOperators = operator
    }

    private fun operatorClick(operator: String) {
        unary = false
        displayStringHelper = text_display.text.toString()
        clear()
        displayOperators = operator
    }

    private fun clear() {
        displayString = ""
        text_display.text = ""
    }

    private fun numberClick(number: String) {
        if(unary) {
            unaryNumber += number
            displayString = text_display.text.toString()
            displayString += number
            text_display.text = displayString
        } else {
            displayString = text_display.text.toString()
            displayString += number
            text_display.text = displayString
        }
    }

    private fun equalClick() {
        var result = 0.0
        try {
            if(unary) {
                displayString = text_display.text.toString()
                text_display.text = "${displayString} ${getString(R.string.close_paren)}"
                val num = unaryNumber.toDouble()

                when(displayOperators) {
                    getString(R.string.cos) -> {
                        result = CalculatorLogic.getCosine(num)
                    }

                    getString(R.string.sin) -> {
                        result = CalculatorLogic.getSine(num)
                    }

                    getString(R.string.tan) -> {
                        result = CalculatorLogic.getTangent(num)
                    }
                }
            } else {
                val num1 = displayStringHelper.toDouble()
                val num2 = (text_display.text.toString()).toDouble()
                when(displayOperators) {
                    getString(R.string.add) -> {
                        result = CalculatorLogic.add(num1, num2)
                    }

                    getString(R.string.subtract) -> {
                        result = CalculatorLogic.subtract(num1, num2)
                    }

                    getString(R.string.times) -> {
                        result = CalculatorLogic.multiply(num1, num2)
                    }

                    getString(R.string.divide) -> {
                        result = CalculatorLogic.divide(num1, num2)
                    }
                }
            }
            unaryNumber = ""

            displayString = if(result == Double.NaN) "" else result.toString()
            text_display.text = displayString
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
