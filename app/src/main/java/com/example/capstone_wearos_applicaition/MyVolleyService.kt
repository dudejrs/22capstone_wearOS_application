package com.example.capstone_wearos_applicaition

import android.app.IntentService
import android.content.Intent
import android.content.Context
import android.util.Log
import androidx.compose.ui.input.key.Key.Companion.D
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.data.StockHoldingDatabase
import com.example.data.StockIndex
import com.example.dataModel.StockIndexDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

private const val Fetch_INDEX = "com.example.capstone_wearos_applicaition.action.FETCH_INDEX_VOLLEY_REQUEST"
private const val GET_RECENT_STOCK = "com.example.capstone_wearos_application.action.GET_RECENT_STOCK_VOLLEY_REQUEST"

private const val INDEX_API_URI = "https://api.odcloud.kr/api/GetMarketIndexInfoService/v1/getStockMarketIndex?resultType=json"
private const val INDEX_API_URI_SERVICE_KEYS = "LP%2B25XDXR5dXxFoH0x00vnaiesFh7eG%2ByEqxXu%2FWYI9GSpXhpbzRtDnWGEh5KJKQleaegf49oBIvIM6gJFcnsw%3D%3D"
//private const val STOCK_API_URI = "15.164.169.231:3000/stocks/"
private const val STOCK_API_URI = "http://10.0.2.2:3000/stocks/"

class MyVolleyService : IntentService("MyVolleyService") {
    private val scope = CoroutineScope(Dispatchers.IO)
    var ctx : Context? = null

    override fun onCreate() {
        ctx = applicationContext
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {

            Fetch_INDEX -> {
                val url = intent.getStringExtra(INDEX_API_URI)
                val serviceKey = intent.getStringExtra(INDEX_API_URI_SERVICE_KEYS)
                handleFetchIndex(url, serviceKey)
            }

            GET_RECENT_STOCK ->{
                handleGETRecentStock()
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */

    private fun handleFetchIndex(param1: String?, param2: String?) {
//        startActionBaz(ctx, param1!!,param2!!)
    }

    private fun handleGETRecentStock(){
        startGETRecentStock(ctx!!)
    }

    companion object {

        /**
         * Starts this service to perform action Baz with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        @JvmStatic
        fun startFetchIndex(context: Context, url: String, serviceKey: String) {
//            val intent = Intent(context, MyVolleyService::class.java).apply {
//                action = Fetch_INDEX
//                putExtra(INDEX_API_URI, url)
//                putExtra(INDEX_API_URI_SERVICE_KEYS, serviceKey)
//            }
            val queue = Volley.newRequestQueue(context)

            val index_list = arrayListOf("%EC%BD%94%EC%8A%A4%ED%94%BC","%EC%BD%94%EC%8A%A4%EB%8B%A5")
            Log.d("app:main:", "${index_list[0]}, ${index_list[1]}")

            var start_date = LocalDate.now().minusDays(10).format(DateTimeFormatter.BASIC_ISO_DATE)
            var end_date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
            Log.d("app:main:", "${start_date} ${end_date}내에서 fetch함")

            var stringRequest : StringRequest? = null
            var x : String? = null
            for (i in index_list.indices){
                x = "&beginBasDt=${start_date}&endBasDt=${end_date}&idxNm=${index_list[i]}&serviceKey=${serviceKey}"
                Log.d("app:main:","${index_list[i]}, ${url+x}")
                stringRequest = StringRequest(Request.Method.GET,
                    url+x,
                    {  response ->
                        val job = CoroutineScope(Dispatchers.IO).launch{
                            fetchIndexResponsefn(response,context)
                        }
                    },
                    {
                        Log.d("app:MainActivity:fetchURL","$it")
                    })
                queue.add(stringRequest)
            }

//            context.startService(intent)
        }

        private fun fetchIndexResponsefn(response: String, context: Context){
            var viewModel = StockIndexDataModel(context)
            val items = JSONObject(response).getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item")
            val listIndex = listOf<StockIndex>()

            for (i in 0 until items.length()){
                val item = items.getJSONObject(i)
                val date_ = DateTimeFormatter.BASIC_ISO_DATE.parse(item.getString("basDt"))
                val date__ = LocalDate.from(date_)
                val date___ = Date.from(date__.atStartOfDay(ZoneId.systemDefault()).toInstant())

                val new_entry = StockIndex(
                    index_name = item.getString("idxNm"),
                    clear_point = item.getInt("clpr").toFloat(),
                    high_point = item.getInt("hipr").toFloat(),
                    low_point = item.getInt("lopr").toFloat(),
                    fluctuation_ratio = item.getLong("vs").toFloat(),
                    date= date___
                )
                Log.d("debug:","${item.getString("basDt")} ${new_entry.date.toString()}")

                listIndex.plus(new_entry)
            }
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.insertData(listIndex)
            }
        }

        fun startGETRecentStock(context: Context){
            val queue = Volley.newRequestQueue(context)
            var db = StockHoldingDatabase.getInstance(context)
            while(db == null){
                db = StockHoldingDatabase.getInstance(context)
            }
//            val myWatchList = db.stockHoldingDAO().getAll()
            val my_list : List<String> = listOf("005930","035420","097950","000720","030000","023530","000100")
            var stringRequest : StringRequest? = null
            var searchString = ""
            for (item in my_list){
                searchString = "$item?type=recent"
                var url = STOCK_API_URI + searchString
                Log.d("myVolleyService::startGETRecentStock",url)
                stringRequest = StringRequest(Request.Method.GET,url,{response ->
                    Log.d("myVolleyService::startGETRecentStock","response success")
//                    fetchStockResponsefn(response,context)
                },{
                    Log.d("myVolleyService::startGETRecentStock","$it")
                })
                queue.add(stringRequest)
            }

        }

//        private fun fetchStockResponsefn(response: String, context: Context){
//            var viewModel = StockIndexDataModel(context)
//            val items = JSONObject(response)
//            val listIndex = listOf<StockIndex>()
//
//            for (i in 0 until items.length()){
//                val item = items.getJSONObject(i)
//                val date_ = DateTimeFormatter.BASIC_ISO_DATE.parse(item.getString("basDt"))
//                val date__ = LocalDate.from(date_)
//                val date___ = Date.from(date__.atStartOfDay(ZoneId.systemDefault()).toInstant())
//
//                val new_entry = StockIndex(
//                    index_name = item.getString("idxNm"),
//                    clear_point = item.getInt("clpr").toFloat(),
//                    high_point = item.getInt("hipr").toFloat(),
//                    low_point = item.getInt("lopr").toFloat(),
//                    fluctuation_ratio = item.getLong("vs").toFloat(),
//                    date= date___
//                )
//                Log.d("debug:","${item.getString("basDt")} ${new_entry.date.toString()}")
//
//                listIndex.plus(new_entry)
//            }
//            CoroutineScope(Dispatchers.IO).launch {
//                viewModel.insertData(listIndex)
//            }
//        }

    }
}