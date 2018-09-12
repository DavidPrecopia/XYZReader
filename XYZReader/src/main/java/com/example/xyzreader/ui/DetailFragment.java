package com.example.xyzreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentDetailBinding;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.util.FormatDate;
import com.example.xyzreader.util.GlideApp;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;

    private ArticleViewModel articleViewModel;
    private DetailViewModel detailViewModel;

    private Article article;
    private static final String ARGUMENT_ID_ARTICLE_INDEX = "article_index_id";

    private ImageView offlineImageView;
    private TextView bodyTextView;
    private ProgressBar bodyProgressBar;


    public DetailFragment() {
    }


    static DetailFragment newInstance(int articleIndex) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT_ID_ARTICLE_INDEX, articleIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArticleViewModel();
        initArticleField();
    }

    private void initArticleViewModel() {
        ArticleViewModelFactory articleFactory = new ArticleViewModelFactory(getActivity().getApplication());
        articleViewModel = ViewModelProviders.of(getActivity(), articleFactory).get(ArticleViewModel.class);
    }


    private void initArticleField() {
        int articleIndex = getArguments().getInt(ARGUMENT_ID_ARTICLE_INDEX);
        article = articleViewModel.getArticlesList().getValue().get(articleIndex);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        initViewReferences();
        initDetailViewModel();
        setUpToolbar();
        initFab();
        bindViews();
    }

    private void initViewReferences() {
        offlineImageView = binding.ivSaveOffline;
        bodyTextView = binding.tvBody;
        bodyProgressBar = binding.progressBarBody;
    }

    private void initDetailViewModel() {
        DetailViewModelFactory detailFactory = new DetailViewModelFactory(
                getActivity().getApplication(),
                article.getId(),
                article.getBody());
        detailViewModel = ViewModelProviders.of(this, detailFactory).get(DetailViewModel.class);
        observeIsSavedOffline();
        observeParsedBody();
    }

    private void observeIsSavedOffline() {
        detailViewModel.getIsSavedOffline().observe(this, isOffline ->
                offlineImageView.setImageResource(getOfflineIcon(isOffline))
        );
    }

    private int getOfflineIcon(Boolean isOffline) {
        return isOffline ? R.drawable.ic_cloud_done_black_24dp : R.drawable.ic_cloud_download_24dp;
    }


    private void observeParsedBody() {
        detailViewModel.getParsedBody().observe(this, body -> {
            bodyTextView.setText(body);
            hideBodyLoading();
        });
    }

    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        if (! getContext().getResources().getBoolean(R.bool.is_master_detail_layout)) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initFab() {
        FloatingActionButton fab = binding.fab;
        fabClickListener(fab);
        fabScrollListener(fab);
    }

    private void fabClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(view -> {
            Intent shareIntent = Intent.createChooser(
                    ShareCompat.IntentBuilder.from(getActivity())
                            .setType("text/plain")
                            .setText(getShareArticleText())
                            .getIntent(),
                    getString(R.string.title_share_article)
            );
            startActivity(shareIntent);
        });
    }

    private String getShareArticleText() {
        return article.getTitle() + " by " + article.getAuthor()
                + "\nhttps://www.articleurlplaceholder.com";
    }

    private void fabScrollListener(FloatingActionButton fab) {
        binding.nestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (nestedScrollView, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (scrollY > oldScrollY) {
                        fab.hide();
                    } else if (scrollY < oldScrollY) {
                        fab.show();
                    }
                });
    }

    private void bindViews() {
        bindTitle();
        bindAuthor();
        bindPublishingDate();
        bindPhoto();
        setOfflineClickListener();
    }

    private void bindTitle() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(article.getTitle());
    }

    private void bindAuthor() {
        binding.tvAuthor.setText(article.getAuthor());
    }

    private void bindPublishingDate() {
        binding.tvPublishedDate.setText(
                FormatDate.getFormattedDate(article.getPublishedDate())
        );
    }

    private void bindPhoto() {
        GlideApp.with(binding.ivDetailThumbnail)
                .load(article.getPhotoUrl())
                .placeholder(R.drawable.ic_image_icon_black_24dp)
                .error(R.drawable.ic_image_icon_black_24dp)
                .into(binding.ivDetailThumbnail);
    }

    private void setOfflineClickListener() {
        offlineImageView.setOnClickListener(view -> {
            if (detailViewModel.getIsSavedOffline().getValue()) {
                detailViewModel.deleteOfflineArticle(this.article);
                displaySnackbar(getString(R.string.message_offline_delete));
            } else {
                detailViewModel.saveOffline(this.article);
                displaySnackbar(getString(R.string.message_offline_saved));
            }
        });
    }


    private void displaySnackbar(String message) {
        Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }


    private void hideBodyLoading() {
        bodyProgressBar.setVisibility(View.GONE);
        bodyTextView.setVisibility(View.VISIBLE);
    }
}
