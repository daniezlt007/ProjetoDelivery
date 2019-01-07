package projetodeliverytccservidor.com.br.projetodeliverytccservidor.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.view.LayoutInflater;

import projetodeliverytccservidor.com.br.projetodeliverytccservidor.OrderDetail;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Order;
import java.util.List;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.R;

/**
 * Created by daniel on 01/04/18.
 */

class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView name, quantity, price,discount;

    public MyViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.product_name);
        quantity = itemView.findViewById(R.id.product_quantity);
        price = itemView.findViewById(R.id.product_price);
        discount = itemView.findViewById(R.id.product_discount);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder>  {

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders){
        this.myOrders = myOrders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = myOrders.get(position);
        holder.name.setText(String.format("Nome: %s", order.getProductName()));
        holder.quantity.setText(String.format("Quantidade: %s", order.getQuantity()));
        holder.price.setText(String.format("Preço: %s", order.getPrice()));
        holder.discount.setText(String.format("Desconto: %s", order.getDiscount()));
    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
