package projetodeliverytccservidor.com.br.projetodeliverytccservidor.service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import projetodeliverytccservidor.com.br.projetodeliverytccservidor.common.Common;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        updateToServer(refreshToken);
    }

    private void updateToServer(String refreshToken) {
        if(Common.currentUser != null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token token = new Token(refreshToken, true);
            tokens.child(Common.currentUser.getPhone()).setValue(token);
        }
    }
}
