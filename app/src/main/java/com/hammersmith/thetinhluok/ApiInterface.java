package com.hammersmith.thetinhluok;

import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Chan Thuon on 9/9/2016.
 */
public interface ApiInterface {
    @POST("user/login")
    Call<User> createUserBySocial(@Body User user);
    @POST("user/register")
    Call<User> userRegister(@Body User user);
    @POST("user/loginaccount")
    Call<User> userLoginByEmail(@Body User user);
    @GET("special/promotion")
    Call<List<Product>> getSpecialPromotion ();
    @GET("recently/added")
    Call<List<Product>> getRecentlyAdded();
}
