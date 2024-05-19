package com.mobiledev.sdct;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobiledev.sdct.adapters.DocumentAdapter;
import com.mobiledev.sdct.models.Document;

import java.util.ArrayList;
import java.util.List;

public class DocumentManagementActivity extends AppCompatActivity {

    private static final String TAG = "DocumentManagement";
    private static final int PICK_DOCUMENT_REQUEST = 1;

    private Button buttonUploadDocument;
    private RecyclerView recyclerViewDocuments;
    private DocumentAdapter documentAdapter;
    private List<Document> documentList;

    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_management);

        buttonUploadDocument = findViewById(R.id.buttonUploadDocument);
        recyclerViewDocuments = findViewById(R.id.recyclerViewDocuments);

        storage = FirebaseStorage.getInstance();
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        documentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentList);
        recyclerViewDocuments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDocuments.setAdapter(documentAdapter);

        buttonUploadDocument.setOnClickListener(view -> openFileChooser());

        loadDocuments();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DOCUMENT_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri documentUri = data.getData();
            uploadDocument(documentUri);
        }
    }

    private void uploadDocument(Uri documentUri) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageReference = storage.getReference("documents/" + System.currentTimeMillis());
        storageReference.putFile(documentUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String documentUrl = uri.toString();
                    String userId = currentUser.getUid();
                    String documentName = documentUri.getLastPathSegment();

                    Document document = new Document(documentName, documentUrl, userId);
                    db.collection("documents").add(document)
                            .addOnSuccessListener(documentReference -> {
                                documentList.add(document);
                                documentAdapter.notifyItemInserted(documentList.size() - 1);
                                Toast.makeText(DocumentManagementActivity.this, "Document uploaded", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error adding document to Firestore", e));
                }))
                .addOnFailureListener(e -> Log.e(TAG, "Error uploading document", e));
    }

    private void loadDocuments() {
        db.collection("documents")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot documents = task.getResult();
                        for (QueryDocumentSnapshot document : documents) {
                            Document doc = document.toObject(Document.class);
                            documentList.add(doc);
                        }
                        documentAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "Error getting documents", task.getException());
                    }
                });
    }
}
