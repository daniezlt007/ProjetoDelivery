package br.com.projetodeliverytcc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.rey.material.widget.CheckBox;

import br.com.projetodeliverytcc.common.Common;
import br.com.projetodeliverytcc.model.User;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    private FButton btnSignIn;
    private EditText txtPhone, txtPassword;
    private static final String TAG = "SignIn";
    //private com.rey.material.widget.CheckBox chkRemember;
    private CheckBox chkRemember;
    private TextView forgotPassword;
    FirebaseDatabase database;
    DatabaseReference tableUser;

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
        setContentView(R.layout.activity_sign_in);

        txtPhone = findViewById(R.id.txtPhone);
        txtPassword = findViewById(R.id.txtPassword);

        btnSignIn = findViewById(R.id.btnSignIn);

        chkRemember = findViewById(R.id.chkRemember);

        forgotPassword = findViewById(R.id.forgotPassword);

        //init paper

        Paper.init(this);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        tableUser = database.getReference("User");

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPassword();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedInternet(getBaseContext())) {
                    if(chkRemember.isChecked()){
                        Paper.book().write(Common.USER_KEY, txtPhone.getText().toString());
                        Paper.book().write(Common.PWD_KEY, txtPassword.getText().toString());
                    }
                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Por favor Aguarde...");
                    mDialog.show();

                    tableUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get user
                            if (dataSnapshot.child(txtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                User user = dataSnapshot.child(txtPhone.getText().toString()).getValue(User.class);
                                user.setPhone(txtPhone.getText().toString());
                                if (user.getPassword().equals(txtPassword.getText().toString())) {
                                    Intent homeIntent = new Intent(SignIn.this, Home.class);
                                    Common.userCurrent = user;
                                    startActivity(homeIntent);
                                    finish();
                                    tableUser.removeEventListener(this);
                                } else {
                                    Toast.makeText(SignIn.this, "Login falhou", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "Usuário não existe na Database", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else{
                    Toast.makeText(SignIn.this,"Verifique sua conexão",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
    }

    private void showForgotPassword() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lembrar Senha");
        builder.setMessage("Entre com seu Pin de Segurança");

        LayoutInflater inflater = this.getLayoutInflater();

        View forgot_view = inflater.inflate(R.layout.forgot_password, null);
        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final EditText txtPhoneCode = forgot_view.findViewById(R.id.txtPhoneCode);
        final EditText txtCodeSecure = forgot_view.findViewById(R.id.txtCodeSecure);

        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(txtPhoneCode.getText().toString()).getValue(User.class);
                        if(user.getSecureCode().equals(txtCodeSecure.getText().toString())){
                            Toast.makeText(SignIn.this,"Sua senha é: " + user.getPassword() , Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(SignIn.this,"Pin de Segurança inválido." , Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        builder.setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }
}
