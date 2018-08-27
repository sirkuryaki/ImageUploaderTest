package com.yopdev.imageuploadertest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yopdev.imageuploadertest.util.Status;
import com.yopdev.imageuploadertest.util.WSResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UploaderActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_PHOTO = 2;

    // UI references.
    private EditText mUrlView;
    private EditText mAppTokenView;
    private EditText mUserTokenView;
    private View mProgressView;
    private View mLoginFormView;
    private ImageView mUserAvatar;
    private TextView mOutput;

    private UploaderViewModel viewModel;


    private final Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            FileOutputStream out = null;
            try {
                File fileOut = createImageFile(getApplicationContext());
                viewModel.setPhotoPath(fileOut.getAbsolutePath());

                out = new FileOutputStream(fileOut);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            } catch (Exception ignored) {
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ignored) {
                }
            }
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
    };

    @SuppressLint("SimpleDateFormat")
    public File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    @Nullable
    public String getPath(@NonNull Context context, @NonNull Uri photoUri) {

        File newFile = null;
        InputStream pictureInputStream;
        try {
            pictureInputStream = context.getContentResolver().openInputStream(photoUri);
        } catch (FileNotFoundException e) {
            pictureInputStream = null;
        }

        if (pictureInputStream == null) {
            return null;
        }

        OutputStream out = null;
        try {
            newFile = createImageFile(context);
            out = new FileOutputStream(newFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = pictureInputStream.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }

        }

        try {
            pictureInputStream.close();
        } catch (IOException ignored) {
        }

        return newFile == null ? null : newFile.getAbsolutePath();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploader);

        viewModel = ViewModelProviders.of(this).get(UploaderViewModel.class);

        mUserAvatar = findViewById(R.id.image_avatar);

        mUrlView = findViewById(R.id.url);
        mAppTokenView = findViewById(R.id.app_token);
        mUserTokenView = findViewById(R.id.user_token);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mOutput = findViewById(R.id.output);


        mUrlView.setText(BuildConfig.BASE_URL);
        mAppTokenView.setText(BuildConfig.APP_TOKEN);
        mUserTokenView.setText(BuildConfig.USER_TOKEN);
    }

    public void OnClickChangePhoto(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(UploaderActivity.this);
        builder.setTitle(R.string.uploader_activity_choose_image);
        builder.setPositiveButton(R.string.uploader_activity_camera, (dialog, which) -> selectFromCamera());
        builder.setNegativeButton(R.string.uploader_activity_photos, (dialog, which) -> selectFromPhotos());
        builder.show();
    }

    private void selectFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File file;
            try {
                file = createImageFile(this);
            } catch (IOException e) {
                file = null;
            }

            if (file != null) {

                viewModel.setPhotoPath(file.getAbsolutePath());

                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".fileprovider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void selectFromPhotos() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_PICK_PHOTO);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Picasso picasso = Picasso.with(this);
            if (requestCode == REQUEST_TAKE_PHOTO) {
                @SuppressWarnings("ConstantConditions") Uri uri = Uri.fromFile(new File(viewModel.getPhotoPath()));
                picasso.load(uri).resize(400, 400).into(mUserAvatar);
                resizeImage(uri);
            } else if (requestCode == REQUEST_PICK_PHOTO) {
                Uri uri = data.getData();
                if (uri != null) {

                    viewModel.setPhotoPath(getPath(this, uri));
                    picasso.load(uri).resize(400, 400).into(mUserAvatar);
                    resizeImage(uri);
                }
            }
        }
    }

    private void resizeImage(Uri uri) {
        Picasso.with(this).load(uri).resize(800, 800).into(mTarget);
    }

    public void onClickUpload(View v) {

        String url = mUrlView.getText().toString();
        String accessToken = mAppTokenView.getText().toString();
        String clientToken = mUserTokenView.getText().toString();

        LiveData<WSResource<String>> resourceLiveData = viewModel.uploadImage(url, accessToken, clientToken);


        if (resourceLiveData == null) {
            mOutput.setText(R.string.uploader_activity_no_photo_selected);
            return;
        }

        resourceLiveData.observe(this, r -> {

            if (r == null) {
                return;
            }
            showProgress(r.status == Status.LOADING);
            if (r.status != Status.LOADING) {

                String message = r.data != null ? r.data : r.errorMessage;

                mOutput.setText(getString(R.string.uploader_activity_request_output, message));
            }
        });
    }
}

