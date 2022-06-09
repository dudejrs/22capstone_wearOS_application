package com.example.ui_fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.data.StockIndex
import com.example.data.StockIndexDatabase
import com.example.capstone_wearos_applicaition.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
//import com.google.android.material.internal.ContextUtils.getActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate.now
import java.time.ZoneOffset
import java.util.*
import kotlin.math.roundToInt


class stockIndexLineChart : Fragment() {

//    private var scope = CoroutineScope(Dispatchers.IO)
    private var indexName : String? = null
    var indexDataSet_viewmodel = IndexLineChartDataSet()
    var indexDataSet = ArrayList<StockIndex>()
    var db : StockIndexDatabase? = null


    inner class IndexLineChartDataSet : ViewModel(){
        var data = MutableLiveData<ArrayList<StockIndex>>()
        var items = ArrayList<StockIndex>()

        fun addAll(list:List<StockIndex>){
            items.addAll(list)
            scope.launch{
                data.postValue(items)
                Log.d("app:viemodel","postvalue")
            }
        }
    }



    suspend fun fetchData(indexName : String, context : Context){
        val job = scope.launch{
            db = StockIndexDatabase.getInstance(context)
//               val start = Date.from(now().minusDays(7).atStartOfDay().toInstant(
//                   ZoneOffset.UTC))
//               val end = Date.from(now().atStartOfDay().toInstant(
//                   ZoneOffset.UTC))

            val start = Date.from(now().minusDays(12).atStartOfDay().toInstant(ZoneOffset.UTC))
            val end = Date()

            val data_fetched =  db!!.StockIndexDAO().getAll(indexName, start, end)
//               val data_fetched = db!!.StockIndexDAO().getAll()
            for (i in data_fetched.indices){
                Log.d("app.stockIndexChart:fetchData","${data_fetched[i].date.toString()}")
                Log.d("app.stockIndexChart:fetchData","${data_fetched[i].date < end} and ${data_fetched[i].date > start}")
            }
            indexDataSet.addAll(data_fetched)
            Log.d("app.stockIndexChart:fetchData", "${start.toString()} ${end.toString()}")
            Log.d("app:stockIndexChart:fetchData","${indexName} : ${data_fetched.size}")
            Log.d("app:stockIndexChart:fetchData","\ndata fetched : ${data_fetched.size}\n stockIndexData: ${indexDataSet.size}")
            indexDataSet_viewmodel.addAll(indexDataSet)
        }
        job.join()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
//        var dataObject = arrayListOf(1f,2f,3f,4f,5f)
        val root = inflater.inflate(R.layout.fragment_stock_index_line_chart, container, false)
        var dataObject = indexDataSet
        var entries = arrayListOf<Entry>()
        var xlegend = arrayListOf<String>()


        if (dataObject.size == 0){
            return root
        }

        var size = dataObject.size
        var gap = (dataObject[size-1].clear_point - dataObject[size-2].clear_point).toInt()
        var ratio = (gap/(dataObject[size-2].clear_point)*100f).roundToInt().toFloat()/100f
        root.findViewById<TextView>(R.id.indexName).text = indexName
        root.findViewById<TextView>(R.id.index_point).text = dataObject.last().clear_point.toString()
        root.findViewById<TextView>(R.id.indexPoint_Gap).text = "$gap ($ratio%)"

        Log.d("app:render", "${dataObject.size}")

        for (i in 0 until dataObject.size) {
            entries.add(Entry(dataObject[i].clear_point,i))
            xlegend.add("")
            Log.d("app:stockline:","${dataObject[i].clear_point}")
//            entries.add(Entry(dataObject[i],i))
        }

        var line_dataset = LineDataSet(entries, "Label")


        val chart = root.findViewById<LineChart>(R.id.line_chart)
        chart.data = LineData(xlegend,line_dataset)
        settingChartStyle(chart,line_dataset)

        chart.invalidate()

        return root
    }

    fun settingChartStyle(chart : LineChart, line_dataset : LineDataSet){

        line_dataset.color = ColorTemplate.rgb(Integer.toHexString(Color.parseColor("#ffcc00")))
        line_dataset.setCircleColor( line_dataset.color)
        line_dataset.circleRadius = 2f
        line_dataset.setDrawValues(false)


        chart.setDrawBorders(false)
        chart.setDrawGridBackground(false)

        chart.legend.isEnabled = false
        chart.isClickable = false
        chart.isDoubleTapToZoomEnabled = false
        chart.axisLeft.setDrawGridLines(false)
        chart.axisLeft.setDrawLabels(false)
        chart.axisLeft.setDrawAxisLine(false)

        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.setDrawLabels(false)
        chart.xAxis.setDrawAxisLine(false)

        chart.axisRight.setDrawGridLines(false)
        chart.axisRight.setDrawLabels(false)
        chart.axisRight.setDrawAxisLine(false)

        chart.setDescription(null)
    }



    companion object {

        var scope = CoroutineScope(Dispatchers.IO)

        fun newInstance(context: Context,  indexName: String) =
            stockIndexLineChart().apply {
                this.indexName = indexName
                scope.launch{fetchData(indexName,context)}
                Log.d("main:sto","${indexDataSet.size}")
            }

    }
}