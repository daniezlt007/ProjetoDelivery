package projetodeliverytccservidor.com.br.projetodeliverytccservidor.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import projetodeliverytccservidor.com.br.projetodeliverytccservidor.R;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.common.Common;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.interfaces.ItemClickListener;

/**
 * Created by daniel on 06/03/18.
 */

public class ProductViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,
        View.OnCreateContextMenuListener {

    public TextView produto_nome;
    public ImageView produto_image;

    private ItemClickListener itemClickListener;

    public ProductViewHolder(View itemView) {
        super(itemView);

        produto_nome = itemView.findViewById(R.id.produto_nome);
        produto_image = itemView.findViewById(R.id.produto_image);

        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);


    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        contextMenu.setHeaderTitle("Escolha a ação");

        contextMenu.add(0,0,getAdapterPosition(), Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(), Common.DELETE);
    }
}
