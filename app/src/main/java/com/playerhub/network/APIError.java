package com.playerhub.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;


public class APIError extends BaseApiError {


    @SerializedName("data")
    @Expose
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @SerializedName("errors")
    @Expose
    private Errors errors;


    public Errors getErrors() {
        return errors;
    }

    public void setErrors(Errors errors) {
        this.errors = errors;
    }


    public static class Errors implements Serializable {

        @SerializedName("design_no")
        @Expose
        private List<String> designNo = null;
        private final static long serialVersionUID = 3450643270690039384L;

        public List<String> getDesignNo() {
            return designNo;
        }

        public void setDesignNo(List<String> designNo) {
            this.designNo = designNo;
        }

    }


    /*private Error error;

    private int statusCode;
    private String name;
    private String message;
    private String ERRORCODE;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return ERRORCODE;
    }

    public void setErrorCode(String errorCode) {
        this.ERRORCODE = errorCode;
    }*/


//    public static class Object {
//
//        @SerializedName("email")
//        @Expose
//        private List<String> email = null;
//        @SerializedName("password")
//        @Expose
//        private List<String> password = null;
//
//        public List<String> getEmail() {
//            return email;
//        }
//
//        public void setEmail(List<String> email) {
//            this.email = email;
//        }
//
//        public List<String> getPassword() {
//            return password;
//        }
//
//        public void setPassword(List<String> password) {
//            this.password = password;
//        }
//
//    }
}
