package com.example.xyzreader.ui.ArticleList;

import android.annotation.SuppressLint;
import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.model.IModelContract;
import com.example.xyzreader.model.Model;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

final class ArticleListViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Article>> articlesList;
    private final MutableLiveData<String> error;

    private final IModelContract model;

    ArticleListViewModel(Application application) {
        super(application);
        this.articlesList = new MutableLiveData<>();
        this.error = new MutableLiveData<>();
        this.model = Model.getInstance(application);
        loadArticles();
    }


    @SuppressLint("CheckResult")
    void loadArticles() {
        model.getArticles()
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
                ArticleListViewModel.this.error.setValue(e.getMessage());
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
