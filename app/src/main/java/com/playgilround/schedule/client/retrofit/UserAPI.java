package com.playgilround.schedule.client.retrofit;

import com.google.gson.JsonObject;
import com.playgilround.schedule.client.model.BaseResponse;
import com.playgilround.schedule.client.model.ResponseMessage;

import java.util.HashMap;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserAPI {

//    @Headers({"Content-Type: application/json"})
//    @POST(BaseUrl.PATH_SIGN_UP)
//    Call<ResponseMessage> signUp(@Body JsonObject user);
    @POST(BaseUrl.PATH_SIGN_UP)
    Single<BaseResponse<String>> register(@Body HashMap map);

    @Headers({"Content-Type: application/json"})
    @POST(BaseUrl.PATH_EMAIL_SIGN_IN)
    Call<JsonObject> emailSignIn(@Body JsonObject user);

    @GET(BaseUrl.PATH_DUPLICATE_EMAIL)
    Call<JsonObject> checkEmail(@Query(BaseUrl.PARAM_SIGN_IN_EMAIL) String email);

    @GET(BaseUrl.PATH_DUPLICATE_NICKNAME)
    Call<JsonObject> checkNickName(@Query(BaseUrl.PARAM_SIGN_IN_NICKNAME) String nickname);

    @Headers({"Content-Type: application/json"})
    @POST(BaseUrl.PATH_TOKEN_SIGN_IN)
    Call<JsonObject> tokenSignIn(@Body JsonObject token);

    @Headers({"Content-Type: application/json"})
    @GET(BaseUrl.PATH_USER_SEARCH)
    Call<JsonObject> searchUserName(@Query(BaseUrl.PARAM_USER_NAME_SEARCH) String name);

}
