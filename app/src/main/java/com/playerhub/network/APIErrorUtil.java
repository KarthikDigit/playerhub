package com.playerhub.network;

import android.text.TextUtils;
import android.util.Log;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Response;


public class APIErrorUtil {

    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                RetrofitAdapter.getRetrofit()
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error = null;
        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            Log.e("APIError ", e.getMessage());
        }

        if (error == null) {
//            error = (APIError) getDefaultError(null);
        }

        return error;
    }

    /**
     * HTTP response body converted to specified {@code type}. {@code null} if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified {@code type}.
     */
    public static <T> T getErrorBodyAs(Class<T> type, Response<?> response) throws IOException {
        if (response == null || response.errorBody() == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = RetrofitAdapter.getRetrofit().responseBodyConverter(type, new Annotation[0]);
        return converter.convert(response.errorBody());
    }

    public static <T> T getErrorBodyAs(Class<T> type, ResponseBody response) throws IOException {
        if (response == null) {
            return null;
        }
        Converter<ResponseBody, T> converter = RetrofitAdapter.getRetrofit().responseBodyConverter(type, new Annotation[0]);
        return converter.convert(response);
    }

    /**
     * Clones a raw buffer so as not to consume the original
     *
     * @param response the original {@link okhttp3.Response} as returned
     *                 by {@link Response#raw()}
     * @return a cloned {@link ResponseBody}
     */
    public static ResponseBody cloneResponseBody(Response<?> response) {
        final ResponseBody responseBody = response.errorBody();
        final Buffer bufferClone = responseBody.source().buffer().clone();
        return ResponseBody.create(responseBody.contentType(), responseBody.contentLength(), bufferClone);
    }

    public static synchronized <T> T parseErrorTest(Class<?> aClass, Response<?> response) {


        T error = null;
        try {
            error = (T) getErrorBodyAs(aClass, response);
        } catch (IOException e) {
            Log.e("APIError ", e.getMessage());
        }

//        if (error == null) {
//            error = null;
//        }

        return error;
    }

    public static synchronized <T> T parseErrorTest(Class<?> aClass, ResponseBody response) {


        T error = null;
        try {
            error = (T) getErrorBodyAs(aClass, response);
        } catch (IOException e) {
            Log.e("APIError ", e.getMessage());
        }

//        if (error == null) {
//            error = null;
//        }

        return error;
    }

    public static APIError getDefaultError(String message) {
        APIError apiError = new APIError();
        if (TextUtils.isEmpty(message)) {
            apiError.setMessage("Something went wrong");
        } else {
            apiError.setMessage(message);
        }
        return apiError;
    }

//    public static synchronized <T> T getDefaultError1(String message) {
//        BaseApiError apiError = new BaseApiError();
//        if (TextUtils.isEmpty(message)) {
//            apiError.setMessage("Something went wrong");
//        } else {
//            apiError.setMessage(message);
//        }
//        return (T) apiError;
//    }
}
