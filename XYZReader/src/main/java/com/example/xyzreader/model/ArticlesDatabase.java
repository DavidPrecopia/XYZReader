package com.example.xyzreader.model;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.example.xyzreader.datamodel.Article;

@Database(entities = {Article.class}, version = 1, exportSchema = false)
public abstract class ArticlesDatabase extends RoomDatabase {

    private static ArticlesDatabase database;

    private ArticlesDatabase getInstance(Application context) {
        if (database == null) {
            database = Room.databaseBuilder(
                    context,
                    ArticlesDatabase.class,
                    DatabaseContract.DATABASE_NAME
            ).build();
        }
        return database;
    }

    public abstract ArticlesDao getArticlesDap();
}
