package com.recipe.teejay.pakrecipe.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.recipe.teejay.pakrecipe.Activity.CatRecipeActivity;
import com.recipe.teejay.pakrecipe.Activity.RecipedetailActivity;
import com.recipe.teejay.pakrecipe.Models.Category;
import com.recipe.teejay.pakrecipe.Models.Recipe;
import com.recipe.teejay.pakrecipe.R;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Asim on 6/11/2016.
 */
public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CardViewHolder> {
    Context context;
    private ArrayList<Category> mCategories;


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

    public CatAdapter(ArrayList<Category> cat, Context con) {
        this.mCategories = cat;
        this.context=con;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_layout, parent, false);
        CardViewHolder cardViewHolder = new CardViewHolder(view);
        return cardViewHolder;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, final int listPosition) {
        final Category cat = mCategories.get(listPosition);
        try {
            cat.setCategoryBitmap(BitmapFactory.decodeStream(context.getAssets().open("cat_images/" + cat.getCategoryImage() + ".dat")));
            holder.mImageHolder.setImageBitmap(cat.getCategoryBitmap());
            /*Drawable d = new BitmapDrawable(context.getResources(),cat.getCategoryBitmap());
            holder.mImageHolder.setBackgroundResource(d);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.mTextTitle.setText(cat.getCategoryName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                Intent intent = new Intent(v.getContext(), CatRecipeActivity.class);

                intent.putExtra("Recipecat", cat.getCategoryID());
               /* ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) v.getContext(), (View)holder.relativeLayout, "recipe");*/
                Log.d("onclick", cat.getCategoryName());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    v.getContext().startActivity(intent);//,options.toBundle());
                }
                else{v.getContext().startActivity(intent);}

            }
        });


    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }
}