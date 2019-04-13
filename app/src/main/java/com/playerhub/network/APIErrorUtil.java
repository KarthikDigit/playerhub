package com.playerhub.network;

import android.text.TextUtils;
import android.util.Log;


import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
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
            error = getDefaultError(null);
        }

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
}
