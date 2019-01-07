package projetodeliverytccservidor.com.br.projetodeliverytccservidor.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import info.hoang8f.widget.FButton;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.R;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.common.Common;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.interfaces.ItemClickListener;

/**
 * Created by daniel on 08/03/18.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    public FButton btnEditar, btnExcluir, btnDetalhe, btnMapa;

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);

        btnEditar = itemView.findViewById(R.id.btnEditar);
        btnExcluir = itemView.findViewById(R.id.btnExcluir);
        btnDetalhe = itemView.findViewById(R.id.btnDetalhe);
        btnMapa = itemView.findViewById(R.id.btnMapa);

    }

}