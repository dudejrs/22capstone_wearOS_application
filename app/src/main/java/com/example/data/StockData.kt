package com.example.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec

@Entity
data class StockData(
    @PrimaryKey(autoGenerate = false)
    var stockNumber : String,
    var stockName : String,
    var price : Int,
    var bp : Int,
    @ColumnInfo(defaultValue = "0")
    var holdings : Int
){

}

@Dao
interface StockDataDAO{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(data : StockData)
    @Update
    fun update(data: StockData)
    @Delete
    fun delete(data: StockData)
    @Query("SELECT * FROM StockData")
    fun getAll() : List<StockData>
    @Query("SELECT * FROM StockData WHERE stockNumber = :stockNumber")
    fun get(stockNumber: String) : StockData
    @Query("Delete FROM StockData")
    fun deleteAll()
    @Query("SELECT * FROM StockData ORDER BY holdings*price LIMIT 5")
    fun getTopList() : List<StockData>
    @Query("SELECT SUM(holdings*price) FROM StockData")
    fun sum() : Int
}


@Database(entities=[StockData::class], version = 2)
abstract class StockDatabase : RoomDatabase(){
    abstract fun stockdataDAO() : StockDataDAO
    companion object{
        private var instance : StockDatabase? = null
        @Synchronized
        fun getInstance(context : Context) : StockDatabase?{
            if(instance == null){
                synchronized(StockDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StockDatabase::class.java,
                        "stockDB"
                    ).fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }
    }
}