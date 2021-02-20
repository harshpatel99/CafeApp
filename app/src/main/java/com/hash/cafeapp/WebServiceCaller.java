package com.hash.cafeapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hash.cafeapp.models.Checksum;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class WebServiceCaller {

    private static WebServiceApiInterface webApiInterface;

    public static WebServiceApiInterface getClient() {
        if (webApiInterface == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okClient = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.MINUTES)
                    .writeTimeout(10, TimeUnit.MINUTES)
                    .build();
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl("http://caffienate.000webhostapp.com/paytm/")
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            webApiInterface = client.create(WebServiceApiInterface.class);
        }
        return webApiInterface;
    }


    public interface WebServiceApiInterface {

        @FormUrlEncoded
        @POST("generateChecksum.php")
        Call<Checksum> getChecksum(
                @Field("MID") String mId,
                @Field("ORDER_ID") String orderId,
                @Field("CUST_ID") String custId,
                @Field("CHANNEL_ID") String channelId,
                @Field("TXN_AMOUNT") String txnAmount,
                @Field("WEBSITE") String website,
                @Field("CALLBACK_URL") String callbackUrl,
                @Field("INDUSTRY_TYPE_ID") String industryTypeId
        );


    }

}
