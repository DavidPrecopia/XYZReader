package com.example.xyzreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.xyzreader.R;
import com.example.xyzreader.databinding.FragmentDetailBinding;
import com.example.xyzreader.datamodel.Article;
import com.example.xyzreader.util.FormatDate;
import com.example.xyzreader.util.GlideApp;
import com.github.clans.fab.FloatingActionButton;

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;

    private ArticleViewModel viewModel;

    private Article article;
    public static final String ARGUMENT_ID_ARTICLE_INDEX = "article_index_id";


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
        initViewModel();
        initArticleField();
    }

    private void initArticleField() {
        int articleIndex = getArguments().getInt(ARGUMENT_ID_ARTICLE_INDEX);
        article = viewModel.getArticlesList().getValue().get(articleIndex);
    }

    private void initViewModel() {
        ArticleViewModelFactory factory = new ArticleViewModelFactory(getActivity().getApplication());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(ArticleViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        setUpToolbar();
        initFab();
        bindViews();
    }

    private void setUpToolbar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initFab() {
        FloatingActionButton fab = binding.fab;
        fab.setImageResource(R.drawable.ic_share_white_24dp);
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
                        fab.hide(true);
                    } else if (scrollY < oldScrollY) {
                        fab.show(true);
                    }
                });
    }

    private void bindViews() {
        binding.author.setText(article.getAuthor());
        bindTitle();
        bindPublishingDate();
        bindBody();
        bindPhoto();
    }

    private void bindTitle() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(article.getTitle());
    }

    private void bindPublishingDate() {
        binding.publishedDate.setText(
                FormatDate.getFormattedDate(article.getPublishedDate())
        );
    }

    private void bindBody() {
        binding.articleBody.setText(
                Html.fromHtml(article.getBody().replaceAll("(\r\n|\n)", "<br />"))
        );
    }

    private void bindPhoto() {
        GlideApp.with(binding.ivDetailThumbnail)
                .load(article.getPhotoUrl())
                .placeholder(R.drawable.ic_image_icon_black_24dp)
                .error(R.drawable.ic_image_icon_black_24dp)
                .into(binding.ivDetailThumbnail);
    }
}
