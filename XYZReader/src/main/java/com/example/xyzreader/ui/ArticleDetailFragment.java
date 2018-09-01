package com.example.xyzreader.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
import com.example.xyzreader.databinding.FragmentArticleDetailBinding;
import com.example.xyzreader.ui.ArticleList.ArticleListActivity;
import com.example.xyzreader.util.GlideApp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * A fragment representing a single Article detail screen. This fragment is
 * either contained in a {@link ArticleListActivity} in two-pane mode (on
 * tablets) or a {@link ArticleDetailActivity} on handsets.
 */
public class ArticleDetailFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String ARGUMENT_ITEM_ID = "item_id";

    private FragmentArticleDetailBinding binding;

    private Cursor mCursor;
    private long mItemId;

    private boolean mIsCard = false;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ArticleDetailFragment() {
    }

    public static ArticleDetailFragment newInstance(long itemId) {
        Bundle arguments = new Bundle();
        arguments.putLong(ARGUMENT_ITEM_ID, itemId);
        ArticleDetailFragment fragment = new ArticleDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemId = getArguments().getLong(ARGUMENT_ITEM_ID);
        mIsCard = getResources().getBoolean(R.bool.is_master_detail_layout);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpActionBar();

        // In support library r8, calling initLoader for a fragment in a FragmentPagerAdapter in
        // the fragment's onCreate may cause the same LoaderManager to be dealt to multiple
        // fragments because their mIndex is -1 (haven't been added to the activity yet). Thus,
        // we do this in onActivityCreated.
        getLoaderManager().initLoader(0, null, this);
    }

    private void setUpActionBar() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(binding.toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_detail, container, false);

        setUpFab();
        bindViews();

        return binding.getRoot();
    }

    private void setUpFab() {
        binding.speedDial.setOnClickListener(view ->
                startActivity(Intent.createChooser(ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setText("Some sample text")
                        .getIntent(), getString(R.string.content_description_fab_share)))
        );
    }


    private void bindViews() {
        TextView bylineView = binding.publishedDate;
        bylineView.setMovementMethod(new LinkMovementMethod());
        TextView bodyView = binding.articleBody;

        if (mCursor != null) {
            binding.collapsingToolbarLayout.setTitle(mCursor.getString(ArticleLoader.Query.TITLE));

            binding.author.setText(mCursor.getString(ArticleLoader.Query.AUTHOR));

            Date publishedDate = getPublishedDate();
            bylineView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
            ));

            bodyView.setText(Html.fromHtml(mCursor.getString(ArticleLoader.Query.BODY).replaceAll("(\r\n|\n)", "<br />")));


            GlideApp.with(binding.ivDetailThumbnail)
                    .load(mCursor.getString(ArticleLoader.Query.PHOTO_URL))
                    .placeholder(R.drawable.ic_image_icon_black)
                    .error(R.drawable.ic_image_icon_black)
                    .into(binding.ivDetailThumbnail);
        } else {
            binding.collapsingToolbarLayout.setTitle("N/A");
            bylineView.setText("N/A");
            bodyView.setText("N/A");
        }
    }

    private Date getPublishedDate() {
        try {
            return dateFormat.parse(
                    mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE)
            );
        } catch (ParseException e) {
            Timber.e("Error parsing data - passing today's date instead.\n%s", e.getMessage());
            return new Date();
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return ArticleLoader.newInstanceForItemId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!isAdded()) {
            if (cursor != null) {
                cursor.close();
            }
            return;
        }

        mCursor = cursor;
        if (mCursor != null && !mCursor.moveToFirst()) {
            Timber.e("Error reading item detail cursor");
            mCursor.close();
            mCursor = null;
        }

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mCursor = null;
        bindViews();
    }
}
