package com.example.dji_mavic_app

import android.content.Context
import androidx.room.*

@Entity
data class FlightLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val durationSeconds: Long,
    val maxAltitude: Double
)

@Dao
interface FlightDao {
    @Query("SELECT * FROM FlightLog ORDER BY id DESC")
    fun getAll(): List<FlightLog>

    @Insert
    fun insert(log: FlightLog)
}

// 🔴 FIX APPLIED HERE: exportSchema = false
@Database(entities = [FlightLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flightDao(): FlightDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drone_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}