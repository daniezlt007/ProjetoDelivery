package br.com.projetodeliverytcc;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.projetodeliverytcc.ViewHolder.CartAdapter;
import br.com.projetodeliverytcc.common.Common;
import br.com.projetodeliverytcc.common.Config;
import br.com.projetodeliverytcc.database.Database;
import br.com.projetodeliverytcc.model.MyResponse;
import br.com.projetodeliverytcc.model.Notification;
import br.com.projetodeliverytcc.model.Order;
import br.com.projetodeliverytcc.model.Request;
import br.com.projetodeliverytcc.model.Sender;
import br.com.projetodeliverytcc.model.Token;
import br.com.projetodeliverytcc.remote.APIService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 9999;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txt_total;
    Button btnPlaceOrder;

    List<Order> cart = new ArrayList<>();
    CartAdapter cartAdapter;

    APIService mService;

    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    String address, comment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Note add before this code
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
        .setDefaultFontPath("fonts/delivery.ttf")
        .setFontAttrId(R.attr.fontPath)
        .build());
        setContentView(R.layout.activity_cart);


        //init paypal
        Intent paypal = new Intent(this, PayPalService.class);
        paypal.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(paypal);

        //init service
        mService = Common.getFCMService();

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlaceOrder.setEnabled(false);
                if (!cart.isEmpty()) {
                    btnPlaceOrder.setEnabled(true);
                    showAlertDialog();
                }
            }
        });

        txt_total = findViewById(R.id.txt_total);

        loadListProduct();

    }
    /*
    * final EditText txtAddress = new EditText(Cart.this);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        txtAddress.setLayoutParams(lp);
        alertDialog.setView(txtAddress);*/


    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("Mais um passo!");
        alertDialog.setMessage("Informe o endereço de entrega: ");

        LayoutInflater inflater = this.getLayoutInflater();
        View order_address_comment = inflater.inflate(R.layout.order_address_coment, null);

        final MaterialEditText txtAddress = order_address_comment.findViewById(R.id.txtAdress);
        final MaterialEditText txtComment = order_address_comment.findViewById(R.id.txtComment);
        alertDialog.setView(order_address_comment);
        alertDialog.setIcon(R.drawable.ic_cart_black);
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                address = txtAddress.getText().toString();
                comment = txtComment.getText().toString();

                String formatAmount = txt_total.getText().toString()
                        .replace("R$", "")
                        .replace(",", ".");


                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                        "BRL",
                        "ComprEX App Order",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                startActivityForResult(intent, PAYPAL_REQUEST_CODE);


            }
        });

        alertDialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetail = confirmation.toJSONObject().toString(4);
                        JSONObject jsonObject = new JSONObject(paymentDetail);

                        Request request = new Request(
                                Common.userCurrent.getPhone(),
                                Common.userCurrent.getName(),
                                address,
                                txt_total.getText().toString(),
                                comment,
                                (String) jsonObject.getJSONObject("response").get("state"),
                                cart
                        );

//String phone, String nome, String address, String total, String status, String comment, String paymentState, List<Order> produtos) {

                        String order_id = String.valueOf(System.currentTimeMillis());
                        requests.child(order_id)
                                .setValue(request);

                        new Database(getBaseContext()).cleanCart();

                        enviarNotificacaoPedido(order_id);

                        Toast.makeText(Cart.this, "Obrigado, pedido criado", Toast.LENGTH_SHORT).show();
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(Cart.this, "Pagamento Cancelado", Toast.LENGTH_SHORT).show();
                }else if(resultCode == PaymentActivity.RESULT_EXTRAS_INVALID){
                    Toast.makeText(Cart.this, "Pagamento inválido", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void enviarNotificacaoPedido(final String order_id) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Token serverToken = postSnapshot.getValue(Token.class);

                    //Criação da notificação
                    Notification notification = new Notification("ComprEX", "Você tem um novo pedido:" + order_id);
                    Sender content = new Sender(serverToken.getToken(), notification);

                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            if (response.code() == 200) {
                                if (response.body().success == 1) {
                                    Toast.makeText(Cart.this, "Obrigado, pedido criado", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(Cart.this, "Falha ao enviar notificação.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("Cart - ERROR - MSG", t.getMessage());
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadListProduct() {
        cart = new Database(this).getCarts();
        cartAdapter = new CartAdapter(cart, this);
        cartAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(cartAdapter);

        double total = 0;
        for (Order order : cart) {
            total += (Double.parseDouble(order.getPrice())) * (Integer.parseInt(order.getQuantity()));
        }
        Locale locale = new Locale("pt", "BR");
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);

        txt_total.setText(String.valueOf(format.format(total)));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.DELETE)) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        cart.remove(position);
        new Database(this).cleanCart();
        for (Order item : cart) {
            new Database(this).addToCart(item);
        }
        loadListProduct();
    }
}
