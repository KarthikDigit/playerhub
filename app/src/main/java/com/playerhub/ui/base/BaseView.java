package com.playerhub.ui.base;

import retrofit2.Response;

public interface BaseView {

    void serverError(Response<?> response, boolean isToastMsg);

    void onTimeout(boolean isToastMsg);

    void onNetworkError(boolean isToastMsg);

    void onUnknownError(String message, boolean isToastMsg);
}
