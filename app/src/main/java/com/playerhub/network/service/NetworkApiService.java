package com.playerhub.network.service;


import com.playerhub.network.request.Deviceinfo;
import com.playerhub.network.request.UpdateKidDetail;
import com.playerhub.network.request.UploadKidProfile;
import com.playerhub.network.response.AnnouncementApi;
import com.playerhub.network.response.ContactListApi;
import com.playerhub.network.response.EventDetailsApi;
import com.playerhub.network.response.EventListApi.EventListResponseApi;
import com.playerhub.network.response.KidDetailsUpdatedResponse;
import com.playerhub.network.response.KidInfoResponse;
import com.playerhub.network.response.KidsAndCoaches;
import com.playerhub.network.response.NotificationApi;
import com.playerhub.network.response.OTPValidateApi;
import com.playerhub.network.response.ProfileDetails;
import com.playerhub.network.response.ReadNotification;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NetworkApiService {

    @GET("events")
    Observable<EventListResponseApi> fetchAllEvents(@HeaderMap Map<String, String> headermap);

    @GET("events/{id}")
    Observable<EventDetailsApi> fetchEventDetailsById(@HeaderMap Map<String, String> headermap, @Path("id") int id);

    //
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("deviceinfo")
    Observable<String> postPustId(@HeaderMap Map<String, String> headermap, @Body Deviceinfo json);

    //    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("notifications")
    Observable<NotificationApi> getAllNotification(@HeaderMap Map<String, String> headermap);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("notifications/{id}")
    Observable<String> deleteNotificationById(@HeaderMap Map<String, String> headermap, @Path("id") int id);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("notifications/clearall")
    Observable<String> deleteAllNotification(@HeaderMap Map<String, String> headermap);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("notifications/read/{id}")
    Observable<ReadNotification> readNotification(@HeaderMap Map<String, String> headermap, @Path("id") String id);


    @GET("user")
    Observable<ProfileDetails> fetchUserDetails(@HeaderMap Map<String, String> headermap);

    @GET("contactlist")
    Observable<ContactListApi> fetchContactList(@HeaderMap Map<String, String> headermap);

    @GET("contactlist")
    Observable<String> fetchContactList1(@HeaderMap Map<String, String> headermap);

    @GET("announcements")
    Observable<AnnouncementApi> fetchAnnouncements(@HeaderMap Map<String, String> headermap);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("announcements/{id}")
    Observable<String> getAnnouncementById(@HeaderMap Map<String, String> headermap, @Path("id") int id);


    @Headers({"Content-Type: application/json;charset=UTF-8",
            "Authorization: key=AAAA5zQ5y4I:APA91bFEXLS3t0uo6AjGFmuof-ZpFosw5HXGe9E0cBCOVDBrzjriffudkh5lb6AgX42cImB378nB-8lxKSdHjZR0yWsZRx7wzAMoT93agNqdv_z9M1kV9pIp8nPBctF7yKTzYi0mNOzZ"})
    @POST("https://fcm.googleapis.com/fcm/send")
    Observable<String> sendPustNotification(@Body String bodyMap);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("verify-otp")
    Observable<OTPValidateApi> validateOtp(@Body String json);


    @GET("kids-coaches")
    Observable<KidsAndCoaches> fetchKids(@HeaderMap Map<String, String> headermap);


    @GET("kid/{id}")
    Observable<KidInfoResponse> fetchKidDetailsById(@HeaderMap Map<String, String> headermap, @Path("id") int id);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("kid/{id}")
    Observable<KidDetailsUpdatedResponse> updateKidDetails(@HeaderMap Map<String, String> headermap, @Body UpdateKidDetail json, @Path("id") int id);


    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("kid/avatar/{id}")
    Observable<String> updateKidProfileImage(@HeaderMap Map<String, String> headermap, @Body UploadKidProfile json, @Path("id") int id);


//    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @FormUrlEncoded
    @POST("kid/avatar/{id}")
    Observable<String> updateKidProfileImage1(@HeaderMap Map<String, String> headermap, @Field("avatar") String avatar, @Path("id") int id);


//    @Headers({"Content-Type: application/json;charset=UTF-8"})
//    @POST("categories")
//    Observable<CategoryAddedApi> addCategory(@HeaderMap Map<String, String> headermap, @Body CategoryRequest json);
//
//    @Multipart
//    @Headers({"Content-Type: application/json;charset=UTF-8"})
//    @POST("products")
//    Observable<String> addProducts(@HeaderMap Map<String, String> headermap, @Part MultipartBody.Part file, @Part("json") RequestBody json);
//
//
//    @Headers({"Content-Type: application/json;charset=UTF-8"})
//    @POST("products")
//    Observable<AddedProductApi> addProducts(@HeaderMap Map<String, String> headermap, @Body AddProductRequest json);
//
//    @Headers({"Content-Type: application/json;charset=UTF-8"})
//    @POST("products/{id}")
//    Observable<UpdateProductApi> updateProducts(@HeaderMap Map<String, String> headermap, @Path("id") int id, @Body AddProductRequest json);
//
//
//    @Headers({"Content-Type: application/json;charset=UTF-8"})
//    @POST("products/{id}")
//    Observable<DeleteApi> deleteProduct(@HeaderMap Map<String, String> headermap, @Path("id") int id, @Body DeleteRequest request);

//
//    @Headers({"Content-Type: application/json;charset=UTF-8"})
//    @POST("contacts")
//    Observable<CommentsApi> postComments(@HeaderMap Map<String, String> headermap, @Body PostCommentsRequest json);
//
//
//    //
////    @Multipart
////    @Headers({"Content-Type: application/json;charset=UTF-8"})
////    @POST("products")
////    Observable<String> addProducts(@HeaderMap Map<String, String> headermap, @Part("product_image") RequestBody product_image, @Body AddProductRequest json);


//
//    @GET("public/api/poojas/{id}")
//    Call<PoojaDetailApi> fetchPoojaDetails(@Path("id") String id);
//
//    @GET("public/api/tours")
//    Call<PilgrimageToursListApi> fetchToursList();
//
//    @GET("public/api/tours/{id}")
//    Call<PilgrimageTourDetailsApi> fetchTourDetails(@Path("id") String id);
//
//    @GET("public/api/audios")
//    Call<AudioVideoMessageListApi> fetchAudioMessages();
//
//    @GET("public/api/videos")
//    Call<AudioVideoMessageListApi> fetchVideoMessages();
//
//    @GET("public/api/audios/{id}")
//    Observable<AudioVideoMessageDetailApi> fetchAudioMessageDetails(@Path("id") String id);
//
//    @GET("public/api/videos/{id}")
//    Call<AudioVideoMessageDetailApi> fetchVideoMessageDetails(@Path("id") String id);
//
//    @GET("public/api/slokas")
//    Observable<SlokasMantrasListApi> fetchSlokasList();
//
//    @GET("public/api/slokas/{id}")
//    Call<SlokasMantrasDetailApi> fetchSlokasWithId(@Path("id") String id);
//
//    @GET("public/api/mantras")
//    Observable<SlokasMantrasListApi> fetchMantrasList();
//
//    @GET("public/api/mantras/{id}")
//    Call<SlokasMantrasDetailApi> fetchMantrasWithId(@Path("id") String id);
//
//    @GET("public/api/slokadays")
//    Observable<SlokaOfTheDayApi> fetchSlokaOfTheDayList();
//
//    @GET("public/api/events")
//    Call<CurrentEventsListApi> fetchCurrentEventsList();
//
//    @GET("public/api/events/{id}")
//    Call<CurrentEventsDetailsApi> fetchCurrentEventDetails(@Path("id") String id);
//
//    @GET("public/api/eappnotices")
//    Call<EappEventsListApi> fetchEappEventsList();
//
//    @GET("public/api/eappnotices/{id}")
//    Call<EappEventsDetailsApi> fetchEappEventDetails(@Path("id") String id);
//
//    @GET("public/api/testimonials")
//    Observable<TestimonialListApi> fetchTestimonialInfo();
//
//    @POST("public/api/testimonials")
//    Observable<TestimonialRequest> postTestimonialInfo(@Body TestimonialRequest testimonialRequest);

}
