package com.example.xyzreader.model;

import android.app.Application;

import com.example.xyzreader.database.ArticlesDao;
import com.example.xyzreader.database.ArticlesDatabase;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.network.INetworkClientContract;
import com.example.xyzreader.network.NetworkClient;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

public final class Model implements IModelContract {

    private final INetworkClientContract network;
    private final ArticlesDao articlesDao;


    private static Model model;

    public static Model getInstance(Application application) {
        if (model == null) {
            model = new Model(application);
        }
        return model;
    }

    private Model(Application application) {
        network = NetworkClient.getInstance();
        articlesDao = ArticlesDatabase.getInstance(application).getArticlesDap();
    }


    @Override
    public Single<List<Article>> getArticles() {
        return network.getArticles();
    }


    @Override
    public Maybe<List<Article>> getOfflineArticles() {
        return null;
    }

    @Override
    public Maybe<Article> getSingleOfflineArticle(int id) {
        return null;
    }

    @Override
    public void saveArticleOffline(Article article) {

    }

    @Override
    public void deleteOfflineArticle(int id) {

    }
}
