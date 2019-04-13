package com.playerhub.network;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.playerhub.common.Constant;
import com.playerhub.network.service.NetworkApiService;
import com.playerhub.network.service.UserService;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class RetrofitAdapter {

    private String headerData = null;

    private static Retrofit retrofit;

    private static Retrofit retrofitUpload;

    private static OkHttpClient okHttpClient;

    private static OkHttpClient okHttpUploadClient;

    public static final int UPLOAD_TIMEOUT_IN_MINUTES = 2;


    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.API_BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getNormalClient())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static NetworkApiService getNetworkApiServiceClient() {
        Retrofit apiClient = RetrofitAdapter.getRetrofit();
        return apiClient.create(NetworkApiService.class);
    }

    public static UserService getUserApiServiceClient() {
        Retrofit apiClient = RetrofitAdapter.getRetrofit();
        return apiClient.create(UserService.class);
    }

    public static Retrofit getRetrofitUpload() {
        if (retrofitUpload == null) {
            retrofitUpload = new Retrofit.Builder()
                    .baseUrl(Constant.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(getUploadClient())
                    .build();
        }
        return retrofitUpload;
    }


    private static OkHttpClient getNormalClient() {
        if (okHttpClient == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    // TODO: Since we are using simple GET Requests, this is not required now.
//                    .addInterceptor(new Interceptor() {
//                        @Override
//                        public Response intercept(Chain chain) throws IOException {
//                            Request original = chain.request();
//
//                            Request.Builder builder = original.newBuilder()
//                                    /*.header("Content-Type", "application/json")*/
//                                    .header("Content-Type", "application/json")
//                                    //.header("Content-Type", "application/x-www-form-urlencoded")
//                                    .method(original.method(), original.body());
//
//                            builder.header("Authorization", "Basic cm9vdDpwYXNzd29yZA==");
//
//                            Request request = builder.build();
//
//                            return chain.proceed(request);
//                        }
//                    })
                    .addInterceptor(logging);
            //okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private static OkHttpClient getUploadClient() {
        if (okHttpUploadClient == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request original = chain.request();

                            Request.Builder builder = original.newBuilder()
                                    /*.header("Content-Type", "application/json")*/
                                    .method(original.method(), original.body());

                            /*if (Preferences.INSTANCE.getAccessToken() != null) {

                            }*/

                            builder.header("Authorization", "Basic cm9vdDpwYXNzd29yZA==");

                            Request request = builder.build();

                            return chain.proceed(request);
                        }
                    })
                    .addInterceptor(logging)
                    .readTimeout(UPLOAD_TIMEOUT_IN_MINUTES, TimeUnit.MINUTES);
            okHttpUploadClient = builder.build();
        }
        return okHttpUploadClient;
    }

    public void clearRetrofitAdapters() {
        retrofit = null;
        retrofitUpload = null;
        okHttpClient = null;
        okHttpUploadClient = null;
    }

}
