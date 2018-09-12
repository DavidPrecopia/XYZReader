package com.example.xyzreader.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import com.example.xyzreader.R;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.model.IModelContract;
import com.example.xyzreader.model.Model;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

final class ArticleViewModel extends AndroidViewModel {

    private final CompositeDisposable disposable;

    private final MutableLiveData<List<Article>> articlesList;
    private final List<Article> offlineArticlesList;
    private final MutableLiveData<String> error;

    private final IModelContract model;

    private int lastSelected;
    private static final int OFFLINE_SELECTED = 100;
    private static final int ARTICLES_SELECTED = 200;


    ArticleViewModel(Application application) {
        super(application);
        this.disposable = new CompositeDisposable();
        this.articlesList = new MutableLiveData<>();
        this.offlineArticlesList = new ArrayList<>();
        this.error = new MutableLiveData<>();
        this.model = Model.getInstance(application);
        loadArticles();
    }


    void loadArticles() {
        lastSelected = ARTICLES_SELECTED;
        disposable.add(model.getArticles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(articleObserver())
        );
    }

    private DisposableSingleObserver<List<Article>> articleObserver() {
        return new DisposableSingleObserver<List<Article>>() {
            @Override
            public void onSuccess(List<Article> articles) {
                ArticleViewModel.this.articlesList.setValue(articles);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
                ArticleViewModel.this.error.setValue(checkErrorMessage(e));
            }

            private String checkErrorMessage(Throwable e) {
                if (e.getMessage().equals(getApplication().getString(R.string.error_msg_no_network_connection))) {
                    return e.getMessage();
                } else {
                    return getApplication().getString(R.string.error_msg_generic);
                }
            }
        };
    }


    void loadOfflineArticles() {
        lastSelected = OFFLINE_SELECTED;
        if (offlineArticlesList.isEmpty()) {
            disposable.add(model.getOfflineArticles()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(offlineObserver())
            );
        } else {
            articlesList.getValue().clear();
            articlesList.setValue(offlineArticlesList);
        }
    }

    private DisposableSubscriber<List<Article>> offlineObserver() {
        return new DisposableSubscriber<List<Article>>() {
            @Override
            public void onNext(List<Article> articles) {
                ArticleViewModel.this.offlineArticlesList.clear();
                ArticleViewModel.this.offlineArticlesList.addAll(articles);
                if (lastSelected == OFFLINE_SELECTED) {
                    ArticleViewModel.this.articlesList.setValue(articles);
                }
            }

            @Override
            public void onError(Throwable t) {
                ArticleViewModel.this.error.setValue(getApplication().getString(R.string.error_msg_generic));
            }

            @Override
            public void onComplete() {
                // N/A
            }
        };
    }


    void refresh() {
        if (lastSelected == ARTICLES_SELECTED) {
            loadArticles();
        } else if (lastSelected == OFFLINE_SELECTED) {
            loadOfflineArticles();
        }
    }


    LiveData<List<Article>> getArticlesList() {
        return articlesList;
    }

    LiveData<String> getError() {
        return error;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
