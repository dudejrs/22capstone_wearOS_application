package com.example.dataModel
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.capstone_wearos_applicaition.MyVolleyService
import com.example.data.StockDatabase
import com.example.data.StockData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.random.Random


class stockDataModel(val context : Context) {

    var repository = StockDatabase.getInstance(context)
    var notify = MutableLiveData<Boolean>(false);
    var isUpdatedWell = false;
    var stock_list : List<StockData>? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    init{
        scope.launch {
            makeFakeData()
        }
    }

    suspend fun getData(): List<StockData>? {
        if (repository == null) {
            getServerData()
            return stock_list
        }

        val job2 = scope.launch {
            stock_list = repository!!.stockdataDAO().getAll()
            Log.d("app:stockDataModel:getData","notify");
        }
        job2.join()
        isUpdatedWell=true
        notify.postValue(!(notify.value)!!)
        getServerData()

        return stock_list;
    }

    fun updateData(){
        if (repository == null){

        }
    }

    fun getServerData(){
        var myintent = Intent(context, MyVolleyService::class.java)
        myintent.action = "com.example.capstone_wearos_application.action.GET_RECENT_STOCK_VOLLEY_REQUEST"
        context.startService(myintent)
        return
    }

    fun notifyDataFetch(){
        Log.d("app:stockDataModel","notify");
    }

    suspend fun makeFakeData(){
        val my_random = Random(LocalDateTime.now().nano)
        lateinit var stock_list : List<StockData>

        Log.d("app:stockDataModel:makeFakeData","1")

        val job1 = scope.launch{
            repository!!.stockdataDAO().deleteAll()
            for (i in 0..10) {
                val random_price = (my_random.nextInt(0,10000)) * 10
                val random_gap = (random_price * 0.03*my_random.nextFloat()* Math.pow(-1.0,
                    my_random.nextInt(0, 10).toDouble())).toInt()*10

                val new_entry = StockData(
                    stockNumber = my_random.nextInt(0,10000).toString(),
                    stockName = "주식종목$i",
                    price = random_price,
                    bp = random_price - random_gap,
                    holdings = my_random.nextInt(0,1000)
                )
                repository!!.stockdataDAO().insert(new_entry)
            }
        }
        job1.join()


        val job2 = scope.launch {
            stock_list = repository!!.stockdataDAO().getAll()
            for ( item in stock_list){
                Log.d("app:main","${item.stockName} ${item.price} ${item.bp}")
            }
        }
        job2.join()
    }

}