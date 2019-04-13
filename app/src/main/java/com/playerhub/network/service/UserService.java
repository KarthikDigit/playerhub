package com.playerhub.network.service;



import com.playerhub.network.request.LoginRequest;
import com.playerhub.network.request.RegistrationRequest;
import com.playerhub.network.response.LoginResponse;
import com.playerhub.network.response.RegistrationResponse;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface UserService {

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("auth/login")
    Observable<LoginResponse> login(@Body LoginRequest json);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("auth/signup")
    Observable<RegistrationResponse> register(@Body RegistrationRequest json);

   /* @FormUrlEncoded
    @POST("create_room")
    Call<CreateRoomResponse> createRoom(@Field("user_id") String username, @Field("room_name") String roomName);

    @GET("view_rooms")
    Call<List<ViewRoomResponse>> getRooms();

    @FormUrlEncoded
    @POST("join_room")
    Call<JoinRoomResponse> joinRoom(@Field("user_id") String username, @Field("room_name") String roomName);*/

}
