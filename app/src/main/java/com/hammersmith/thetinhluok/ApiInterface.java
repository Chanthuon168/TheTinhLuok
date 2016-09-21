package com.hammersmith.thetinhluok;

import com.hammersmith.thetinhluok.model.Category;
import com.hammersmith.thetinhluok.model.Comment;
import com.hammersmith.thetinhluok.model.Favorite;
import com.hammersmith.thetinhluok.model.Image;
import com.hammersmith.thetinhluok.model.Love;
import com.hammersmith.thetinhluok.model.MyProduct;
import com.hammersmith.thetinhluok.model.Product;
import com.hammersmith.thetinhluok.model.Reply;
import com.hammersmith.thetinhluok.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

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
    Call<List<Product>> getSpecialPromotion();

    @GET("recently/added")
    Call<List<Product>> getRecentlyAdded();

    @GET("product/{id}")
    Call<List<Product>> getProduct(@Path("id") int id);

    @GET("image/{id}")
    Call<List<Image>> getImage(@Path("id") int id);

    @GET("product/detail/{id}")
    Call<Product> getProductDetail(@Path("id") int id);

    @GET("get/favorite/{social_link}")
    Call<List<Favorite>> getFavorite(@Path("social_link") String socialLink);

    @GET("delete/favorite/{id}/{social_link}")
    Call<List<Favorite>> deleteFavorite(@Path("id") int id, @Path("social_link") String socialLink);

    @GET("get/myproduct/{social_link}")
    Call<List<MyProduct>> getMyProduct(@Path("social_link") String socialLink);

    @GET("delete/myproduct/{id}/{social_link}")
    Call<List<MyProduct>> deleteMyProduct(@Path("id") int id, @Path("social_link") String socialLink);

    @POST("create/favorite")
    Call<Favorite> createFavorite(@Body Favorite favorite);

    @GET("get/favorite/{id}/{social_link}")
    Call<Favorite> getFavoriteStatus(@Path("id") int id, @Path("social_link") String socialLink);

    @GET("count/love/{id}")
    Call<Love> getCountLove(@Path("id") int id);

    @GET("love/status/{id}/{social_link}")
    Call<Love> getLoveStatus(@Path("id") int id, @Path("social_link") String socialLink);

    @POST("create/love")
    Call<Love> createLove(@Body Love love);

    @GET("count/comment/{id}")
    Call<Comment> getCountComment(@Path("id") int id);

    @GET("get/comment/{id}")
    Call<List<Comment>> getComment(@Path("id") int id);

    @POST("create/comment")
    Call<Comment> createComment(@Body Comment comment);

    @GET("get/reply/{id}")
    Call<List<Reply>> getReply(@Path("id") int id);

    @POST("create/reply")
    Call<Reply> createReply (@Body Reply reply);

    @GET("get/category")
    Call<List<Category>> getCategory ();
}
