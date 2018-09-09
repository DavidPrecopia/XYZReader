package com.example.xyzreader.model;

import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

public interface IModelContract {
    Single<List<Article>> getArticles();

    Maybe<List<Article>> getOfflineArticles();

    void saveArticleOffline(Article article);

    void deleteOfflineArticle(int id);
}
