package projetodeliverytccservidor.com.br.projetodeliverytccservidor;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.ViewHolder.ProductViewHolder;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.common.Common;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.interfaces.ItemClickListener;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Product;

public class ProductList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;

    RelativeLayout rootLayout;

    FirebaseDatabase db;
    DatabaseReference products;
    FirebaseStorage storage;
    StorageReference storageReference;

    String categoryId = "";

    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;

    MaterialEditText txtName, txtDescription, txtPrice, txtDiscount;
    FButton btnSelect, btnUpload;

    Uri saveUri;

    Product newProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        db = FirebaseDatabase.getInstance();
        products = db.getReference("Product");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recyclerView = findViewById(R.id.recycler_product);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        rootLayout = findViewById(R.id.rootLayout);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });

        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        Log.e("HOME.CLASS","CategoryId:" + categoryId);

        if(!categoryId.isEmpty() && categoryId != null){
            loadListProduto(categoryId);
        }

    }

    private void loadListProduto(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(
                Product.class,
                R.layout.product_item,
                ProductViewHolder.class,
                products.orderByChild("menuId").equalTo(categoryId)
        ) {
            @Override
            protected void populateViewHolder(ProductViewHolder productViewHolder, Product product, int i) {
                productViewHolder.produto_nome.setText(product.getName());
                Picasso.with(getBaseContext()).
                        load(product.getImage()).
                        into(productViewHolder.produto_image);

                productViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //showAddProductDialog();
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void uploadImage() {
        if(saveUri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Enviando fotos...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("produtos/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ProductList.this, "Enviado!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newProduct = new Product();
                            newProduct.setName(txtName.getText().toString());
                            newProduct.setDescription(txtDescription.getText().toString());
                            newProduct.setPrice(txtPrice.getText().toString());
                            newProduct.setDiscount(txtDiscount.getText().toString());
                            newProduct.setMenuId(categoryId);
                            newProduct.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProductList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progres = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Enviando " + progres + "%");
                }
            });
        }
    }
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecione uma foto"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            saveUri = data.getData();
            btnSelect.setText("Image Selecionada!");
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductList.this);
        alertDialog.setTitle("Add Produto");
        alertDialog.setMessage("Por favor preencha as informações completas");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_product = inflater.inflate(R.layout.add_new_product_layout, null);

        txtName = add_new_product.findViewById(R.id.txtName);
        txtDescription = add_new_product.findViewById(R.id.txtDescription);
        txtPrice = add_new_product.findViewById(R.id.txtPrice);
        txtDiscount = add_new_product.findViewById(R.id.txtDesconto);

        btnSelect = add_new_product.findViewById(R.id.btnSelect);
        btnUpload = add_new_product.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        alertDialog.setView(add_new_product);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Button para envio
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(newProduct != null){
                    products.push().setValue(newProduct);
                    Snackbar.make(rootLayout, "Novo Produto " + newProduct.getName() +
                            " adicionado com sucesso.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if(item.getTitle().equals(Common.UPDATE)){
            showUpdateProduct(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
        }else if(item.getTitle().equals(Common.DELETE)){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductList.this);
            alertDialog.setTitle("ATENÇÃO");
            alertDialog.setMessage("Deseja realmente excluir esse Produto?");
            alertDialog.setIcon(R.drawable.ic_report_black_24dp);
            alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    deleteProduct(adapter.getRef(item.getOrder()).getKey());
                    Snackbar.make(rootLayout, "Produto excluído com sucesso", Snackbar.LENGTH_SHORT).show();
                }
            });

            alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

        }

        return super.onContextItemSelected(item);

    }

    private void deleteProduct(String key) {
        products.child(key).removeValue();
    }

    private void showUpdateProduct(final String key, final Product item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductList.this);
        alertDialog.setTitle("Atualizar Produto");
        alertDialog.setMessage("Por favor preencha as informações completas");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_new_product = inflater.inflate(R.layout.add_new_product_layout, null);

        txtName = add_new_product.findViewById(R.id.txtName);
        txtDescription = add_new_product.findViewById(R.id.txtDescription);
        txtPrice = add_new_product.findViewById(R.id.txtPrice);
        txtDiscount = add_new_product.findViewById(R.id.txtDesconto);

        txtName.setText(item.getName());
        txtDescription.setText(item.getDescription());
        txtPrice.setText(item.getPrice());
        txtDiscount.setText(item.getDiscount());

        btnSelect = add_new_product.findViewById(R.id.btnSelect);
        btnUpload = add_new_product.findViewById(R.id.btnUpload);

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage(item);
            }
        });

        alertDialog.setView(add_new_product);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        //Button para envio
        alertDialog.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                    item.setName(txtName.getText().toString());
                    item.setDescription(txtDescription.getText().toString());
                    item.setPrice(txtPrice.getText().toString());
                    item.setDiscount(txtDiscount.getText().toString());

                    products.child(key).setValue(item);
                    Snackbar.make(rootLayout, "Produto " + item.getName() +
                            " atualizado com sucesso.", Snackbar.LENGTH_SHORT).show();

            }
        });

        alertDialog.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void changeImage(final Product item) {
        if(saveUri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Enviando fotos...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("produtos/" + imageName);
            imageFolder.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.dismiss();
                    Toast.makeText(ProductList.this, "Enviado!", Toast.LENGTH_SHORT).show();
                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setImage(uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(ProductList.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progres = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Enviando " + progres + "%");
                }
            });
        }
    }
}
