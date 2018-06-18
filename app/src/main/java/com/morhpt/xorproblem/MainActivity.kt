package com.morhpt.xorproblem

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.morhpt.nn.NeuralNetwork
import org.jetbrains.anko.*
import org.jetbrains.anko.design.snackbar
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import org.jetbrains.anko.sdk25.coroutines.onClick


class MainActivity : AppCompatActivity(), AnkoLogger {

    private data class Data(val inputs: Array<Number>, val outputs: Array<Number>)

    private lateinit var main: LinearLayout
    private lateinit var val1: EditText
    private lateinit var val2: EditText
    private lateinit var res: TextView

    private val nn = NeuralNetwork(2, 4, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scrollView {
            main = verticalLayout {

                padding = dip(16)

                linearLayout {
                    verticalLayout {

                        textView("0 XOR 0 = 0")
                        textView("1 XOR 0 = 1")
                        textView("0 XOR 1 = 1")
                        textView("1 XOR 1 = 0")

                    }.lparams(weight = 1f)
                    linearLayout {

                        val1 = editText("1")
                        textView(" XOR ")
                        val2 = editText("1")
                        textView(" =  ")
                        res = textView("0")

                    }.lparams(weight = 1f).applyRecursively { view ->
                        when (view) {
                            is EditText -> {
                                view.setRawInputType(InputType.TYPE_CLASS_NUMBER)
                                view.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))
                            }
                        }
                    }
                }.lparams(width = matchParent)
                linearLayout {
                    button("Train").onClick { train() }
                    button("Predict").onClick { predict() }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun predict() {
        info { "started predicting" }
        val inputs = arrayOf<Number>(val1.text.toString().toInt(), val2.text.toString().toInt())
        val result = nn.predict(inputs)[0]
        val formatedDecimal = DecimalFormat(".##").format(result).toDouble()

        res.text = "$formatedDecimal"
        info { "predicted" }
    }

    private fun train() {
        info { "started training" }
        val dialog = progressDialog(message = "Please wait a bit…", title = "Training")
        val trainN = 214748//3 // 6
        val rand = Random()

        doAsync {
            val testingData = setupTestingData()

            info { "created testing data" }

            for (x in 0..trainN) {
                val random = rand.nextInt(3 + 1)
                val data = testingData[random]
                doAsync {
                    nn.train(data.inputs, data.outputs)
                    dialog.progress = (x * 100) / trainN
                }
            }

            info { "trained" }
            snackbar(main, "Trained", "Predict", { predict() })
            dialog.dismiss()
        }
    }

    // Known data
    private fun setupTestingData(): ArrayList<Data> {
        val testingData = ArrayList<Data>()

        testingData.add(Data(arrayOf(0, 0), arrayOf(0)))
        testingData.add(Data(arrayOf(1, 0), arrayOf(1)))
        testingData.add(Data(arrayOf(0, 1), arrayOf(1)))
        testingData.add(Data(arrayOf(1, 1), arrayOf(0)))

        return testingData
    }
}
