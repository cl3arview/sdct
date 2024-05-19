package com.mobiledev.sdct.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mobiledev.sdct.R;
import com.mobiledev.sdct.models.Document;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private List<Document> documentList;

    public DocumentAdapter(List<Document> documentList) {
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        Document document = documentList.get(position);
        holder.textViewDocumentName.setText(document.getName());
        holder.itemView.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(document.getUrl()));
            context.startActivity(intent);
        });

        holder.buttonDeleteDocument.setOnClickListener(v -> deleteDocument(document));
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    private void deleteDocument(Document document) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Delete from Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl(document.getUrl());
        storageReference.delete().addOnSuccessListener(aVoid -> {
            // Delete from Firestore
            db.collection("documents").whereEqualTo("url", document.getUrl())
                    .get().addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            db.collection("documents").document(doc.getId()).delete()
                                    .addOnSuccessListener(aVoid1 -> {
                                        documentList.remove(document);
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle failure
                                    });
                        }
                    });
        }).addOnFailureListener(e -> {
            // Handle failure
        });
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDocumentName;
        Button buttonDeleteDocument;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDocumentName = itemView.findViewById(R.id.textViewDocumentName);
            buttonDeleteDocument = itemView.findViewById(R.id.buttonDeleteDocument);
        }
    }
}
