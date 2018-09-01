package com.example.xyzreader.network;

import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

interface XyzReaderService {
    @GET(UrlManager.PATH)
    Single<List<Article>> getArticles();
}