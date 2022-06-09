package com.example.capstone_wearos_applicaition

import android.app.Activity
import android.content.res.Resources
//import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.data.StockData
import com.example.data.StockDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDateTime
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

class StockDetailsActivity : Activity() {

    private var stockNumber  = ""
    private val scope = CoroutineScope(Dispatchers.Default)
    private var stock : StockData? = null
    private val custom_format = DecimalFormat("#,###")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var intent = getIntent()
        stockNumber = intent.getStringExtra("stockNumber").toString()

        val parent_heigth = Resources.getSystem().displayMetrics.heightPixels

        setContentView(R.layout.activity_stock_details2)
        findViewById<ConstraintLayout>(R.id.StockDetailContainer).layoutParams.height = parent_heigth
        findViewById<ConstraintLayout>(R.id.StockDetailContainer2).layoutParams.height = parent_heigth
        findViewById<ConstraintLayout>(R.id.StockDetailContainer3).layoutParams.height = parent_heigth
        Log.d("app:StockDetail:onCreate","${stockNumber}")

        var job = scope.launch{
            fetchData()
        }
    }


    suspend fun fetchData(){
        val db = StockDatabase.getInstance(this)!!
        var job = scope.launch{
            stock = db.stockdataDAO().get(stockNumber)
        }
        job.join()
        runOnUiThread{
            updateView()
        }
    }

    private fun updateView(){
        val gap = stock!!.price - stock!!.bp
        val my_random = Random(LocalDateTime.now().nano)
        val low = (max(
            min(stock!!.price, stock!!.bp) - 0.05*gap,
            0.7 * stock!!.bp)/10).roundToInt()*10
        val high = (min(
            1.3 * stock!!.bp,
            max(stock!!.price, stock!!.bp) + 0.05 * gap)/10).roundToInt()*10
        val vpTextList = arrayListOf<String>("매수","중립","매도")
        val flutuationTextList = arrayListOf<String>("고","저")

        findViewById<TextView>(R.id.stockNumber).text = stock?.stockNumber
        findViewById<TextView>(R.id.stockName2).text = stock?.stockName
        findViewById<TextView>(R.id.price_gap).text = "${custom_format.format(stock?.price)} (${custom_format.format(gap)})"
        findViewById<TextView>(R.id.tradeNumber).text = custom_format.format(my_random.nextInt(5000,1000000))
        findViewById<TextView>(R.id.weightChipText).text = vpTextList[my_random.nextInt(0,255)%3]
        findViewById<TextView>(R.id.fluctText).text = flutuationTextList[my_random.nextInt(0,255)%2]
        findViewById<TextView>(R.id.end_base).text = "${custom_format.format(stock?.bp)} / ${custom_format.format(stock?.price)}"
        findViewById<TextView>(R.id.high_low).text = "${custom_format.format(high)} / ${custom_format.format(low)}"
        findViewById<ImageView>(R.id.slider_cursor).translationX = gap.toFloat()/143

    }
}