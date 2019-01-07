package br.com.projetodeliverytcc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import br.com.projetodeliverytcc.ViewHolder.ProductViewHolder;
import br.com.projetodeliverytcc.common.Common;
import br.com.projetodeliverytcc.interfaces.ItemClickListener;
import br.com.projetodeliverytcc.model.Category;
import br.com.projetodeliverytcc.model.Order;
import br.com.projetodeliverytcc.model.Product;
import br.com.projetodeliverytcc.database.Database;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProductList extends AppCompatActivity {

    RecyclerView recycler_product;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference productList;

    String categoryId = "";

    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> searchAdapter;
    List<String> sugestaoList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //Db favorites

    Database localDB;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    SwipeRefreshLayout swipeRefreshLayout;

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //create Photo bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

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
        setContentView(R.layout.activity_product_list);

        callbackManager = new CallbackManager.Factory().create();
        shareDialog = new ShareDialog(this);


        database = FirebaseDatabase.getInstance();
        productList = database.getReference("Product");

        localDB = new Database(this);


        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Get Intent CategoryId
                if(getIntent() != null){
                    categoryId = getIntent().getStringExtra("CategoryId");
                }

                if(!categoryId.isEmpty() && categoryId != null){
                    if(Common.isConnectedInternet(getBaseContext())){
                        loadProductList(categoryId);
                    }else{
                        Toast.makeText(ProductList.this,"Verifique sua conexão",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //Get Intent CategoryId
                if(getIntent() != null){
                    categoryId = getIntent().getStringExtra("CategoryId");
                }

                if(!categoryId.isEmpty() && categoryId != null){
                    if(Common.isConnectedInternet(getBaseContext())){
                        loadProductList(categoryId);
                    }else{
                        Toast.makeText(ProductList.this,"Verifique sua conexão",Toast.LENGTH_SHORT).show();
                        return;
                    }

                }
            }
        });



        recycler_product = findViewById(R.id.recycler_product);
        recycler_product.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_product.setLayoutManager(layoutManager);


        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Digite o Produto");
        //materialSearchBar.setSpeechMode(false); //definido no XML
        loadSugestao();
        materialSearchBar.setLastSuggestions(sugestaoList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<String> pesquisa = new ArrayList<>();
                for (String search : sugestaoList) {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        pesquisa.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(pesquisa);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled){
                    recycler_product.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int i) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        Query searchQuery = productList.orderByChild("name").equalTo(text.toString());
        FirebaseRecyclerOptions<Product> productOption = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(searchQuery, Product.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(productOption) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int position, @NonNull Product model) {
                productViewHolder.product_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(productViewHolder.product_image);

                final Product local = model;
                productViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent productDetail = new Intent(ProductList.this, ProductDetail.class);
                        productDetail.putExtra("productId", searchAdapter.getRef(position).getKey());
                        startActivity(productDetail);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item, parent, false);
                return new ProductViewHolder(view);
            }
        };
        recycler_product.setAdapter(searchAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadSugestao() {
        productList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Product item = postSnapshot.getValue(Product.class);
                    sugestaoList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadProductList(String categoryId) {
        Query searchQuery = productList.orderByChild("menuId").equalTo(categoryId);
        FirebaseRecyclerOptions<Product> productOption = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(searchQuery, Product.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(productOption) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder productViewHolder, final int position, @NonNull final Product model) {
                productViewHolder.product_name.setText(model.getName());
                productViewHolder.product_price.setText(String.format("R$ %s" , model.getPrice().toString()));
                Picasso.with(getBaseContext()).load(model.getImage()).into(productViewHolder.product_image);

                //btn_quick_cart
                productViewHolder.btn_quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new Database(getBaseContext()).addToCart(new Order(
                                adapter.getRef(position).getKey(),
                                model.getName(),
                                "1",
                                model.getPrice(),
                                model.getDiscount()
                        ));

                        Toast.makeText(ProductList.this, "Produto Adicionado ao Carrinho", Toast.LENGTH_SHORT).show();
                    }
                });

                //add Favorites
                if(localDB.isFavorites(adapter.getRef(position).getKey())){
                    productViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                productViewHolder.share_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(ProductList.this, "TESTE CLIQUE", Toast.LENGTH_SHORT).show();
                        Picasso.with(getApplicationContext())
                                .load(model.getImage())
                                .into(target);
                    }
                });

                //click to change state of favorites
                productViewHolder.fav_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!localDB.isFavorites(adapter.getRef(position).getKey())){
                            localDB.addToFavorites(adapter.getRef(position).getKey());
                            productViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                            Toast.makeText(ProductList.this, "" + model.getName() + " foi adicionado como Favorito.", Toast.LENGTH_LONG).show();
                        }else{
                            localDB.removeFromFavorites(adapter.getRef(position).getKey());
                            productViewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                            Toast.makeText(ProductList.this, "" + model.getName() + " foi removido como Favorito.", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                final Product local = model;
                productViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent productDetail = new Intent(ProductList.this, ProductDetail.class);
                        productDetail.putExtra("productId",adapter.getRef(position).getKey());
                        startActivity(productDetail);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_item, parent, false);
                return new ProductViewHolder(view);
            }
        };
        adapter.startListening();
        recycler_product.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);


    }

}
