package com.example.xyzreader.ui.ArticleList;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ArticleListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    @NonNull
    private final Application application;

    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    ArticleListViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public ArticleListViewModel create(@NonNull Class modelClass) {
        return new ArticleListViewModel(application);
    }
}
