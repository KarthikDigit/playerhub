package com.playerhub.preference;


import android.content.Context;

import java.util.HashMap;
import java.util.Map;


/**
 * Preferences to store/retrieve values/model as string/JsonParcels
 */
public enum Preferences {
    INSTANCE;

    // Preference Name
    private static final String PREF_NAME = "playerhub_pref";

    // Preference keys]
    private static final String IS_USER_LOGGED_IN = "is_user_logged_in";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String USER_MOBILE_NUMBER = "user_mobile_number";
    private static final String USER_ID = "user_id";
    private static final String MSG_USER_ID = "msg_user_id";
    private static final String ROLE_ID = "role_id";
    private static final String ROLE = "role";
    private static final String HOST_ID = "host_id";
    private static final String USER_NAME = "user_name";
    private static final String EMAIL = "email";
    private static final String LOGO = "logo";
    private static final String TYPE = "type";
    private static final String COUNTRY_CODE = "country_code";
    private static final String PHONE = "phone";
    private static final String USER_ROOM_NUMBER = "user_room_number";
    private static final String USER_AUTH_TOKEN = "user_token";
    private static final String USER_TYPE = "user_type";
    private static final String TOKEN_TYPE = "token_type";
    private static final String VERIFY_PROFILE_POPUP = "verify_profile_popup";

    private static final String TWILIO_ACCESS_TOKEN = "twilio_access_token";
    private static final String TWILIO_SID = "twilio_sid";
    private static final String USER_ROOM_NAME = "user_room_name";
    private static final String FCM_TOKEN = "fcm_token";
    private static final String KEY_FCM_TOKEN = "fcmtoken";
    private static final String KEY_DEVICE_ID = "deviceid";
    private static final String WHISHLIST_PRODUCT = "whish_list_products";
    private static final String KEY_NOTI_COUNT = "noti_count";
    private static final String KEY_IS_FIRSTTIME_LAUNCH = "is_first_time_launch";
    private static final String KEY_AUTO_IMAGE_DOWNLOAD = "auto_image_download";

    private UserPreferences mPreferenceHandle;

    public void createPreferences(Context context) {
        mPreferenceHandle = new UserPreferences(context, PREF_NAME);
    }

    public void clearPreference() {
        if (mPreferenceHandle != null) {
            mPreferenceHandle.clearPreference();
        }
    }

    public void putNotiCount(int count) {

        mPreferenceHandle.setInt(KEY_NOTI_COUNT, count);
    }


    public void putAutoImageDownload(boolean isDownload) {

        mPreferenceHandle.setBoolean(KEY_AUTO_IMAGE_DOWNLOAD, isDownload);
    }

    public boolean getAutoImageDownload() {

        return mPreferenceHandle.getBoolean(KEY_AUTO_IMAGE_DOWNLOAD, true);
    }

    public void putIsFirstTimeLaunch(boolean isFirstTime) {

        mPreferenceHandle.setBoolean(KEY_IS_FIRSTTIME_LAUNCH, isFirstTime);
    }


    public boolean getIsFirstTimeLaunch() {
        return mPreferenceHandle.getBoolean(KEY_IS_FIRSTTIME_LAUNCH, true);
    }

    public void putUserType(String user_type) {

        mPreferenceHandle.setString(USER_TYPE, user_type);
    }

    public String getUserType() {

        return mPreferenceHandle.getString(USER_TYPE, null);
    }

    public int getNotCount() {
        return mPreferenceHandle.getInt(KEY_NOTI_COUNT, 0);
    }

    public void putLogo(String logo) {
        mPreferenceHandle.setString(LOGO, logo);
    }

    public String getLogo() {
        return mPreferenceHandle.getString(LOGO, "");
    }

    public void putType(String type) {
        mPreferenceHandle.setString(TYPE, type);
    }

    public String getTYPE() {
        return mPreferenceHandle.getString(TYPE, "");
    }

    public void putCountCode(String code) {
        mPreferenceHandle.setString(COUNTRY_CODE, code);
    }

    public String getCountryCode() {
        return mPreferenceHandle.getString(COUNTRY_CODE, "");
    }

    public void putPhone(String phone) {
        mPreferenceHandle.setString(PHONE, phone);
    }

    public String getPHONE() {
        return mPreferenceHandle.getString(PHONE, "");
    }

    public void putUserLoggedInStatus(boolean loggedIn) {
        mPreferenceHandle.setBoolean(IS_USER_LOGGED_IN, loggedIn);
    }

    public boolean isUserLoggedIn() {
        return mPreferenceHandle.getBoolean(IS_USER_LOGGED_IN, false);
    }

    //---------------------------------------


    public void putMsgUserId(String msgUserId) {

        mPreferenceHandle.setString(MSG_USER_ID, msgUserId);
    }

    public String getMsgUserId() {

        return mPreferenceHandle.getString(MSG_USER_ID, null);
    }

    public void putFcmToken(String token) {

        mPreferenceHandle.setString(KEY_FCM_TOKEN, token);
    }

    public String getFcmToken() {
        return mPreferenceHandle.getString(KEY_FCM_TOKEN, null);
    }

    public void putDeviceId(String deviceId) {

        mPreferenceHandle.setString(KEY_DEVICE_ID, deviceId);

    }

    public String getDeviceId() {
        return mPreferenceHandle.getString(KEY_DEVICE_ID, null);
    }

    public void putEmail(String email) {

        mPreferenceHandle.setString(EMAIL, email);

    }

    public String getEmail() {
        return mPreferenceHandle.getString(EMAIL, null);
    }

    public void putWhishListProducts(String whishlist) {

        mPreferenceHandle.setString(WHISHLIST_PRODUCT, whishlist);
    }

    public String getWhishlistProduct() {

        return mPreferenceHandle.getString(WHISHLIST_PRODUCT, null);
    }

    public void putRoleId(String roleId) {
        mPreferenceHandle.setString(ROLE_ID, roleId);
    }

    public void putRole(String role) {
        mPreferenceHandle.setString(ROLE, role);
    }

    public String getRoleId() {
        return mPreferenceHandle.getString(ROLE_ID, null);
    }

    public String getROLE() {
        return mPreferenceHandle.getString(ROLE, null);
    }

    public void putAccessToken(String accesstoken) {
        mPreferenceHandle.setString(ACCESS_TOKEN, accesstoken);
    }

    public void putTokenType(String tokenType) {
        mPreferenceHandle.setString(TOKEN_TYPE, tokenType);
    }

    public String getTokenType() {
        return mPreferenceHandle.getString(TOKEN_TYPE, null);
    }

    public String getAccessToken() {
        return mPreferenceHandle.getString(ACCESS_TOKEN, null);
    }

    public void putUserMobileNumber(String mobileNumber) {
        mPreferenceHandle.setString(USER_MOBILE_NUMBER, mobileNumber);
    }

    public String getUserMobileNumber() {
        return mPreferenceHandle.getString(USER_MOBILE_NUMBER, null);
    }

    public void putUserId(String userId) {
        mPreferenceHandle.setString(USER_ID, userId);
    }

    public String getUserId() {
        return mPreferenceHandle.getString(USER_ID, null);
    }

    public void putHostId(String hostId) {
        mPreferenceHandle.setString(HOST_ID, hostId);
    }

    public String getHostId() {
        return mPreferenceHandle.getString(HOST_ID, null);
    }

    public String getUserName() {
        return mPreferenceHandle.getString(USER_NAME, "");
    }

    public void putUserName(String userName) {
        mPreferenceHandle.setString(USER_NAME, userName);
    }

    public String getUserRoomNumber() {
        return mPreferenceHandle.getString(USER_ROOM_NUMBER, null);
    }

    public void putUserRoomNUmber(String userRoomNumber) {
        mPreferenceHandle.setString(USER_ROOM_NUMBER, userRoomNumber);
    }

    public String getUserAuthToken() {
        return mPreferenceHandle.getString(USER_AUTH_TOKEN, null);
    }

    public void putUserAuthToken(String authToken) {
        mPreferenceHandle.setString(USER_AUTH_TOKEN, authToken);
    }

    public void putPopupDontshow(boolean dontShow) {
        mPreferenceHandle.setBoolean(VERIFY_PROFILE_POPUP, dontShow);
    }

    public boolean isDontShow() {
        return mPreferenceHandle.getBoolean(VERIFY_PROFILE_POPUP, false);
    }

    public String getUserRoomName() {
        return mPreferenceHandle.getString(USER_ROOM_NAME, null);
    }

    public void putUserRoomName(String roomName) {
        mPreferenceHandle.setString(USER_ROOM_NAME, roomName);
    }

    public String getTwilioAccessToken() {
        return mPreferenceHandle.getString(TWILIO_ACCESS_TOKEN, null);
    }

    public void putTwilioAccessToken(String accessToken) {
        mPreferenceHandle.setString(TWILIO_ACCESS_TOKEN, accessToken);
    }

    public String getTwilioSid() {
        return mPreferenceHandle.getString(TWILIO_SID, null);
    }

    public void putTwilioSid(String sid) {
        mPreferenceHandle.setString(TWILIO_SID, sid);
    }


    public String getFCMToken() {
        return mPreferenceHandle.getString(FCM_TOKEN, null);
    }

    public void putFCMToken(String fcmToken) {
        mPreferenceHandle.setString(FCM_TOKEN, fcmToken);
    }

    public Map<String, String> getAuthendicate() {

        Map<String, String> hashMap = new HashMap<>();

        String auth = getTokenType() + " " + getAccessToken();


        hashMap.put("Authorization", auth);
        hashMap.put("Accept", "application/json");

        return hashMap;

    }

    public void saveNotification(String keyId, String msg) {

        mPreferenceHandle.setString(keyId, msg);
    }

    public String getNotificationById(String keyId) {

        return mPreferenceHandle.getString(keyId, null);
    }
}
