package com.example.xyzreader.model;

import android.app.Application;

import com.example.xyzreader.R;
import com.example.xyzreader.database.ArticlesDao;
import com.example.xyzreader.database.ArticlesDatabase;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.network.INetworkClientContract;
import com.example.xyzreader.network.NetworkClient;
import com.example.xyzreader.util.NetworkStatusUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import timber.log.Timber;

public final class Model implements IModelContract {

    private final INetworkClientContract network;
    private final NetworkStatusUtil networkStatus;
    private final String errorMsgNoNetwork;

    private final List<Article> articleListCache;

    private final ArticlesDao articlesDao;


    private static Model model;

    public static Model getInstance(Application application) {
        if (model == null) {
            model = new Model(application);
        }
        return model;
    }

    private Model(Application application) {
        this.network = NetworkClient.getInstance(application);
        this.networkStatus = NetworkStatusUtil.getInstance(application);
        this.errorMsgNoNetwork = application.getString(R.string.error_msg_no_network_connection);
        this.articleListCache = new ArrayList<>();
        this.articlesDao = ArticlesDatabase.getInstance(application).getArticlesDap();
    }


    @Override
    public Single<List<Article>> getArticles() {
        if (articleListCache.isEmpty()) {
            return queryNetwork();
        } else {
            return Single.just(articleListCache);
        }
    }

    private Single<List<Article>> queryNetwork() {
        if (networkStatus.haveConnection()) {
            return network.getArticles()
                    .doOnSuccess(articles -> {
                        articleListCache.clear();
                        articleListCache.addAll(articles);
                    });
        } else {
            return Single.error(new Exception(errorMsgNoNetwork));
        }
    }


    @Override
    public Maybe<List<Article>> getOfflineArticles() {
        return articlesDao.getAllArticles();
    }

    @Override
    public void saveArticleOffline(Article article) {
        long insertResult = articlesDao.insertArticle(article);
        if (insertResult == -1) {
            Timber.e("Error inserting article");
        }
    }

    @Override
    public void deleteOfflineArticle(int id) {
        int deletionResult = articlesDao.deleteArticle(id);
        if (deletionResult == -1) {
            Timber.e("Error deleting article");
        }
    }
}
