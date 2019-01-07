package br.com.projetodeliverytcc.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import br.com.projetodeliverytcc.model.User;
import br.com.projetodeliverytcc.remote.APIService;
import br.com.projetodeliverytcc.remote.RetrofitClient;

/**
 * Created by daniel on 13/02/18.
 */

public class Common {

    public static User userCurrent;
    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static String convertStatus(String status) {
        //0=PEDIDO RECEBIDO, 1=CONFERINDO PEDIDO, 2=PEDIDO ENVIADO
        if(status.equals("0")){
            return "PEDIDO RECEBIDO";
        }else if(status.equals("1")){
            return "PEDIDO CONFERIDO";
        }else{
            return "PEDIDO ENVIADO";
        }
    }

    public static String DELETE = "Excluir";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static boolean isConnectedInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
        if(info != null){
            for (int i = 0; i < info.length; i++) {
                if(info[i].getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }


}
