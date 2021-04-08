package com.example.exampleandroidachitecturecomp;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class},version=1)
public abstract class Notedb extends RoomDatabase {

    private static Notedb instance;

    public abstract NoteDao noteDao();

//synchronized means that only one thread will be able to access this instance at a time
//    this way there will be no multiple instances of db being used at the same time

    public static synchronized Notedb getInstance(Context context){
        if(instance==null)
        {
            instance= Room.databaseBuilder(context.getApplicationContext(),
                    Notedb.class,"note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }

        return instance;
    }

    private static RoomDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void,Void,Void>{
        private NoteDao noteDao;
        private PopulateDbAsyncTask(Notedb notedb){
            this.noteDao= notedb.noteDao();
        }
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("title 1","description 1",1));
            noteDao.insert(new Note("title 2","description 2",2));
            noteDao.insert(new Note("title 3","description 3",3));
            return null;
        }
    }
}
