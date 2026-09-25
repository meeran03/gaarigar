package com.gianteyes.gaarigarapp.screens.home_screens;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.models.LoginResponseModel;
import com.gianteyes.gaarigarapp.services.APIClient;
import com.gianteyes.gaarigarapp.services.Api;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfile extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 9544;
    // Permissions for accessing the storage
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            // android 11
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    };
    LoginResponseModel user;
    CircleImageView image;
    Uri selectedImage;
    String part_image;
    TextInputEditText firstName;
    TextInputEditText lastName;
    TextInputEditText password;

    public void verifyStoragePermissions() {
        // Check if we have write permission
        int permission = ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            requestPermissions(
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences sharedPreferences = this.getContext().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        image = view.findViewById(R.id.user_profile_image);
        firstName = view.findViewById(R.id.firstNameInputField);
        lastName = view.findViewById(R.id.lastNameInputField);
        password = view.findViewById(R.id.passwordInputField);
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        // set the default image for the user
        if (user.getImage() != null) {
            Glide.with(this).load(user.getImage()).into(image);
        }
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyStoragePermissions();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                UpdateProfile.this.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        view.findViewById(R.id.saveProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateProfile.this.updateProfile();
            }
        });
    }

    // Method to get the absolute path of the selected image from its URI
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                selectedImage = data.getData();                                                         // Get the image file URI
                String[] filePathColumn = {MediaStore.MediaColumns.DATA};
                Cursor cursor = this.getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                part_image = cursor.getString(columnIndex);                                             // Get the absolute path of the image file
                cursor.close();
                Glide.with(this).load(selectedImage).into(image);                                       // Set the image in the ImageView
            }
        }
    }

    // Upload the image to the remote database
    public void updateProfile() {
        try {
            String firstName = this.firstName.getText().toString();
            String lastName = this.lastName.getText().toString();
            String password = this.password.getText().toString();
            if (firstName == null || firstName.isEmpty()) {
                Toast.makeText(this.getContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lastName == null || lastName.isEmpty()) {
                Toast.makeText(this.getContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            final HashMap<String, RequestBody> map = new HashMap<>();
            if (!firstName.equals(this.user.getFirstName())) {
                map.put("firstName", RequestBody.create(MediaType.parse("text/plain"), firstName));
            }
            if (!lastName.equals(this.user.getLastName())) {
                map.put("lastName", RequestBody.create(MediaType.parse("text/plain"), lastName));
            }
            if (!password.isEmpty() && password.length() > 0) {
                map.put("password", RequestBody.create(MediaType.parse("text/plain"), password));
            }
            if (map.isEmpty()) {
                Toast.makeText(getActivity(), "Nothing to Update", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Gson gson = new Gson();
                final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
                String json = gson.toJson(this.user);
                sharedPreferences.edit().putString("user", json).apply();
            }
            Call<ResponseBody> upload;
            final File imageFile = new File(this.part_image);
            final Api api = APIClient.getInstance().getMyApi();
            String contentType = part_image.substring(part_image.lastIndexOf(".") + 1);
            if (contentType.equals("jpg")) {
                contentType = "jpeg";
            }
            final RequestBody reqBody = RequestBody.create(MediaType.parse("image/" + contentType), imageFile);
            MultipartBody.Part partImage = MultipartBody.Part.createFormData("image", imageFile.getName(), reqBody);
            upload = api.updateCustomer(this.user.getId(), map, partImage);
            upload.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Toast.makeText(getContext(), jObjError.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(UpdateProfile.this.getContext(), "Request failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this.getContext(), "You can't update without image.", Toast.LENGTH_SHORT).show();
        }
    }
}
