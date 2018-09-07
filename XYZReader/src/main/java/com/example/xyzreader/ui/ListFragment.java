package com.example.xyzreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentListBinding;
import com.example.xyzreader.datamodel.Article;

import java.util.List;

public class ListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private ArticleViewModel viewModel;
    private FragmentListBinding binding;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorTv;


    public ListFragment() {
    }


    static ListFragment getInstance() {
        return new ListFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        getViewReferences();
        initViewModel();
        initSwipeRefreshLayout();
    }

    private void getViewReferences() {
        swipeRefreshLayout = binding.swipeRefreshLayout;
        progressBar = binding.progressBar;
        errorTv = binding.tvError;
    }

    private void initViewModel() {
        ArticleViewModelFactory factory = new ArticleViewModelFactory(getActivity().getApplication());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(ArticleViewModel.class);
        observeArticles();
        observeError();
    }

    private void observeArticles() {
        viewModel.getArticlesList().observe(this, articlesList -> {
            hideLoadingView();
            initRecyclerView(articlesList);
        });
    }

    private void observeError() {
        viewModel.getError().observe(this, this::displayError);
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    private void initRecyclerView(List<Article> articlesList) {
        RecyclerView recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(getDividerItemDecoration(recyclerView, layoutManager));
        recyclerView.setAdapter(new ArticleAdapter(articlesList, (MainActivity) getActivity()));
    }

    private RecyclerView.ItemDecoration getDividerItemDecoration(RecyclerView recyclerView, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(
                recyclerView.getContext(),
                layoutManager.getOrientation()
        );
    }


    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        displayLoadingView();
        viewModel.loadArticles();
    }


    private void displayLoadingView() {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
    }

    private void hideLoadingView() {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    private void displayError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
        errorTv.setVisibility(View.VISIBLE);
        errorTv.setText(errorMessage);
    }
}