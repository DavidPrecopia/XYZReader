package com.example.xyzreader.model;

import android.app.Application;

import com.example.xyzreader.R;
import com.example.xyzreader.database.ArticlesDao;
import com.example.xyzreader.database.ArticlesDatabase;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.network.INetworkClientContract;
import com.example.xyzreader.network.NetworkClient;
import com.example.xyzreader.util.NetworkStatusUtil;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;

public final class Model implements IModelContract {

    private final INetworkClientContract network;
    private final NetworkStatusUtil networkStatus;
    private final String errorMsgNoNetwork;

    private final ArticlesDao articlesDao;


    private static Model model;

    public static Model getInstance(Application application) {
        if (model == null) {
            model = new Model(application);
        }
        return model;
    }

    private Model(Application application) {
        this.network = NetworkClient.getInstance();
        this.networkStatus = NetworkStatusUtil.getInstance(application);
        this.errorMsgNoNetwork = application.getString(R.string.error_msg_no_network_connection);
        this.articlesDao = ArticlesDatabase.getInstance(application).getArticlesDap();
    }


    @Override
    public Single<List<Article>> getArticles() {
        if (networkStatus.noConnection()) {
            return Single.error(new Exception(errorMsgNoNetwork));
        } else {
            return network.getArticles();
        }
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
