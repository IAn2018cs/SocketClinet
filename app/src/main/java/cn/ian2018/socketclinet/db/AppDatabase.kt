package cn.ian2018.socketclinet.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.ian2018.socketclinet.db.dao.MsgInfoDao
import cn.ian2018.socketclinet.db.data.MsgInfo

/**
 * Created by chenshuai on 2020-05-07
 */
@Database(entities = [MsgInfo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun msgInfoDao(): MsgInfoDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDataBase(context)
                        .also {
                            instance = it
                        }
            }
        }

        private fun buildDataBase(context: Context): AppDatabase {
            return Room
                    .databaseBuilder(context, AppDatabase::class.java, "socket-clint.db")
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }
                    })
//                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
        }

//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "ALTER TABLE shipment ADD COLUMN auto_identify INTEGER NOT NULL DEFAULT 0"
//                )
//            }
//        }
//
//        val MIGRATION_2_3 = object : Migration(2, 3) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL(
//                    "ALTER TABLE shipment ADD COLUMN rule TEXT NOT NULL DEFAULT ''"
//                )
//                database.execSQL(
//                    "ALTER TABLE shipment ADD COLUMN result TEXT NOT NULL DEFAULT ''"
//                )
//            }
//        }
    }
}