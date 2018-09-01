package com.example.xyzreader.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Single;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface ArticlesDao {
    @Query("SELECT * FROM " + DatabaseContract.TABLE_NAME_ARTICLES)
    Single<List<Article>> getAllArticle();

    @Query("SELECT * FROM " + DatabaseContract.TABLE_NAME_ARTICLES + " WHERE " + DatabaseContract.COLUMN_NAME_ID + " = :id")
    Single<Article> getSingleArticle(int id);

    @Insert(onConflict = REPLACE)
    long insertArticle(Article article);

    @Query("DELETE FROM " + DatabaseContract.TABLE_NAME_ARTICLES + " WHERE " + DatabaseContract.COLUMN_NAME_ID + " = :id")
    int deleteArticle(int id);
}
