package com.example.xyzreader.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.Html;

import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.model.IModelContract;
import com.example.xyzreader.model.Model;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

final class DetailViewModel extends AndroidViewModel {

    private final MutableLiveData<Boolean> isSavedOffline;
    private final MutableLiveData<String> parsedBody;

    private final CompositeDisposable disposable;

    private final IModelContract model;

    DetailViewModel(@NonNull Application application, int articleId, String body) {
        super(application);
        this.isSavedOffline = new MutableLiveData<>();
        this.parsedBody = new MutableLiveData<>();
        this.disposable = new CompositeDisposable();
        this.model = Model.getInstance(application);
        init(articleId, body);
    }

    private void init(int articleId, String body) {
        checkIfSavedOffline(articleId);
        prepareBody(body);
    }


    private void checkIfSavedOffline(int articleId) {
        disposable.add(model.isArticleSavedOffline(articleId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(isSavedOfflineObserver())
        );
    }

    private DisposableSingleObserver<Boolean> isSavedOfflineObserver() {
        return new DisposableSingleObserver<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                DetailViewModel.this.isSavedOffline.setValue(aBoolean);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
            }
        };
    }


    private void prepareBody(String body) {
        disposable.add(bindBody(body)
                .subscribeOn(Schedulers.io())
                .subscribeWith(prepareBodyObserver())
        );
    }

    private Single<String> bindBody(String body) {
        return Single.just(
                Html.fromHtml(body.replaceAll("(\r\n|\n)", "<br />")).toString()
        );
    }

    private DisposableSingleObserver<String> prepareBodyObserver() {
        return new DisposableSingleObserver<String>() {
            @Override
            public void onSuccess(String body) {
                DetailViewModel.this.parsedBody.postValue(body);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e);
            }
        };
    }


    void saveOffline(Article article) {
        disposable.add(Completable.fromCallable(() -> model.saveArticleOffline(article))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }

    void deleteOfflineArticle(Article article) {
        disposable.add(Completable.fromCallable(() -> model.deleteOfflineArticle(article.getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        );
    }


    LiveData<Boolean> getIsSavedOffline() {
        return isSavedOffline;
    }

    LiveData<String> getParsedBody() {
        return parsedBody;
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
