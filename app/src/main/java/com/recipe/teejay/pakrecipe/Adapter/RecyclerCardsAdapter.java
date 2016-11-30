package com.recipe.teejay.pakrecipe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.recipe.teejay.pakrecipe.Activity.MainActivity;
import com.recipe.teejay.pakrecipe.Activity.RecipedetailActivity;
import com.recipe.teejay.pakrecipe.Models.Recipe;
import com.recipe.teejay.pakrecipe.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Asim on 6/11/2016.
 */
public class RecyclerCardsAdapter extends RecyclerView.Adapter<RecyclerCardsAdapter.CardViewHolder>  {
Context context;
    private ArrayList<Recipe> mRecipes;
    private ArrayList<Recipe> mRecipescopy;


    public void filter(String text) {

        if(text.isEmpty()){
            mRecipes.clear();
            mRecipes.addAll(mRecipescopy);
        } else{
            ArrayList<Recipe> result = new ArrayList<>();
            text = text.toLowerCase();
            for(Recipe item: mRecipes){
                if(item.getRecipeName().toLowerCase().contains(text)){
                    result.add(item);
                }
            }
            mRecipes.clear();
            mRecipes.addAll(result);
        }
        notifyDataSetChanged();
    }
    public static class CardViewHolder extends RecyclerView.ViewHolder{

        ImageView mImageHolder;
        TextView mTextTitle;
        RelativeLayout relativeLayout;
        View mView;

        public CardViewHolder(View itemView) {
            super(itemView);
            this.mImageHolder = (ImageView) itemView.findViewById(R.id.image_holder);
            this.mTextTitle = (TextView) itemView.findViewById(R.id.text_title);
            this.mView=itemView;
           /* this.relativeLayout=(RelativeLayout) itemView.findViewById(R.id.rl);*/

        }
    }

    public RecyclerCardsAdapter(ArrayList<Recipe> recipes, Context con) {
        this.mRecipes = recipes;
        this.mRecipescopy=new ArrayList<Recipe>();
        this.mRecipescopy.addAll(recipes);
        this.context=con;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(view);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int listPosition) {
        final Recipe recipe = mRecipes.get(listPosition);

        try {
            recipe.setImage(BitmapFactory.decodeStream(context.getAssets().open("recipe_images/" + recipe.getFilename() + ".dat")));
            holder.mImageHolder.setImageBitmap(recipe.getImage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.mTextTitle.setText(recipe.getRecipeName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                   Intent intent = new Intent(v.getContext(), RecipedetailActivity.class);

                    intent.putExtra("Recipepos", recipe.getRecipeID());
               /* ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) v.getContext(), (View)holder.relativeLayout, "recipe");*/
                    Log.d("onclick", recipe.getRecipeName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.getContext().startActivity(intent);//,options.toBundle());
                }
                else{v.getContext().startActivity(intent);}

            }
        });


    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}