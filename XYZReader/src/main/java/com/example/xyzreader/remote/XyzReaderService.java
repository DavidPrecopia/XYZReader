package com.example.xyzreader.remote;

import com.example.xyzreader.datamodel.Article;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

interface XyzReaderService {
    @GET(UrlManager.PATH)
    Single<List<Article>> getArticles();
}