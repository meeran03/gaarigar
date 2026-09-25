package com.gianteyes.gaarigarmechanic.screens.home_screens;

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
import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;
import com.gianteyes.gaarigarmechanic.services.APIClient;
import com.gianteyes.gaarigarmechanic.services.Api;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

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
        final int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            this.requestPermissions(
                    UpdateProfile.PERMISSIONS_STORAGE,
                    UpdateProfile.REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        final Gson gson = new Gson();
        this.user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.image = view.findViewById(R.id.user_profile_image);
        this.firstName = view.findViewById(R.id.firstNameInputField);
        this.lastName = view.findViewById(R.id.lastNameInputField);
        this.password = view.findViewById(R.id.passwordInputField);
        this.firstName.setText(this.user.getFirstName());
        this.lastName.setText(this.user.getLastName());
        // set the default image for the user
        if (this.user.getImage() != null) {
            Glide.with(this).load(this.user.getImage()).into(this.image);
        }
        this.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                UpdateProfile.this.verifyStoragePermissions();
                final Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), UpdateProfile.PICK_IMAGE_REQUEST);
            }
        });
        view.findViewById(R.id.saveProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                updateProfile();
            }
        });
    }

    // Method to get the absolute path of the selected image from its URI
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UpdateProfile.PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                this.selectedImage = data.getData();                                                         // Get the image file URI
                final String[] filePathColumn = {MediaStore.MediaColumns.DATA};
                final Cursor cursor = getActivity().getContentResolver().query(this.selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                this.part_image = cursor.getString(columnIndex);                                             // Get the absolute path of the image file
                cursor.close();
                Glide.with(this).load(this.selectedImage).into(this.image);                                       // Set the image in the ImageView
            }
        }
    }

    // Upload the image to the remote database
    public void updateProfile() {
        final String firstName = this.firstName.getText().toString();
        final String lastName = this.lastName.getText().toString();
        final String password = this.password.getText().toString();
        if (firstName == null || firstName.isEmpty()) {
            Toast.makeText(getContext(), "First name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastName == null || lastName.isEmpty()) {
            Toast.makeText(getContext(), "Last name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        HashMap<String, RequestBody> map = new HashMap<>();
        if (!firstName.equals(user.getFirstName())) {
            map.put("firstName", RequestBody.create(MediaType.parse("text/plain"), firstName));
        }
        if (!lastName.equals(user.getLastName())) {
            map.put("lastName", RequestBody.create(MediaType.parse("text/plain"), lastName));
        }
        if (!password.isEmpty()) {
            map.put("password", RequestBody.create(MediaType.parse("text/plain"), password));
        }
        final File imageFile = new File(this.part_image);                                                          // Create a file using the absolute path of the image
        final RequestBody reqBody = RequestBody.create(MediaType.parse("image/*"), imageFile);                 // Create a request body using the image file
        final MultipartBody.Part partImage = MultipartBody.Part.createFormData("image", imageFile.getName(), reqBody);
        Api api = APIClient.getInstance().getMyApi();
        final Call<ResponseBody> upload = api.updateCustomer(user.getId(), map, partImage);
        upload.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(final Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateProfile.this.getActivity(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(final Call<ResponseBody> call, final Throwable t) {
                Toast.makeText(getActivity(), "Request failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
