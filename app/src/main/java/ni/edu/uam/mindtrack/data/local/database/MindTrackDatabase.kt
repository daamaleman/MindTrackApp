package ni.edu.uam.mindtrack.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ni.edu.uam.mindtrack.data.local.dao.SessionDao
import ni.edu.uam.mindtrack.data.local.entities.SessionEntity

@Database(entities = [SessionEntity::class], version = 1, exportSchema = false)
abstract class MindTrackDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    companion object {
        @Volatile
        private var INSTANCE: MindTrackDatabase? = null

        fun getDatabase(context: Context): MindTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindTrackDatabase::class.java,
                    "mindtrack_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
