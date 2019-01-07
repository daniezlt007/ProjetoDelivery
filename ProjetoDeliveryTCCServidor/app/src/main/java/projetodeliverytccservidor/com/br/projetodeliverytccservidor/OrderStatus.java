package projetodeliverytccservidor.com.br.projetodeliverytccservidor;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import projetodeliverytccservidor.com.br.projetodeliverytccservidor.ViewHolder.OrderViewHolder;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.common.Common;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.interfaces.ItemClickListener;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.MyResponse;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Notification;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Order;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Request;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Sender;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Token;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase db;
    DatabaseReference requests;

    MaterialSpinner spinner;

    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        db = FirebaseDatabase.getInstance();
        requests = db.getReference("Requests");

        apiService = Common.getFCMService();

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();

    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder orderViewHolder, final Request request, final int position) {

                orderViewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                orderViewHolder.txtOrderStatus.setText(Common.converteStatusPedido(request.getStatus()));
                orderViewHolder.txtOrderAddress.setText(request.getAddress());
                orderViewHolder.txtOrderPhone.setText(request.getPhone());

                orderViewHolder.btnEditar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showUpdatePedido(adapter.getRef(position).getKey(), adapter.getItem(position));
                    }
                });

                orderViewHolder.btnExcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deletePedido(adapter.getRef(position).getKey());
                    }
                });

                orderViewHolder.btnDetalhe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = request;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });

                orderViewHolder.btnMapa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent tracking = new Intent(OrderStatus.this, RastreamentoPedido.class);
                        Common.currentRequest = request;
                        startActivity(tracking);
                    }
                });

            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void deletePedido(String key) {
        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdatePedido(String key, final Request item) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OrderStatus.this);
        alertBuilder.setTitle("Gerenciamento de Pedidos");
        alertBuilder.setMessage("Selecione o status desejado");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null);
        //0=PEDIDO RECEBIDO, 1=CONFERINDO PEDIDO, 2=PEDIDO ENVIADO
        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("PEDIDO RECEBIDO","CONFERINDO PEDIDO","PEDIDO ENVIADO");

        alertBuilder.setView(view);
        final String localKey = key;
        alertBuilder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localKey).setValue(item);
                adapter.notifyDataSetChanged();
                sendStatusPedidoParaUsuario(localKey, item);
            }
        });

        alertBuilder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertBuilder.show();
    }

    private void sendStatusPedidoParaUsuario(final String key, Request item) {
        DatabaseReference tokens = db.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnap:dataSnapshot.getChildren()){
                    Token token = postDataSnap.getValue(Token.class);

                    Notification notification = new Notification("ComprEX", "Seu pedido #:" + key + " foi atualizado.");
                    Sender sender = new Sender(token.getToken(), notification);

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if(response.body().success == 1){
                                Toast.makeText(OrderStatus.this, "Pedido atualizado.", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(OrderStatus.this, "Pedido atualizado porém não foi enviado a notificação.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("Tag - OrderStatus - MSG" , t.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
