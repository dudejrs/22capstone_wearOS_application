package com.example.capstone_wearos_applicaition

//import androidx.wear.compose.material.Card
//import androidx.wear.compose.material.ScalingLazyColumn
//import androidx.wear.compose.material.ScalingLazyListState
//import androidx.wear.compose.material.Text

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.data.StockData
import com.example.data.StockDatabase
import com.example.data.StockIndex
import com.example.data.StockIndexDatabase
import com.example.ui_fragments.stockIndexLineChart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Math.pow
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random


class MainActivity : FragmentActivity() {
//    private lateinit var fragment1 : Fragment

//    private val menuList = listOf<String>("item1","item2","item3")
    private var fragment1 : stockIndexLineChart? = null
    private var fragment2 : stockIndexLineChart? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var rootView = setContentView(R.layout.activity_main)
        var my_manager = supportFragmentManager


        // Notification channel registeration
        var myintent : Intent = Intent(this, NotificationManagerService::class.java)
        myintent.putExtra("action","CREATE_NOTIFICATION_CHANNEL")
        this.startService(myintent)
        // for Noti Test
        runTestBusyService()

//        scope.launch {
//            makeFakeData()
//        }

        fetchURL()
        var volleyIntent = Intent(this,MyVolleyService::class.java)
        volleyIntent.action = "com.example.capstone_wearos_applicaition.action.FETCH_INDEX_VOLLEY_REQUEST"
        this.startService(volleyIntent)

        fragment1 = stockIndexLineChart.newInstance(applicationContext,"코스피")
        fragment2 = stockIndexLineChart.newInstance(applicationContext,"코스닥")
        fragment1!!.indexDataSet_viewmodel.data.observe(this, Observer<ArrayList<StockIndex>>{
            my_manager.beginTransaction()
                .replace(R.id.container,fragment1 as Fragment)
                .replace(R.id.container2,fragment2 as Fragment).commit()
        })
        fragment2!!.indexDataSet_viewmodel.data.observe(this, Observer<ArrayList<StockIndex>>{
            my_manager.beginTransaction()
                .replace(R.id.container,fragment1 as Fragment)
                .replace(R.id.container2,fragment2 as Fragment).commit()
        })
        my_manager.beginTransaction()
            .replace(R.id.container,fragment1 as Fragment)
            .replace(R.id.container2,fragment2 as Fragment).commit()
//        my_manager.beginTransaction().replace(R.id.container,fragment1 as Fragment).commit()
//        my_manager.beginTransaction().replace(R.id.container2,fragment2 as Fragment).commit()




    }

    fun gotoInterests(view:View) {
        val intent = Intent(this,MyInterestStockListActivity::class.java)
        startActivity(intent)

    }

    fun gotoPortfolio(view:View) {
        val intent = Intent(this,MyPortfolioActivity::class.java)
        startActivity(intent)

    }

    suspend fun makeFakeData(){

        val db = StockDatabase.getInstance(applicationContext)
        val my_random = Random(LocalDateTime.now().nano)

        val job1 = scope.launch{
            db!!.stockdataDAO().deleteAll()
            for (i in 0..10) {
                val random_price = (my_random.nextInt(0,10000)) * 10
                val random_gap = (random_price * 0.03*my_random.nextFloat()*pow(-1.0,
                    my_random.nextInt(0,10).toDouble())).toInt()*10

                val new_entry = StockData(
                    stockNumber = my_random.nextInt(0,10000).toString(),
                    stockName = "주식종목$i",
                    price = random_price,
                    bp = random_price - random_gap,
                    holdings = my_random.nextInt(0,1000)
                )
                db!!.stockdataDAO().insert(new_entry)
            }
        }
        job1.join()

        val job2 = scope.launch {
            val stock_list = db!!.stockdataDAO().getAll()
            for ( item in stock_list){
                Log.d("app:main","${item.stockName} ${item.price} ${item.bp}")
            }
        }
        job2.join()
    }

    private fun fetchURL(){
        val queue = Volley.newRequestQueue(this)

        val url = "https://api.odcloud.kr/api/GetMarketIndexInfoService/v1/getStockMarketIndex?resultType=json"
        val serviceKey = "LP%2B25XDXR5dXxFoH0x00vnaiesFh7eG%2ByEqxXu%2FWYI9GSpXhpbzRtDnWGEh5KJKQleaegf49oBIvIM6gJFcnsw%3D%3D"
//        val index_list = arrayListOf("${URLEncoder.encode("코스피","UTF-8")}","${URLEncoder.encode("코스닥 150","UTF-8")}")
//        val index_list = Array<String>(2){"%EC%BD%94%EC%8A%A4%ED%94%BC";"%EC%BD%94%EC%8A%A4%EB%8B%A5"}
        val index_list = arrayListOf("%EC%BD%94%EC%8A%A4%ED%94%BC","%EC%BD%94%EC%8A%A4%EB%8B%A5")
        Log.d("app:main:", "${index_list[0]}, ${index_list[1]}")
        var start_date = now().minusDays(15).format(DateTimeFormatter.BASIC_ISO_DATE)
        var end_date = now().format(DateTimeFormatter.BASIC_ISO_DATE)
        Log.d("app:main:", "${start_date} ${end_date}내에서 fetch함")

        var stringRequest : StringRequest? = null
        var x : String? = null
        for (i in index_list.indices){
            x = "&beginBasDt=${start_date}&endBasDt=${end_date}&idxNm=${index_list[i]}&serviceKey=${serviceKey}"
            Log.d("app:main:","${index_list[i]}, ${url+x}")
            stringRequest = StringRequest(Request.Method.GET,
                url+x,
                {  response ->
                    val job = scope.launch{
                        responsefn(response)
                    }
                },
                {
                    Log.d("app:MainActivity:fetchURL","$it")
                })
            queue.add(stringRequest)
        }

    }

    private fun responsefn(response: String){
        var db = StockIndexDatabase.getInstance(applicationContext)
        val items = JSONObject(response).getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item")

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

            try{
                db!!.StockIndexDAO().insert(new_entry)
                Log.d("app:Mainactivity:responsefn","성공 : items ${i} ${new_entry.index_name} ${new_entry.date.toString()} ${new_entry.clear_point}")
            }catch(e : Exception){
                Log.d("app:MainActivity:responsefn","${e.message}\t ${db!!.StockIndexDAO().size()}")
                Log.d("app:Mainactivity:responsefn","실패 : ${new_entry.index_name}")
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun runTestBusyService(){
        if( !isMyServiceRunning(TestBusyService::class.java)){
                var myintent : Intent = Intent(this, TestBusyService::class.java)
                this.startService(myintent)
                Log.d("app::MainActivity::runTestBusyService","started")
                return
            }
        Log.d("app::MainActivity::runTestBusyService","%b".format(isMyServiceRunning(TestBusyService::class.java)))
    }


}
