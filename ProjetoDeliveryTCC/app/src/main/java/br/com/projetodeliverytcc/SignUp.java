package br.com.projetodeliverytcc;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.projetodeliverytcc.common.Common;
import br.com.projetodeliverytcc.model.User;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUp extends AppCompatActivity {

    private FButton btnSignUp;
    private EditText txtPhone, txtName, txtPassword, txtSecureCode;
    private static final String TAG = "SignUp";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/delivery.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_sign_up);

        txtPhone = findViewById(R.id.txtPhone);
        txtName = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.txtPassword);
        txtSecureCode = findViewById(R.id.txtSecureCode);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference tableUser = database.getReference("User");

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Common.isConnectedInternet(getBaseContext())) {


                    final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                    mDialog.setMessage("Por favor Aguarde...");
                    mDialog.show();

                    tableUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(txtPhone.length() < 9){
                                Toast.makeText(SignUp.this, "Padrão de número inválido.", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }else {
                                if (dataSnapshot.child(txtPhone.getText().toString()).exists()) {
                                    mDialog.dismiss();
                                    Toast.makeText(SignUp.this, "Telefone já cadastrado", Toast.LENGTH_SHORT).show();
                                } else {
                                    mDialog.dismiss();

                                    User user = new User(txtName.getText().toString(),
                                            txtPassword.getText().toString(),
                                            txtSecureCode.getText().toString()
                                    );
                                    tableUser.child(txtPhone.getText().toString()).setValue(user);
                                    Toast.makeText(SignUp.this, "Usuário Cadastrado com Sucesso", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    Toast.makeText(SignUp.this,"Verifique sua conexão",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });


    }
}
