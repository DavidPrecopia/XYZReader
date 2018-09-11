package com.example.xyzreader.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Maybe;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ArticlesDao {
    @Query("SELECT * FROM " + DatabaseContract.TABLE_NAME_ARTICLES)
    Maybe<List<Article>> getAllArticles();

    @Insert(onConflict = REPLACE)
    long insertArticle(Article article);

    @Query("DELETE FROM " + DatabaseContract.TABLE_NAME_ARTICLES + " WHERE " + DatabaseContract.COLUMN_NAME_ID + " = :id")
    int deleteArticle(int id);

    @Query("SELECT * FROM " + DatabaseContract.TABLE_NAME_ARTICLES + " WHERE " + DatabaseContract.COLUMN_NAME_ID + " = :id LIMIT 1")
    int isArticleSavedOffline(int id);
}
