package com.example.data

import android.content.Context
import androidx.room.*

@Entity
data class WatchList(
    @PrimaryKey(autoGenerate = true)
    var watchID : Boolean,
    var stockNumber : Int,
    var underLine : Float,
    var upperLine : Float
) {

}


@Dao
interface WatchListDAO{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data : WatchList)
    @Update
    fun update(data: WatchList)
    @Delete
    fun delete(data: WatchList)
    @Query("SELECT * FROM WatchList")
    fun getAll() : List<WatchList>
    @Query("Delete FROM WatchList")
    fun deleteAll()
}


@Database(entities=[WatchList::class], version = 1)
abstract class WatchListDatabase : RoomDatabase(){
    abstract fun stockdataDAO() : WatchListDAO
    companion object{
        private var instance : WatchListDatabase? = null
        @Synchronized
        fun getInstance(context : Context) : WatchListDatabase?{
            if(instance == null){
                synchronized(StockDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WatchListDatabase::class.java,
                        "stockDB"
                    ).build()
                }
            }
            return instance
        }
    }
}