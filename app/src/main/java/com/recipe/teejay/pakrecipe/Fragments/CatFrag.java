package com.recipe.teejay.pakrecipe.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.recipe.teejay.pakrecipe.Activity.MainActivity;
import com.recipe.teejay.pakrecipe.Adapter.CatAdapter;
import com.recipe.teejay.pakrecipe.Adapter.RecyclerCardsAdapter;
import com.recipe.teejay.pakrecipe.R;

/**
 * Created by Asim on 6/15/2016.
 */
public class CatFrag extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View v=inflater.inflate(R.layout.allcat, parent, false);
        RecyclerView recipesRecycleView = (RecyclerView) v.findViewById(R.id.recycle_viewcat);

        recipesRecycleView.setHasFixedSize(true);

        GridLayoutManager recycleLayoutManager = new GridLayoutManager(getActivity(),1,GridLayoutManager.VERTICAL, false);
        recipesRecycleView.setLayoutManager(recycleLayoutManager);
        recipesRecycleView.setItemAnimator(new DefaultItemAnimator());
        ((MainActivity) getActivity())
                .setActionBarTitle("All Categories");
        if(MainActivity.mrecipes!=null){
            CatAdapter reciperAdapter = new CatAdapter(MainActivity.mcategories,getActivity());
            recipesRecycleView.setAdapter(reciperAdapter);}
        else{
            Log.d("AllCatfrag","mcat is null");

        }



        return v;
    }
}
