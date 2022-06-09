package com.example.capstone_wearos_applicaition

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.data.StockData
import com.example.data.StockDatabase
//import com.google.common.math.IntMath
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.dataModel.stockDataModel

class MyInterestStockListActivity : FragmentActivity() {
    private lateinit var fragment1 : Fragment
    private lateinit var fragment2 : Fragment
    private lateinit var fragment3 : Fragment
    private var scope = CoroutineScope(Dispatchers.IO)
    private var stock_list : List<StockData>? = null
    private var viewModel : stockDataModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_interest_stock_list)

        viewModel = stockDataModel(applicationContext)

        scope.launch {
            stock_list=viewModel!!.getData()
        }

        if (stock_list == null){

        }


        viewModel!!.notify.observe( this , Observer{
            if(viewModel!!.isUpdatedWell){
                make_list()
                viewModel!!.isUpdatedWell = false
            }
        })

    }


    private fun make_list(){
        Log.d("app:MyInterestStockListActivity:make_list","called")
        scope.launch {
            stock_list = viewModel!!.getData()
        }

        if (stock_list != null) {
            var container = findViewById<RecyclerView>(R.id.rclview).apply {
                isHorizontalFadingEdgeEnabled = true
                isVerticalFadingEdgeEnabled = true
                layoutManager = LinearLayoutManager(this@MyInterestStockListActivity)
                adapter = StockListItemAdapter(applicationContext, stock_list!!, this)
            }

        }
    }

    fun gotoDetails(view: View){
        val intent = Intent(this,StockDetailsActivity::class.java)
        startActivity(intent)
    }
}