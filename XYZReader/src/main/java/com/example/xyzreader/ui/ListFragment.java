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
import com.example.xyzreader.databinding.ListItemArticleBinding;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.util.FormatDate;
import com.example.xyzreader.util.GlideApp;

import java.util.List;

public class ListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private OnClickListFragment onClickListFragment;

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
        initClickListener();
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

    private void initClickListener() {
        onClickListFragment = (MainActivity) getActivity();
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
        recyclerView.setAdapter(new ArticleAdapter(articlesList));
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


    @Override
    public void onDetach() {
        super.onDetach();
        onClickListFragment = null;
    }


    private class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {
        private final List<Article> articlesList;

        ArticleAdapter(List<Article> articlesList) {
            this.articlesList = articlesList;
        }

        @NonNull
        @Override
        public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ArticleViewHolder(
                    DataBindingUtil.inflate(
                            LayoutInflater.from(parent.getContext()), R.layout.list_item_article, parent, false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
            holder.bindView(
                    articlesList.get(holder.getAdapterPosition())
            );
        }

        @Override
        public int getItemCount() {
            return articlesList.size();
        }


        class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private ListItemArticleBinding binding;

            ArticleViewHolder(ListItemArticleBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                binding.getRoot().setOnClickListener(this);
            }


            private void bindView(Article article) {
                binding.setArticle(article);
                bindThumbnail(article.getThumbnailUrl());
                bindPublishedDate(article.getPublishedDate());
                binding.executePendingBindings();
            }

            private void bindThumbnail(String thumbnailUrl) {
                GlideApp.with(binding.ivThumbnail)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.ic_image_icon_black)
                        .error(R.drawable.ic_image_icon_black)
                        .into(binding.ivThumbnail);
            }

            private void bindPublishedDate(String publishedDate) {
                binding.publishedDate.setText(
                        FormatDate.getFormattedDate(publishedDate)
                );
            }


            @Override
            public void onClick(View v) {
                onClickListFragment.openDetailFragment(getAdapterPosition());
            }
        }
    }


    interface OnClickListFragment {
        void openDetailFragment(int articleIndex);
    }
}