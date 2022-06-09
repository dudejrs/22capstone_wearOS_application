package com.example.data

import android.content.Context
import androidx.room.*

@Entity
data class StockHolding(
    @PrimaryKey(autoGenerate = true)
    var holdingID : Int,
    var stockNumber : String,
    var holdings : Int
) {

}

@Dao
interface StockHodlingDAO{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(holding : StockHolding)
    @Update
    fun update(holding: StockHolding)
    @Delete
    fun delete(holding: StockHolding)
    @Query("SELECT * FROM StockHolding")
    fun getAll() : List<StockHolding>
    @Query("SELECT * FROM StockHolding WHERE stockNumber = :stockNumber")
    fun get(stockNumber: Int) : StockHolding
    @Query("Delete FROM StockHolding")
    fun deleteAll()
}


@Database(entities=[StockHolding::class], version =1)
abstract class StockHoldingDatabase : RoomDatabase(){
    abstract fun stockHoldingDAO() : StockHodlingDAO
    companion object{
        private var instance : StockHoldingDatabase? = null
        @Synchronized
        fun getInstance(context : Context) : StockHoldingDatabase?{
            if(instance == null){
                synchronized(StockDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StockHoldingDatabase::class.java,
                        "stockDB"
                    ).fallbackToDestructiveMigrationFrom().build()
                }
            }
            return instance
        }
    }
}