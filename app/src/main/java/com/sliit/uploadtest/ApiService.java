package com.sliit.uploadtest;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("image")
    Call<AddImage> addimage(@Part MultipartBody.Part image);

    @Multipart
    @POST("video")
    Call<AddVideo> addVideo(@Part MultipartBody.Part video);
}
