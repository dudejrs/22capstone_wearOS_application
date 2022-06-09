package com.example.dataModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.data.StockData
import com.example.data.StockHolding
import com.example.data.StockHoldingDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.random.Random

class StockHodingModel(val context : Context) {
    private var repository = StockHoldingDatabase.getInstance(context)
    var notify = MutableLiveData<Boolean>(false);
    var isUpdatedWell = false;
    var stock_holding_list : List<StockHolding>? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init{
        scope.launch {
            makeFakeData()
        }
    }

    suspend fun getData() : List<StockHolding>?{
        if (repository == null) {

            return stock_holding_list
        }

        val job2 = scope.launch {
            stock_holding_list = repository!!.stockHoldingDAO().getAll()
            Log.d("app:stockHoldingModel:getData","notify");
        }
        job2.join()
        isUpdatedWell=true
        notify.postValue(!(notify.value)!!)

        return stock_holding_list;
    }

    suspend fun makeFakeData(){
        var myrandom = Random(LocalDateTime.now().nano)
        val my_list : List<String> = listOf("005930","035420","097950","000720","030000","023530","000100")
        var job1 = scope.launch{
            for (item in my_list) {
                var new_entry = StockHolding(0,item, myrandom.nextInt(0,100))
                repository!!.stockHoldingDAO().insert(new_entry)
            }
        }
        job1.join()

        var job2 = scope.launch {
            stock_holding_list = repository!!.stockHoldingDAO().getAll()
        }
        job2.join()
    }

}