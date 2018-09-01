package com.example.xyzreader.network;

import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Single;

public interface INetworkClientContract {
    Single<List<Article>> getArticles();
}
