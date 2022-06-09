package com.example.capstone_wearos_applicaition

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class MyPortfolioActivity : FragmentActivity() {

    private val jsonList = ArrayList<Int>()
    private val labelList = ArrayList<String>()
    private lateinit var barChart : BarChart
//    private lateinit var minuteTextview : TextView
    private lateinit var fragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_portfolio)


//        setContentView(R.layout.layout_chart)
//        fragment = assetPieChart.newInstance(applicationContext)
//        fragment!!.stock_list_viewmodel.data.observe(this, Observer<ArrayList<StockData>>{
//            supportFragmentManager.beginTransaction().replace(R.id.chart1, fragment as Fragment).commit()
//        })
//        supportFragmentManager.beginTransaction().replace(R.id.chart1, fragment as Fragment).commit()
//

//        barChart = findViewById<BarChart>(R.id.chart1)
//        fragment = stockIndexLineChart()
//        var myManager = supportFragmentManager
//        myManager.beginTransaction().replace(R.id.fragmentContainerView,fragment).commit()
//        drawchart()

    }

    private fun drawchart(){
        labelList.addAll(arrayListOf<String>("일","월","화","수","목","금","토"))
        jsonList.addAll(arrayListOf(10,20,30,40,50,60,70))
        BarChartGraph(labelList,jsonList)
    }

    private fun BarChartGraph(labelList: ArrayList<String>, valList : ArrayList<Int>){
        var entries = ArrayList<BarEntry>()
        var labels = ArrayList<String>()
        for (i in 0 until valList.size){
            entries.add(BarEntry(valList[i].toFloat(),i))
            labels.add(labelList.get(i))
        }
        var depenses = BarDataSet(entries, "일일 사용 시간")
        depenses.axisDependency = YAxis.AxisDependency.LEFT
        depenses.setColors(ColorTemplate.LIBERTY_COLORS)

        barChart.setDescription(" ")
        barChart.setData(BarData(labels, depenses))
        barChart.animateXY(1000,1000)
        barChart.invalidate()

    }
}