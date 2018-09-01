package com.example.xyzreader.ui.ArticleList;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.xyzreader.R;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.network.INetworkClientContract;
import com.example.xyzreader.network.NetworkClient;
import com.example.xyzreader.util.NetworkStatusUtil;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

final class ArticleListViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Article>> articlesList;
    private final MutableLiveData<String> error;

    private final INetworkClientContract network;
    private final NetworkStatusUtil networkStatus;

    ArticleListViewModel(Application application) {
        super(application);
        this.articlesList = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
        this.network = NetworkClient.getInstance();
        this.networkStatus = NetworkStatusUtil.getInstance(application);
        init();
    }

    private void init() {
        loadArticles();
    }

    void loadArticles() {
        if (networkStatus.noConnection()) {
            error.setValue(getApplication().getString(R.string.error_msg_no_network_connection));
        } else {
            queryNetwork();
        }
    }

    @SuppressLint("CheckResult")
    private void queryNetwork() {
        network.getArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer());
    }

    private SingleObserver<List<Article>> observer() {
        return new SingleObserver<List<Article>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<Article> articles) {
                ArticleListViewModel.this.articlesList.setValue(articles);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
                ArticleListViewModel.this.error
                        .setValue(getApplication().getString(R.string.error_msg_generic));
            }
        };
    }


    LiveData<List<Article>> getArticlesList() {
        return articlesList;
    }

    LiveData<String> getError() {
        return error;
    }
}
