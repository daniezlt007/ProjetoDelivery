package projetodeliverytccservidor.com.br.projetodeliverytccservidor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import android.support.v7.widget.LinearLayoutManager;

import projetodeliverytccservidor.com.br.projetodeliverytccservidor.adapter.OrderDetailAdapter;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.common.Common;

public class OrderDetail extends AppCompatActivity {

    TextView order_id,order_phone,order_address,order_total, order_comment;
    String order_id_value = "";
    RecyclerView lstProduct;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_address = findViewById(R.id.order_address);
        order_total = findViewById(R.id.order_total);
        order_comment = findViewById(R.id.order_comment);

        lstProduct = findViewById(R.id.listProducts);
        lstProduct.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstProduct.setLayoutManager(layoutManager);

        if(getIntent() != null){
            order_id_value = getIntent().getStringExtra("OrderId");
        }

        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentRequest.getProdutos());
        adapter.notifyDataSetChanged();
        lstProduct.setAdapter(adapter);


    }
}
