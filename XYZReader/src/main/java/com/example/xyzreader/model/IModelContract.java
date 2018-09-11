package com.example.xyzreader.model;

import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

public interface IModelContract {
    Single<List<Article>> getArticles();

    Maybe<List<Article>> getOfflineArticles();

    boolean saveArticleOffline(Article article);

    boolean deleteOfflineArticle(int id);

    Flowable<Boolean> isArticleSavedOffline(int id);
}
