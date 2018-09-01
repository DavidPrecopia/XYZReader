package com.example.xyzreader.ui.ArticleList;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.ActivityArticleListBinding;
import com.example.xyzreader.databinding.ListItemArticleBinding;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.ui.ArticleDetailActivity;
import com.example.xyzreader.util.GlideApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private ArticleListViewModel viewModel;
    private ActivityArticleListBinding binding;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView errorTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_article_list);
        init();
    }


    private void init() {
        getViewReferences();
        initViewModel();
        initToolbar();
        initSwipeRefreshLayout();
    }

    private void getViewReferences() {
        swipeRefreshLayout = binding.swipeRefreshLayout;
        progressBar = binding.progressBar;
        errorTv = binding.tvError;
    }

    private void initViewModel() {
        ArticleListViewModelFactory factory = new ArticleListViewModelFactory(getApplication());
        viewModel = ViewModelProviders.of(this, factory).get(ArticleListViewModel.class);
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

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        // Using a logo image instead
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    private void initRecyclerView(List<Article> articlesList) {
        RecyclerView recyclerView = binding.recyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_refresh:
                onRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
            }

            private void bindThumbnail(String thumbnailUrl) {
                GlideApp.with(binding.ivThumbnail)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.ic_image_icon_black)
                        .error(R.drawable.ic_image_icon_black)
                        .into(binding.ivThumbnail);
            }

            private void bindPublishedDate(String publishedDate) {
                binding.publishedDate.setText(Html.fromHtml(
                        DateUtils.getRelativeTimeSpanString(
                                parsePublishedDate(publishedDate),
                                System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                                DateUtils.FORMAT_ABBREV_ALL).toString()));
            }

            private long parsePublishedDate(String publishedDate) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            .parse(publishedDate)
                            .getTime();
                } catch (ParseException ex) {
                    Timber.e(ex);
                    Timber.i("passing today's date");
                    return new Date().getTime();
                }
            }


            @Override
            public void onClick(View v) {
                // TODO onClick
            }
        }
    }
}
