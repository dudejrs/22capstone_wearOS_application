package com.example.data

import android.content.Context
import android.util.Log
import androidx.room.*
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime.now
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.Int as Int1

@Entity
data class StockIndex(
    val index_name : String,
    val date : Date,
    val clear_point: Float,
    val fluctuation_ratio : Float,
    val high_point : Float,
    val low_point : Float,
) {
    @PrimaryKey(autoGenerate = false)
    var id : String = index_name + date.toString()
}


@Dao
interface StockIndexDAO{
    @Insert(onConflict=OnConflictStrategy.IGNORE)
    fun insert(data : StockIndex)
    @Update
    fun update(data: StockIndex)
    @Delete
    fun delete(data: StockIndex)
    @Query("SELECT * FROM StockIndex")
    fun getAll() : List<StockIndex>
    @Query("SELECT * FROM StockIndex WHERE index_name = :name")
    fun getAll(name :String) : List<StockIndex>
    @Query("SELECT * FROM StockIndex WHERE index_name = :name and (date between :start and :end)")
    fun getAll(name :String, start : Date, end : Date) : List<StockIndex>
    @Query("SELECT * FROM StockIndex WHERE (date between :start and :end)")
    fun getAll(start : Date, end : Date) : List<StockIndex>
    @Query("DELETE FROM StockIndex")
    fun deleteAll()
    @Query("SELECT COUNT(*) FROM StockIndex")
    fun size() :kotlin.Int
}

@Database(entities = [StockIndex::class], version = 1)
@TypeConverters(RoomTypeConverter::class)
abstract class StockIndexDatabase : RoomDatabase(){
    abstract fun StockIndexDAO() : StockIndexDAO
    companion object{
        private var instance : StockIndexDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : StockIndexDatabase? {
            if (instance == null) {
                synchronized(StockIndexDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        StockIndexDatabase::class.java,
                        "stockIndexDB"
                    ).build()
                }
            }
            return instance
        }
    }
}
//
//object RoomTypeConverter {
//    @TypeConverter
//    fun fromTimestamp(value: Long?): Date? {
//        return value?.let { Date(it) }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date?): Long? {
//        return date?.time
//    }
//}
object RoomTypeConverter {

    private var x = DateTimeFormatter.BASIC_ISO_DATE

    @TypeConverter
    fun fromTimestamp(value: String?): Date? {
        var date = LocalDate.from(x.parse(value))
        Log.d("typeconverter","${date.toString()}")

        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): String? {
        var date_ = date?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
        Log.d("typeconverter","${date_.toString()}")
        return x.format(date_)
    }
}