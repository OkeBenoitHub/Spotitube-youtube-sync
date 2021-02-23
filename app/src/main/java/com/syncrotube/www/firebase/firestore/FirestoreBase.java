package com.syncrotube.www.firebase.firestore;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Firebase Firestore :: contain every recurring task dealing with Firestore database
 */
final public class FirestoreBase {

    // interface for following method
    public interface addDocumentByIdToCollectionCallback {
        void onAddDocumentByIdStatus(boolean isSuccessful, String errorMessage);
    }
    /**
     * Add document to collection by id
     * @param collectionName :: collection name
     * @param documentId :: document ID
     * @param objectData :: object data
     */
    public static void addDocumentByIdToCollection(String collectionName, String documentId, Object objectData, addDocumentByIdToCollectionCallback addDocumentByIdToCollectionCallback) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName).document(documentId)
                .set(objectData)
                .addOnSuccessListener(aVoid -> addDocumentByIdToCollectionCallback.onAddDocumentByIdStatus(true, null))
                .addOnFailureListener(e -> addDocumentByIdToCollectionCallback.onAddDocumentByIdStatus(false,e.getMessage()));

    }

    // interface for following method
    public interface pushDocumentWithoutIdToCollectionCallback {
        void onPushDocumentWithoutIdStatus(boolean isSuccessful, String documentRefId, String errorMessage);
    }
    /**
     * Push document by id to collection
     * @param collectionName :: collection name
     * @param objectData :: object data
     */
    public static void pushDocumentWithoutIdToCollection(String collectionName, Object objectData, pushDocumentWithoutIdToCollectionCallback pushDocumentWithoutIdToCollectionCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collectionName)
                .add(objectData)
                .addOnSuccessListener(documentReference -> pushDocumentWithoutIdToCollectionCallback.onPushDocumentWithoutIdStatus(true,documentReference.getId(),null))
                .addOnFailureListener(e -> pushDocumentWithoutIdToCollectionCallback.onPushDocumentWithoutIdStatus(false,null, e.getMessage()));

    }

    // interface for following method
    public interface updateDocumentByIdFromCollectionCallback {
        void onUpdateDocumentByIdStatus(boolean isSuccessful, String errorMessage);
    }
    /**
     * Update document by id from collection
     * @param collectionName :: collection name
     * @param documentId :: document ID
     * @param updatedDocFieldsData :: new updated fields doc data
     * Map<String, Object> updatedDocFieldsData = new HashMap<>();
     * updatedDocFieldsData.put("field", "new data");
     * update more fields ...
     */
    public static void updateDocumentByIdFromCollection(String collectionName, String documentId, Map<String, Object> updatedDocFieldsData, updateDocumentByIdFromCollectionCallback updateDocumentByIdFromCollectionCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(collectionName).document(documentId);
        if (updatedDocFieldsData != null && updatedDocFieldsData.size() > 0) {
            documentReference
                    .update(updatedDocFieldsData)
                    .addOnSuccessListener(aVoid -> updateDocumentByIdFromCollectionCallback.onUpdateDocumentByIdStatus(true, null))
                    .addOnFailureListener(e -> updateDocumentByIdFromCollectionCallback.onUpdateDocumentByIdStatus(false, e.getMessage()));
        }
    }

    // interface for following method
    public interface getAllDocumentsFromCollectionCallback {
        void onGetAllDocumentsStatus(boolean isSuccessful,List<QueryDocumentSnapshot> documentList, String errorMessage);
    }
    /**
     * Get all documents from collection
     * @param collectionName :: collection name
     */
    public static ListenerRegistration getAllDocumentsFromCollection(String collectionName,boolean withLiveUpdates, getAllDocumentsFromCollectionCallback getAllDocumentsFromCollectionCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (!withLiveUpdates) {
            // get all documents once
            db.collection(collectionName)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<QueryDocumentSnapshot> objectList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // make sure document exists
                                if (document.exists()) {
                                    objectList.add(document);
                                }
                            }
                            getAllDocumentsFromCollectionCallback.onGetAllDocumentsStatus(true, objectList, null);
                        } else {
                            getAllDocumentsFromCollectionCallback.onGetAllDocumentsStatus(false, null, null);
                        }
                    });
            return null;
        } else {
            // with live updates
            return db.collection(collectionName)
                    .addSnapshotListener((value, e) -> {
                        if (e != null) {
                            getAllDocumentsFromCollectionCallback.onGetAllDocumentsStatus(false,null,null);
                            return;
                        }

                        List<QueryDocumentSnapshot> objects = new ArrayList<>();
                        if (value != null) {
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.exists()) {
                                    objects.add(doc);
                                }
                            }
                        }
                        getAllDocumentsFromCollectionCallback.onGetAllDocumentsStatus(true,objects,null);
                    });
        }
    }

    // interface for following method
    public interface getDocumentByIdFromCollectionCallback {
        void onGetDocumentByIdStatus(boolean isSuccessful,DocumentSnapshot documentObject, String errorMessage);
    }
    /**
     * Get document by ID from collection
     * @param collectionName :: collection name
     * @param documentId :: document id
     */
    public static ListenerRegistration getDocumentByIdFromCollection(String collectionName, String documentId, boolean withLiveUpdates, getDocumentByIdFromCollectionCallback getDocumentByIdFromCollectionCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(collectionName).document(documentId);
        if (!withLiveUpdates) {
            // get document once
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        getDocumentByIdFromCollectionCallback.onGetDocumentByIdStatus(true,document,null);
                    } else {
                        getDocumentByIdFromCollectionCallback.onGetDocumentByIdStatus(false, null, null);
                    }
                } else {
                    getDocumentByIdFromCollectionCallback.onGetDocumentByIdStatus(false, null, null);
                }
            });
            return null;
        } else {
            // with live updates
            return docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    // Listen failed
                    getDocumentByIdFromCollectionCallback.onGetDocumentByIdStatus(false,null,e.getMessage());
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    getDocumentByIdFromCollectionCallback.onGetDocumentByIdStatus(true,snapshot,null);
                } else {
                    getDocumentByIdFromCollectionCallback.onGetDocumentByIdStatus(false,null,null);
                }
            });
        }
    }

    /**
     * Detach live updates to keep firing from document(s) reference
     * @param registration :: registration source
     */
    public static void detachLiveUpdatesListenerFromDocumentRef(ListenerRegistration registration) {
        registration.remove();
    }

    // interface for following method
    public interface deleteDocumentByIdFromCollectionCallback {
        void onDeleteDocumentByIdStatus(boolean isSuccessful, String errorMessage);
    }
    /**
     * Delete document by ID from collection
     * @param collectionName :: collection name
     * @param documentId :: document id
     */
    public static void deleteDocumentByIdFromCollection(String collectionName, String documentId, deleteDocumentByIdFromCollectionCallback deleteDocumentByIdFromCollectionCallback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(collectionName).document(documentId);
        docRef.delete()
                .addOnSuccessListener(aVoid -> deleteDocumentByIdFromCollectionCallback.onDeleteDocumentByIdStatus(true,null))
                .addOnFailureListener(e -> deleteDocumentByIdFromCollectionCallback.onDeleteDocumentByIdStatus(false,e.getMessage()));
    }
}
