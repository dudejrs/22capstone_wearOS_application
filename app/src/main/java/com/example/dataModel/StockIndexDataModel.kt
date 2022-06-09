package com.example.dataModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstone_wearos_applicaition.MyVolleyService
import com.example.data.StockIndex
import com.example.data.StockIndexDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StockIndexDataModel(val context: Context) {
    var repository = StockIndexDatabase.getInstance(context)
    var notify = MutableLiveData<Boolean>(false)
    var isUpdated = false
    var indexList : List<StockIndex>? = null
    private val scope = CoroutineScope(Dispatchers.IO)


    suspend fun getData() : List<StockIndex>?{
        if (repository ==null){
            return indexList
        }

        val job2 = scope.launch {
            indexList = repository!!.StockIndexDAO().getAll()
            Log.d("app:stockIndexDataModel:getData","notify");
        }
        job2.join()
        isUpdated=true
        notify.postValue(!(notify.value)!!)

        return indexList;

    }


    suspend fun updateData(indexList : List<StockIndex>){
        if (repository ==null){
            return
        }
        val job2 = scope.launch {
            for (data in indexList) {
                repository!!.StockIndexDAO().update(data)
            }
        }
        job2.join()
    }

    suspend fun insertData(indexList : List<StockIndex>){
        if (repository ==null){
            return
        }
        val job2 = scope.launch {
            for (data in indexList) {
                repository!!.StockIndexDAO().insert(data)
            }
        }
        job2.join()
    }




}