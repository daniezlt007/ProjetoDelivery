package projetodeliverytccservidor.com.br.projetodeliverytccservidor.remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by daniel on 10/03/18.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;
    public static Retrofit getClient(String baseUrl){
        if(retrofit == null){
            retrofit = new Retrofit.
                    Builder().
                    baseUrl(baseUrl).
                    addConverterFactory(ScalarsConverterFactory.create()).
                    build();
        }
        return retrofit;
    }

}
