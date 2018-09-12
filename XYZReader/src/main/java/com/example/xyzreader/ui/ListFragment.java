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
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

public class ListFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener {

    private ArticleViewModel viewModel;
    private FragmentListBinding binding;

    private FloatingActionMenu floatingActionMenu;
    private RecyclerView recyclerView;
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
        initFab();
    }

    private void getViewReferences() {
        floatingActionMenu = binding.fabBase;
        recyclerView = binding.recyclerView;
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

    /**
     * Initializing RecyclerView once data is available ensures
     * scroll position will be restore on rotation
     */
    private void observeArticles() {
        viewModel.getArticlesList().observe(this, articlesList -> {
            hideLoadingView();
            if (articlesList == null || articlesList.isEmpty()) {
                displayError(getString(R.string.error_msg_no_articles));
            } else {
                initRecyclerView(articlesList);
            }
        });
    }

    private void observeError() {
        viewModel.getError().observe(this, this::displayError);
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initFab() {
        floatingActionMenu.setClosedOnTouchOutside(true);
        floatingActionMenu.setIconAnimated(false);
        bindFabIcons();
        fabScrollListener();
        fabClickListeners();
    }

    /**
     * Need to set the icons here because these elements
     * do not support `app:srcCompat`
     */
    private void bindFabIcons() {
        binding.fabSortOffline.setImageResource(R.drawable.ic_cloud_done_white_18dp);
        binding.fabSortArticles.setImageResource(R.drawable.ic_articles_list_18dp);
    }

    private void fabScrollListener() {
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    floatingActionMenu.hideMenuButton(true);
                } else if (dy < 0) {
                    floatingActionMenu.showMenuButton(true);
                }
            }
        });
    }

    private void fabClickListeners() {
        binding.fabSortOffline.setOnClickListener(view -> {
            commonFabClickListenerActions();
            viewModel.loadOfflineArticles();
        });
        binding.fabSortArticles.setOnClickListener(view -> {
            commonFabClickListenerActions();
            viewModel.loadArticles();
        });
    }

    private void commonFabClickListenerActions() {
        displayLoadingView();
        floatingActionMenu.close(false);
    }


    private void initRecyclerView(List<Article> articlesList) {
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
        viewModel.refresh();
    }


    private void displayLoadingView() {
        errorTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
    }

    private void hideLoadingView() {
        errorTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void displayError(String errorMessage) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        errorTv.setVisibility(View.VISIBLE);
        errorTv.setText(errorMessage);
    }


    final class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

        private final List<Article> articlesList;

        private final OnClickListFragment onClickListFragment;


        ArticleAdapter(List<Article> articlesList, OnClickListFragment onClickListFragment) {
            this.articlesList = articlesList;
            this.onClickListFragment = onClickListFragment;
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

            private final ListItemArticleBinding binding;

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
                        .placeholder(R.drawable.ic_image_icon_black_24dp)
                        .error(R.drawable.ic_image_icon_black_24dp)
                        .into(binding.ivThumbnail);
            }

            private void bindPublishedDate(String publishedDate) {
                binding.tvPublishedDate.setText(
                        FormatDate.getFormattedDate(publishedDate)
                );
            }


            @Override
            public void onClick(View v) {
                onClickListFragment.openDetailFragment(getAdapterPosition());
            }
        }
    }


    public interface OnClickListFragment {
        void openDetailFragment(int articleIndex);
    }
}