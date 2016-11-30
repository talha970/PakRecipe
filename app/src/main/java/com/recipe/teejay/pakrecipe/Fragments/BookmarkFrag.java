package com.recipe.teejay.pakrecipe.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.recipe.teejay.pakrecipe.Activity.MainActivity;
import com.recipe.teejay.pakrecipe.Adapter.RecyclerCardsAdapter;
import com.recipe.teejay.pakrecipe.DB.DbManager;
import com.recipe.teejay.pakrecipe.Models.Recipe;
import com.recipe.teejay.pakrecipe.R;

import java.util.ArrayList;

/**
 * Created by Asim on 6/12/2016.
 */
public class BookmarkFrag extends Fragment {
    ArrayList<Recipe> bookrecipe;
    RecyclerCardsAdapter reciperAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View v=inflater.inflate(R.layout.allrec_layout, parent, false);
        RecyclerView recipesRecycleView = (RecyclerView) v.findViewById(R.id.recycle_view);
        TextView nobooktv=(TextView) v.findViewById(R.id.nobooktv);
        recipesRecycleView.setHasFixedSize(true);

        GridLayoutManager recycleLayoutManager = new GridLayoutManager(getActivity(),1,GridLayoutManager.VERTICAL, false);
        recipesRecycleView.setLayoutManager(recycleLayoutManager);
        recipesRecycleView.setItemAnimator(new DefaultItemAnimator());
        DbManager myDbHelper = new DbManager(getActivity());
        bookrecipe=myDbHelper.getBookmarkedRecipes();
        ((MainActivity) getActivity())
                .setActionBarTitle("Bookmarked Recipes");
        if(!bookrecipe.isEmpty()){
      reciperAdapter = new RecyclerCardsAdapter(bookrecipe,getActivity());
            recipesRecycleView.setAdapter(reciperAdapter);}
        else{
            Log.d("Allrecfrag","mrecipes is null");
            nobooktv.setVisibility(View.VISIBLE);
        }



        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(reciperAdapter!=null){
            reciperAdapter.notifyDataSetChanged();
        }
    }
}
