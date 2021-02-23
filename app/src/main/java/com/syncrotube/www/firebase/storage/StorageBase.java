package com.syncrotube.www.firebase.storage;

import android.content.Context;
import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;

/**
 * Firebase Storage :: contain every recurring task dealing with Firebase storage
 */
final public class StorageBase {
    private static StorageReference getStorageReference() {
        return FirebaseStorage.getInstance().getReference();
    }

    // interface that will check for upload file status
    public interface uploadFileToStorageReferenceCallback {
        void onUploadFileToStorageStatus(boolean isSuccessful, Uri uploadedFileUri);
    }

    /**
     * Upload specific file to Firebase Storage
     * @param filePath :: file path
     * @param rootDirectoryName :: root directory name :: eg userId/photos/...
     * @param fileExt :: file extension :: eg image/jpg
     */
    public static void uploadFileToStorageReference(String filePath, String rootDirectoryName, String fileExt, uploadFileToStorageReferenceCallback uploadFileToStorageReferenceCallback) {
        Uri fileUri = Uri.fromFile(new File(filePath));
        // rootDirectoryName :: eg userId/photos/...
        // fileExt :: eg image/jpg
        StorageReference filePathRef = getStorageReference().child(rootDirectoryName + fileUri.getLastPathSegment());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(fileExt)
                .build();
        UploadTask uploadTask = filePathRef.putFile(fileUri, metadata);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                uploadFileToStorageReferenceCallback.onUploadFileToStorageStatus(false, null);
                throw Objects.requireNonNull(task.getException());
            }

            // Continue with the task to get the download URL
            return filePathRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                uploadFileToStorageReferenceCallback.onUploadFileToStorageStatus(true, downloadUri);
            }
        });
    }
}
