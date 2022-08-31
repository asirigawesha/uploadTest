package com.sliit.uploadtest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.sliit.uploadtest.databinding.ActivityMainBinding;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ActivityResultLauncher<Intent> imageSelectionLauncher;
    ActivityResultLauncher<Intent> videoSelectionLauncher;
    String imagePath;
    String videoPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        imageSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        imagePath =data.getData().getPath().split(":")[1];
                        Log.d("path", imagePath);
                        Bitmap bitmap= BitmapFactory.decodeFile(imagePath);
                        binding.imageView.setImageBitmap(bitmap);

                    }
                });

        videoSelectionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        videoPath = generatePath(data.getData(), MainActivity.this);
                        if(videoPath==null){
                            videoPath=data.getData().getPath().split(":")[1];
                            Log.i("path null", videoPath);
                        }
                        Log.i("path", videoPath);
                        //videoPath =data.getData().getPath().split(":")[1];
                       // Log.d("path", String.valueOf(data.getData()));


                    }
                });
        clickListeners();
        
    }


    private void clickListeners() {

        binding.buttonSelectImage.setOnClickListener(V->{
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Intent intent =new Intent();
                intent.setDataAndType( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                imageSelectionLauncher.launch(intent);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });

        binding.buttonSelectVideo.setOnClickListener(V->{
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                Intent intent =new Intent();
                intent.setDataAndType( MediaStore.Video.Media.EXTERNAL_CONTENT_URI,"video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                videoSelectionLauncher.launch(intent);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        });

        binding.buttonUpload.setOnClickListener(V->{
            uploadImage();
        });

        binding.buttonUploadVideo.setOnClickListener(V->{
            uploadVideo();
        });

    }


    public void uploadImage(){
        Retrofit retrofit =new Retrofit.Builder().baseUrl("http://192.168.1.6:8000/").
                addConverterFactory(GsonConverterFactory.create()).build();
        File file=new File(imagePath);
        RequestBody requestFIle=RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body=MultipartBody.Part.createFormData("file_name",file.getName(),requestFIle);

        ApiService apiService=retrofit.create(ApiService.class);
        Call<AddImage>call=apiService.addimage(body);

        call.enqueue(new Callback<AddImage>() {
            @Override
            public void onResponse(Call<AddImage> call, Response<AddImage> response) {
                if(response.isSuccessful()){
                    Log.d("ss",response.body().getFileName());
                }
            }

            @Override
            public void onFailure(Call<AddImage> call, Throwable t) {
                Log.d("ss",t.getMessage());
            }
        });
    }

    public void uploadVideo(){
        Retrofit retrofit =new Retrofit.Builder().baseUrl("http://192.168.1.6:8000/").
                addConverterFactory(GsonConverterFactory.create()).build();
        File file=new File(videoPath);
        RequestBody requestFIle=RequestBody.create(MediaType.parse("multipart/form-data"),file);
        MultipartBody.Part body=MultipartBody.Part.createFormData("file_name",file.getName(),requestFIle);

        ApiService apiService=retrofit.create(ApiService.class);
        Call<AddVideo>callVideo=apiService.addVideo(body);

        callVideo.enqueue(new Callback<AddVideo>() {
            @Override
            public void onResponse(Call<AddVideo> callVideo, Response<AddVideo> response) {
                if(response.isSuccessful()){
                    Log.d("ss",response.body().getFileName());
                }
            }

            @Override
            public void onFailure(Call<AddVideo> callVideo, Throwable t) {
                Log.d("ss",t.getMessage());
            }
        });
    }

    public String generatePath(Uri uri, Context context) {

        String filePath = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Path filePathd=Paths.get(uri.getPath());
//            filePath=filePathd.toString().split(":")[1];
//        }
        String fileId = DocumentsContract.getDocumentId(uri);
        // Split at colon, use second item in the array
        String id = fileId.split(":")[1];
        String[] column = {MediaStore.Video.Media.DATA};
        String selector = MediaStore.Video.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                column, selector, new String[]{id}, null);
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
            Log.i("inpath", filePath);
        }
        cursor.close();
        if(filePath==null){
            return uri.getPath().split(":")[1];
        }
        else{
            return filePath;
        }
    }


}