package com.example.xyzreader.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

final class DetailViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    @NonNull
    private final Application application;
    private final int articleId;
    private final String body;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public DetailViewModelFactory(@NonNull Application application, int articleId, String body) {
        super(application);
        this.application = application;
        this.articleId = articleId;
        this.body = body;
    }

    @NonNull
    @Override
    public DetailViewModel create(@NonNull Class modelClass) {
        return new DetailViewModel(application, articleId, body);
    }
}
