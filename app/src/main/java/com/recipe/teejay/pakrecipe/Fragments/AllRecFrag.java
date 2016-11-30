package com.recipe.teejay.pakrecipe.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.recipe.teejay.pakrecipe.Activity.MainActivity;
import com.recipe.teejay.pakrecipe.Adapter.RecyclerCardsAdapter;
import com.recipe.teejay.pakrecipe.Models.Recipe;
import com.recipe.teejay.pakrecipe.R;

import java.util.ArrayList;

/**
 * Created by Asim on 6/11/2016.
 */
public class AllRecFrag extends Fragment implements SearchView.OnQueryTextListener {
    RecyclerCardsAdapter reciperAdapter;
    private AdView mAdView;
    ArrayList<Recipe> recipes;
    public AllRecFrag(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View v=inflater.inflate(R.layout.allrec_layout, parent, false);
        setHasOptionsMenu(true);
        RecyclerView recipesRecycleView = (RecyclerView) v.findViewById(R.id.recycle_view);
       /* mAdView = (AdView) v.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);*/
        recipesRecycleView.setHasFixedSize(true);
recipes=MainActivity.mrecipes;
        GridLayoutManager recycleLayoutManager = new GridLayoutManager(getActivity(),1,GridLayoutManager.VERTICAL, false);
        recipesRecycleView.setLayoutManager(recycleLayoutManager);
        recipesRecycleView.setItemAnimator(new DefaultItemAnimator());
        if(MainActivity.mrecipes!=null){
         reciperAdapter = new RecyclerCardsAdapter(recipes,getActivity());
            recipesRecycleView.setAdapter(reciperAdapter);}
        else{
            Log.d("Allrecfrag","mrecipes is null");

        }



        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
       inflater.inflate(R.menu.main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
        ((MainActivity) getActivity())
                .setActionBarTitle("All Recipes");

        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        // Do something when collapsed
                       // reciperAdapter.setFilter(mCountryModel);
                        return true; // Return true to collapse action view
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        // Do something when expanded
                        return true; // Return true to expand action view
                    }
                });


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query.isEmpty()){
            Log.d("all rec","empty query");
        }
        reciperAdapter.filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

            reciperAdapter.filter(newText);

        return true;
    }
}
