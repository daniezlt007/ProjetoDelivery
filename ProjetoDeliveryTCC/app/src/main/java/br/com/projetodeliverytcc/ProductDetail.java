package br.com.projetodeliverytcc;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import br.com.projetodeliverytcc.common.Common;
import br.com.projetodeliverytcc.database.Database;
import br.com.projetodeliverytcc.model.Order;
import br.com.projetodeliverytcc.model.Product;
import br.com.projetodeliverytcc.model.Rating;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.stepstone.apprating.*;
import com.stepstone.apprating.listener.RatingDialogListener;

import com.google.firebase.database.Query;
import java.util.Arrays;

public class ProductDetail extends AppCompatActivity implements RatingDialogListener {

    TextView productName,productPrice,productDescription;
    ImageView imgProduct;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnRating;
    CounterFab btnCart;
    ElegantNumberButton numberButton;
    RatingBar ratingBar;

    String productId = "";

    FirebaseDatabase database;
    DatabaseReference products;

    DatabaseReference ratingsTable;

    Product produtoAtual;

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
        setContentView(R.layout.activity_product_detail);

        database = FirebaseDatabase.getInstance();
        products = database.getReference("Product");
        ratingsTable = database.getReference("Rating");

        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);

        numberButton = findViewById(R.id.numberButton);

        btnCart = findViewById(R.id.btnCart);
        btnRating = findViewById(R.id.btnRating);
        ratingBar = findViewById(R.id.ratingBar);

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingDialog();
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(ProductDetail.this).addToCart(new Order(
                    productId,
                    produtoAtual.getName(),
                    numberButton.getNumber(),
                    produtoAtual.getPrice(),
                    produtoAtual.getDiscount()
                ));

                Toast.makeText(ProductDetail.this, "Produto Adicionado ao Carrinho", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCard());

        imgProduct = findViewById(R.id.imgProduct);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        if(getIntent() != null){
            productId = getIntent().getStringExtra("productId");
        }
        if(!productId.isEmpty()){
            if(Common.isConnectedInternet(getBaseContext())){
                getDetailProduct(productId);
                getRatingProduct(productId);
            }else{
                Toast.makeText(ProductDetail.this,"Verifique sua conexão",Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }

    private void getRatingProduct(String productId) {
        Query productRating = ratingsTable.orderByChild("productId").equalTo(productId);
        productRating.addValueEventListener(new ValueEventListener() {
            int count=0, sum=0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postDataSnapshot: dataSnapshot.getChildren()){

                    Rating item = postDataSnapshot.getValue(Rating.class);
                    sum+=Integer.parseInt(item.getRateValue());
                    count++;
                }

                if(count != 0){
                    float average = sum / count;
                    ratingBar.setRating(average);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancelar")
                .setNoteDescriptions(Arrays.asList("Muito Ruim","Ruim","Bom","Muito Bom", "Excelente"))
                .setDefaultRating(1)
                .setTitle("Avalie o produto")
                .setDescription("Por favor selecione as estrelas e deixe seu comentário")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Por favor escreva seu comentário aqui...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(ProductDetail.this)
                .show();

    }

    private void getDetailProduct(String productId) {
        products.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtoAtual = dataSnapshot.getValue(Product.class);
                Picasso.with(getBaseContext()).load(produtoAtual.getImage())
                        .into(imgProduct);
                collapsingToolbarLayout.setTitle(produtoAtual.getName());

                productPrice.setText(produtoAtual.getPrice());
                productName.setText(produtoAtual.getName());

                productDescription.setText(produtoAtual.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onPositiveButtonClicked(int value, String comentario) {
        final Rating rating = new Rating(Common.userCurrent.getPhone(),
                productId,
                String.valueOf(value),
                comentario);

        ratingsTable.child(Common.userCurrent.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Common.userCurrent.getPhone()).exists()){
                    ratingsTable.child(Common.userCurrent.getPhone()).removeValue();
                    ratingsTable.child(Common.userCurrent.getPhone()).setValue(rating);
                }else{
                    ratingsTable.child(Common.userCurrent.getPhone()).setValue(rating);
                }
                Toast.makeText(ProductDetail.this, "Obrigado por enviar sua opinião!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
