package com.example.xyzreader.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
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

public class DetailFragment extends Fragment {

    private FragmentDetailBinding binding;

    private ArticleViewModel viewModel;
    
    private Article article;
    public static final String ARGUMENT_ID_ARTICLE = "article_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DetailFragment() {
    }


    public static DetailFragment newInstance(int  articleIndex) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGUMENT_ID_ARTICLE, articleIndex);
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
        int articleIndex = getArguments().getInt(ARGUMENT_ID_ARTICLE);
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

        setUpActionBar();
        setUpFab();
        bindViews();

        return binding.getRoot();
    }

    private void setUpActionBar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpFab() {
        binding.fab.setOnClickListener(view ->
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.content_description_fab_share)))
        );
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
                .placeholder(R.drawable.ic_image_icon_black)
                .error(R.drawable.ic_image_icon_black)
                .into(binding.ivDetailThumbnail);
    }
}
