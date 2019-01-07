package projetodeliverytccservidor.com.br.projetodeliverytccservidor.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import projetodeliverytccservidor.com.br.projetodeliverytccservidor.igeocooordenada.IGeoCoordinates;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.remote.APIService;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.remote.FCMRetrofitClient;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.remote.RetrofitClient;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Request;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.User;

/**
 * Created by daniel on 04/03/18.
 */

public class Common {

    public static User currentUser;
    public static Request currentRequest;
    public static final String UPDATE = "Atualizar";
    public static final String DELETE = "Excluir";

    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";


    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String baseUrl = "https://maps.googleapis.com";
    public static final String fcmUrl = "https://fcm.googleapis.com";

    public static String converteStatusPedido(String status){
        if(status.equals("0")){
            return "PEDIDO RECEBIDO";
        }else if(status.equals("1")){
            return "PEDIDO CONFERIDO";
        }else{
            return "PEDIDO ENVIADO";
        }
    }

    public static IGeoCoordinates getGeoCodService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }

    public static APIService getFCMService(){
        return FCMRetrofitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeigth){
        bitmap = Bitmap.createBitmap(newWidth, newHeigth, Bitmap.Config.ARGB_8888);
        float scaleX = newWidth/(float) bitmap.getWidth();
        float scaleY = newHeigth/(float) bitmap.getHeight();
        float pivotX=0, pivotY=0;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);
        Canvas canvas = new Canvas(bitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0,0, new Paint(Paint.FILTER_BITMAP_FLAG));
        return bitmap;
    }
}
