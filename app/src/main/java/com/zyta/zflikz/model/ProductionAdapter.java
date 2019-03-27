package com.zyta.zflikz.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyta.zflikz.GlideApp;
import com.zyta.zflikz.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductionAdapter extends RecyclerView.Adapter<ProductionAdapter.ProductionHolder> {
    ArrayList<ProductionCompany> productionArrayList = new ArrayList<ProductionCompany>();
    Context mContext;

    public ProductionAdapter(Context mContext, ArrayList<ProductionCompany> productionArrayList) {
        this.productionArrayList = productionArrayList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ProductionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.content_production, parent, false);
        return new ProductionHolder(view, mContext, productionArrayList);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductionHolder holder, int position) {
        productionArrayList.get(position);
        ImageView productionImage = holder.productionImage;
        TextView productionName = holder.productionName;
        TextView productionCountry = holder.productionCountry;

        if (productionArrayList.get(position).getLogoPath() != null) {
//            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w185" + productionArrayList.get(position).getLogoPath()).fitCenter().placeholder(R.drawable.zlikx_logo).into(productionImage);
            GlideApp.with(mContext).load("http://image.tmdb.org/t/p/w185" + productionArrayList.get(position)
                    .getLogoPath())
                    .fitCenter()
                    .placeholder(R.drawable.zlikx_logo)
                    .into(productionImage);
        } else {
//            GlideApp.with(mContext).load(R.drawable.no_image_available).placeholder(R.drawable.zlikx_logo).into(productionImage);
//            productionImage.setVisibility(View.GONE);
        }
        if (productionArrayList.get(position).getOriginCountry().isEmpty()) {
            productionCountry.setVisibility(View.GONE);
            productionName.setPadding(40, 40, 40, 40);
        }

        productionName.setText(productionArrayList.get(position).getName());
        productionCountry.setText(productionArrayList.get(position).getOriginCountry());
    }

    @Override
    public int getItemCount() {
        if (null == productionArrayList) return 0;
        return productionArrayList.size();
    }

    public class ProductionHolder extends RecyclerView.ViewHolder {
        ImageView productionImage;
        TextView productionName, productionCountry;
        Context context;
        ArrayList<ProductionCompany> productionCountriesList;


        public ProductionHolder(View itemView, Context context, ArrayList<ProductionCompany> productionCountries) {
            super(itemView);
            this.productionImage = productionImage;
            this.productionName = productionName;
            this.productionCountry = productionCountry;
            this.context = context;
            this.productionCountriesList = productionArrayList;

            productionImage = itemView.findViewById(R.id.production_image_view);
            productionName = itemView.findViewById(R.id.production_name_text_view);
            productionCountry = itemView.findViewById(R.id.production_country_text_view);
        }
    }
}