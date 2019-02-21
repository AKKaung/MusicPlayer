package com.ldt.musicr.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldt.musicr.ui.main.navigate.library.SongAdapter;
import com.ldt.musicr.util.uitool.AutoGeneratedPlaylistBitmap;
import com.ldt.musicr.util.Tool;
import com.ldt.musicr.R;
import com.ldt.musicr.ui.main.BaseActivity;
import com.ldt.musicr.ui.main.SupportFragmentPlusActivity;
import com.ldt.musicr.loader.PlaylistSongLoader;
import com.ldt.musicr.services.MusicStateListener;
import com.ldt.musicr.model.Album;
import com.ldt.musicr.model.Playlist;
import com.ldt.musicr.model.Song;
import com.ldt.musicr.ui.widget.DeepShadowImgChildConstraintLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.fancybuttons.FancyButton;

public class PlaylistPagerFragment extends FragmentPlus implements MusicStateListener {

    @BindView(R.id.play_all_button) FancyButton playAllButton;
    @BindView(R.id.random_button) FancyButton randomButton;

    @BindView(R.id.more_playlist) View mMoreButton;

    @BindView(R.id.playlist_big_rv) RecyclerView recyclerView;

    @BindView(R.id.Art) ImageView artView;
    @BindView(R.id.title) TextView titleView;
    @BindView(R.id.artist) TextView artistView;

    @BindView(R.id.toolbar) Toolbar toolbar;

    @BindView(R.id.background_constraint) DeepShadowImgChildConstraintLayout back_constraint;
    @BindView(R.id.playlist_pager_collapsing_toolbar) CollapsingToolbarLayout collapsingToolbarLayout;

    SongAdapter songListAdapter;
    Bitmap bitmap;

    String Title, Artist;
    List<Song> arraylist;

    @Override
    public void onTransitionComplete() {

    }

    public void set(List<Song> list,String title,String artist) {
        this.arraylist = list;
        this.Title = title;
        this.Artist = artist;
    }

    @Override
    public StatusTheme setDefaultStatusTheme() {
        return StatusTheme.BlackIcon;
    }

    @Override
    public void restartLoader() {

    }

    @Override
    public void onPlaylistChanged() {

    }

    @Override
    public void onMetaChanged() {

    }

    @Override
    public void onArtWorkChanged() {
        int surface = Tool.getSurfaceColor();
        int heavy_surface = Tool.getHeavyColor();
        titleView.setTextColor(surface);
        artistView.setTextColor(surface);
        randomButton.setTextColor(heavy_surface);
        playAllButton.setIconColor(heavy_surface);
        playAllButton.setTextColor(heavy_surface);

        if(songListAdapter!=null) songListAdapter.notifyDataSetChanged();
    }

    public enum ListType {
        ALBUM,PLAYLIST
    }
    ListType type;
    Playlist playlist;
    Album album;
    int position;
    public static PlaylistPagerFragment Initialize(Activity activity, Playlist playlist, int position, @Nullable Bitmap bitmap)
    {
        PlaylistPagerFragment fragment = new PlaylistPagerFragment();
        fragment.setFrameLayoutNTransitionType(activity, SupportFragmentPlusActivity.TransitionType.RIGHT_LEFT);
     fragment.type =ListType.PLAYLIST;
     fragment.playlist = playlist;
     fragment.position = position;
     if(bitmap!=null) fragment.bitmap = bitmap;
        return fragment;
    }
    public static PlaylistPagerFragment Initialize(Activity activity, Album album, int position, @Nullable Bitmap bitmap)
    {
        PlaylistPagerFragment fragment = new PlaylistPagerFragment();
        fragment.setFrameLayoutNTransitionType(activity, SupportFragmentPlusActivity.TransitionType.RIGHT_LEFT);
        fragment.type = ListType.ALBUM;
        fragment.album = album;
        fragment.position = position;
        if(bitmap!= null) fragment.bitmap = bitmap;
        return fragment;
    }

    private int[] getRelativePosition(View v) {
        int[] locationInScreen = new int[2]; // view's position in scrren
        int[] parentLocationInScreen = new int[2]; // parent view's position in screen
        v.getLocationOnScreen(locationInScreen);
        View parentView = (View)v.getParent();
        parentView.getLocationOnScreen(parentLocationInScreen);
        float relativeX = locationInScreen[0] - parentLocationInScreen[0];
        float relativeY = locationInScreen[1] - parentLocationInScreen[1];
        return new int[]{(int) relativeX, (int) relativeY};
    }
    @Override
    public void onDetach() {
        ((BaseActivity)getActivity()).removeMusicStateListenerListener(this);
        super.onDetach();
    }

    float oneDp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       return inflater.inflate( R.layout.playlist_pager_v2,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        ButterKnife.bind(this,view);

        getMainActivity().setMusicStateListenerListener(this);
        oneDp = Tool.getOneDps(getContext());


        back_constraint.setShadowDeltaRect((int)oneDp*20,(int)oneDp*20,(int)-oneDp*20,(int)oneDp*20);
        //back_constraint.setShadowDeltaRect(0,0,0,0);

        setupToolbar();

        int surface = Tool.getSurfaceColor();
        int heavy_surface = Tool.getHeavyColor();
        titleView.setTextColor(surface);
        artistView.setTextColor(surface);

        randomButton.setTextColor(surface);
        playAllButton.setIconColor(surface);
        playAllButton.setTextColor(surface);

        if(bitmap!=null) {
            artView.setImageBitmap(bitmap);
            back_constraint.setBitmapImage(bitmap);
        }
        new loadSong().execute();
    }

    private void setupToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

    }

    private class loadSong extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
         recyclerView.setAdapter(songListAdapter);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

         titleView.setText(Title);
         artistView.setText(Artist);
            collapsingToolbarLayout.setTitle(Title);
        }

        @Override
        protected Void doInBackground(Void... voids) {
           // if(type==ListType.PLAYLIST) {
                arraylist = PlaylistSongLoader.getPlaylistWithListID(getActivity(), position,playlist.id);
                if(bitmap==null)
                new loadArtwork().execute();
           // }
            Title = playlist.name;
            Artist = "Various Artist";
          //  songListAdapter = new SongAdapter((AppCompatActivity) getActivity(), mData, false, false);
          return null;
        }
    }
    private class loadArtwork extends AsyncTask<Void,Void,Void> {
        @Override
        protected void onPostExecute(Void aVoid) {

            artView.setImageBitmap(bitmap);
            back_constraint.setBitmapImage(bitmap);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            bitmap = AutoGeneratedPlaylistBitmap.getBitmap(getActivity(),arraylist,false,false);
            return null;
        }
    }

}
