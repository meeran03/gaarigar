package com.gianteyes.gaarigarapp.services;

import com.gianteyes.gaarigarapp.models.Category;
import com.gianteyes.gaarigarapp.models.ChangePasswordOTPModel;
import com.gianteyes.gaarigarapp.models.InitiateRequestModel;
import com.gianteyes.gaarigarapp.models.InitiateRequestResponseModel;
import com.gianteyes.gaarigarapp.models.InitiateStandardServiceRequest;
import com.gianteyes.gaarigarapp.models.LoginRequestModel;
import com.gianteyes.gaarigarapp.models.LoginResponseModel;
import com.gianteyes.gaarigarapp.models.OrderModel;
import com.gianteyes.gaarigarapp.models.PaymentResponseDto;
import com.gianteyes.gaarigarapp.models.RateRequestModel;
import com.gianteyes.gaarigarapp.models.RegisterRequestModel;
import com.gianteyes.gaarigarapp.models.RegisterResponseModel;
import com.gianteyes.gaarigarapp.models.SendOTPRequestModel;
import com.gianteyes.gaarigarapp.models.SendOTPResponse;
import com.gianteyes.gaarigarapp.models.StandardServicesResponse;
import com.gianteyes.gaarigarapp.models.VerifyOTPModel;
import com.gianteyes.gaarigarapp.models.VerifyOTPResponseDTO;
import com.gianteyes.gaarigarapp.models.common.MLocation;

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
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    String BASE_URL = "https://backend-production-17213.up.railway.app/api/";

    @POST("auth/login")
    Call<LoginResponseModel> loginCustomer(@Body LoginRequestModel request);

    @POST("auth/register/customer")
    Call<RegisterResponseModel> registerCustomer(@Body RegisterRequestModel request);

    @POST("sms/send")
    Call<SendOTPResponse> sendOTP(@Body SendOTPRequestModel request);

    @POST("sms/verify")
    Call<VerifyOTPResponseDTO> verifyOTP(@Body VerifyOTPModel request);

    @POST("sms/change-password")
    Call<Void> changePassword(@Body ChangePasswordOTPModel request);

    @POST("initiate-request/create/mechanic-request/")
    Call<InitiateRequestResponseModel> initiateMechanicRequest(@Body InitiateRequestModel request);

    @POST("initiate-request/create/fuel-delivery-request/")
    Call<InitiateRequestResponseModel> initiateFuelDeliveryRequest(@Body InitiateRequestModel request);


    @POST("initiate-request/ask/mechanic/{mechanicId}/{initiatedRequestId}")
    Call<ResponseBody> askMechanic(@Path("mechanicId") Long mechanicId, @Path("initiatedRequestId") Long initiatedRequestId);

    @POST("initiate-request/ask/petrolpump/{petrolpumpId}/{initiatedRequestId}")
    Call<ResponseBody> askPetrolPump(@Path("petrolpumpId") Long mechanicId, @Path("initiatedRequestId") Long initiatedRequestId);

    // endpoints related to profile
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

    @GET("mechanic-standard-service/search")
    Call<ArrayList<StandardServicesResponse>> searchStandardServices(
            @Query("q") String mechanicId,
            @Query("category") Long categoryId,
            @Query(("max-price")) Long maxPrice,
            @Query(("min-price")) Long minPrice
    );


    @POST("rate/")
    Call<Void> rateProvider(@Body RateRequestModel ratingModel);

    @POST("standard-order/create")
    Call<Void> createStandardOrder(@Body InitiateStandardServiceRequest request);

    @POST("customer/update-location")
    Call<Void> updateLocation(@Body MLocation location);

    @PUT("order/cancel/{orderId}")
    Call<Void> cancelOrder(@Path("orderId") Long orderId);

}
