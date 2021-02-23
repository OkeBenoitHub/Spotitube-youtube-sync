package com.syncrotube.www.utils;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.syncrotube.www.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

/**
 * Photo util :: contain every recurring task dealing with Photo
 */
final public class PhotoUtil {
    private static String mPhotoFilePath;

    /**
     * Create a new file
     * @return :: nothing
     */
    public static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mPhotoFilePath = image.getAbsolutePath();
        return image;
    }

    public static String getPhotoFilePath() {
        return mPhotoFilePath;
    }

    /**
     * Pick existing photo from phone gallery
     */
    public static Intent pickPhotoFromGallery(Context context) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent chooserIntent = Intent.createChooser(getIntent, context.getString(R.string.choose_photo_from_text));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        return chooserIntent;
    }

    /**
     * Add photo to phone gallery
     * @param context :: context
     * @param photoUri :: photo Uri
     */
    public static void addPhotoToPhoneGallery(Context context, Uri photoUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * Capture photo from camera
     */
    public static Intent capturePhoto(Context context) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(context);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.syncrotube.www.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                return takePictureIntent;
            }
        }
        return null;
    }

    /**
     * Crop photo after taking it from gallery or camera
     */
    public static void cropPhoto(Context context, String currentPhotoPath, Fragment fragment) {
        if (currentPhotoPath != null) {
            File f = new File(currentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            // start cropping activity for pre-acquired image saved on the device
            if (fragment != null) {
                CropImage.activity(contentUri)
                        .start(context, fragment);
            }
        }
    }

    /**
     * load photo file with Glide
     * @param context :: context
     * @param photoFilePath :: photo file path
     * @param profilePhotoHolder :: photo image holder
     * @param errorDrawableImgFailed :: error drawable image failed
     */
    public static void loadPhotoFileWithGlide(Context context, String photoFilePath, ImageView profilePhotoHolder, int errorDrawableImgFailed) {
        Glide.with(context)
                .load(photoFilePath)
                .error(errorDrawableImgFailed)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(new ColorDrawable(Color.GRAY))
                .into(profilePhotoHolder);
    }

    /**
     * Photo editor view
     * @param context :: context
     * @param photoIntentUri :: Uri of the photo file
     * @param photoEditorView :: photo editor view
     * @return :: Photo editor object
     */
    public static PhotoEditor photoEditorView(Context context, Uri photoIntentUri, PhotoEditorView photoEditorView) {
        if (photoEditorView != null) {
            photoEditorView.getSource().setAdjustViewBounds(true);
            photoEditorView.getSource().setScaleType(ImageView.ScaleType.FIT_CENTER);
            photoEditorView.getSource().setImageURI(photoIntentUri);
            // Photo editor
            return new PhotoEditor.Builder(context, photoEditorView)
                    .setPinchTextScalable(true)
                    .build();
        }
        return null;
    }

    public interface MyCallback {
        void onSavedPhotoFile(boolean isSuccessful);
    }

    /**
     * save photo file to user device
     * @param context :: context
     * @param photoEditor :: photo editor
     * @param myCallback :: call back method
     */
    public static void savePhotoFile(Context context, PhotoEditor photoEditor, MyCallback myCallback) {
        if (photoEditor != null) {
            try {
                createImageFile(context);
                // check for write permission granted
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    photoEditor.saveAsFile(mPhotoFilePath, new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath) {
                            myCallback.onSavedPhotoFile(true);
                        }

                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            myCallback.onSavedPhotoFile(false);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
