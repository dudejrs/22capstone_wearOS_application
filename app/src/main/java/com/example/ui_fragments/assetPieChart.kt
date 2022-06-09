package com.example.ui_fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.data.StockData
import com.example.data.StockDatabase
import com.example.capstone_wearos_applicaition.R
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class assetPieChart : Fragment() {

    var stock_list =  ArrayList<StockData>()
    var stock_list_viewmodel = assetPieChartDataSet()
    var total = 0

    inner class assetPieChartDataSet : ViewModel(){
        var data = MutableLiveData<ArrayList<StockData>>()
        var items = ArrayList<StockData>()

        fun addAll(list : List<StockData>){
            items.addAll(list)
            scope.launch{
                data.postValue(items)

            }
        }
    }
    suspend fun fetchData(context: Context){
        val job = scope.launch{
            val db = StockDatabase.getInstance(context)
            stock_list.addAll(db!!.stockdataDAO().getTopList())
            total = db!!.stockdataDAO().sum()
            stock_list_viewmodel.addAll(stock_list)
            Log.d("Pie","$total, ${stock_list.size}")
        }
        job.join()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview = super.onCreateView(inflater, container, savedInstanceState)
        if (stock_list.size == 0) return rootview

        var entries = ArrayList<Entry>()
        var xlegend = ArrayList<String>()
        var sum = 0
        for (i in 0 until stock_list.size){
            val temp = stock_list[i].price * stock_list[i].holdings
            sum = sum + temp
            entries.add(Entry(temp.toFloat(),i))
            xlegend.add(stock_list[i].stockName)
        }
        entries.add(Entry((total-sum).toFloat(),stock_list.size))
        xlegend.add("기타")

        var piedataset = PieDataSet(entries,"label")

        val chart = rootview?.findViewById<PieChart>(R.id.chart1)
        chart?.data =  PieData(xlegend,piedataset)

        chart?.invalidate()


        return rootview
    }

    companion object{
        val scope = CoroutineScope(Dispatchers.IO)

        fun newInstance(context: Context) =
            assetPieChart().apply {
                scope.launch{fetchData(context)}
            }
    }
}