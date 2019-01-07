package br.com.projetodeliverytcc.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.projetodeliverytcc.R;
import br.com.projetodeliverytcc.interfaces.ItemClickListener;

/**
 * Created by daniel on 25/02/18.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView product_name, product_price;
    public ImageView product_image,fav_image, share_image, btn_quick_cart;

    private ItemClickListener itemClickListener;

    public ProductViewHolder(View itemView) {
        super(itemView);

        product_name = itemView.findViewById(R.id.product_name);
        product_image = itemView.findViewById(R.id.product_image);
        product_price = itemView.findViewById(R.id.product_price);
        fav_image = itemView.findViewById(R.id.fav);
        share_image = itemView.findViewById(R.id.share_image);
        btn_quick_cart = itemView.findViewById(R.id.btn_quick_cart);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
