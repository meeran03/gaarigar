package com.gianteyes.gaarigarmechanic.services;

import com.gianteyes.gaarigarmechanic.models.Category;
import com.gianteyes.gaarigarmechanic.models.LoginRequestModel;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;
import com.gianteyes.gaarigarmechanic.models.OrderModel;
import com.gianteyes.gaarigarmechanic.models.PaymentResponseDto;
import com.gianteyes.gaarigarmechanic.models.RegisterRequestModel;
import com.gianteyes.gaarigarmechanic.models.RegisterResponseModel;
import com.gianteyes.gaarigarmechanic.models.SendOTPRequestModel;
import com.gianteyes.gaarigarmechanic.models.StandardServicesResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://backend-production-17213.up.railway.app/api/";

    @POST("auth/login")
    Call<LoginResponseModel> loginCustomer(@Body LoginRequestModel request);

    @POST("auth/register")
    Call<RegisterResponseModel> registerCustomer(@Body RegisterRequestModel request);

    @POST("auth/otp/")
    Call<SendOTPRequestModel> sendOTP(@Body SendOTPRequestModel request);

    @Multipart
    @PATCH("customer/update/{customerId}")
    Call<ResponseBody> updateCustomer(@Path("customerId") Long customerId,
                                      @PartMap Map<String, RequestBody> partMap,
                                      @Part MultipartBody.Part image);

    @GET("order/{customerId}")
    Call<List<OrderModel>> getOrders(
            @Path("customerId") Long customerId,
            @Query("statuses") String status,
            @Query("startDate") LocalDateTime startDate,
            @Query("endDate") LocalDateTime endDate
    );

    @POST("order/pay/{orderId}")
    Call<PaymentResponseDto> payOrder(@Path("orderId") Long orderId, @Body String apiVersion);

    @GET("category/")
    Call<ArrayList<Category>> getCategories();

    @GET("mechanic-standard-service/{id}")
    Call<List<StandardServicesResponse>> getMechanicStandardServices(@Path("id") Long mechanicId);

}
