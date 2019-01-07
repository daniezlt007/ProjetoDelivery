package projetodeliverytccservidor.com.br.projetodeliverytccservidor.remote;

import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.MyResponse;
import projetodeliverytccservidor.com.br.projetodeliverytccservidor.model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAFwQaIj4:APA91bEdDcGPrf1cW23R1SID2ma7V2FPVocBCVFiSX1UHR1ASm8AfG7I_xanvqNjKlpGBxNyBT2Y-kBJRsMDdy7RjGsOQdDJMR2G6-Hctao-E2J8nZz6TYZpb6s4e279Hy1yXoN1wQiw"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
