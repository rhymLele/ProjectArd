package com.example.projectard.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.projectard.dao.NoteDao;
import com.example.projectard.entity.Note;

@Database(entities = Note.class,version = 2,exportSchema = false)
public abstract class NoteDatabases extends RoomDatabase {

    private static NoteDatabases noteDatabases;
    public static synchronized NoteDatabases getNoteDatabases(Context context){
        if(noteDatabases==null){
            noteDatabases= Room.databaseBuilder(context,NoteDatabases.class,"notes_db").build();
        }return noteDatabases;
    }
    public abstract NoteDao noteDao();
//    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            // Thêm cột reminder vào bảng notes
//            database.execSQL("ALTER TABLE notes ADD COLUMN reminder TEXT");
//        }
//    };

}
