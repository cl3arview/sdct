package com.mobiledev.sdct;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mobiledev.sdct.adapters.MessageAdapter;
import com.mobiledev.sdct.models.Message;

import java.util.ArrayList;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {

    private static final String TAG = "MessagingActivity";

    private EditText editTextMessage;
    private Button buttonSendMessage;
    private RecyclerView recyclerViewMessages;
    private MessageAdapter messagesAdapter;
    private List<Message> messageList;

    private FirebaseFirestore db;
    private CollectionReference conversationsRef;
    private FirebaseAuth mAuth;

    private String userId;
    private String userName;
    private String conversationId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Get user ID and name from intent
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        conversationsRef = db.collection("conversations");

        // Setup views
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSendMessage = findViewById(R.id.buttonSendMessage);
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        // Setup RecyclerView
        messageList = new ArrayList<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            messagesAdapter = new MessageAdapter(messageList, currentUser.getUid());
        }
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messagesAdapter);

        // Generate conversation ID based on user IDs
        if (currentUser != null && userId != null) {
            conversationId = generateConversationId(currentUser.getUid(), userId);

            // Load messages
            loadMessages();

            // Send message button click listener
            buttonSendMessage.setOnClickListener(view -> sendMessage());
        }
    }

    private void loadMessages() {
        conversationsRef.document(conversationId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error loading messages: ", e);
                        Toast.makeText(MessagingActivity.this, "Error loading messages: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Message message = dc.getDocument().toObject(Message.class);
                        switch (dc.getType()) {
                            case ADDED:
                                messagesAdapter.addMessage(message);
                                break;
                            case MODIFIED:
                                // Handle modified message if needed
                                break;
                            case REMOVED:
                                // Handle removed message if needed
                                break;
                        }
                    }
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                });
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String senderId = currentUser.getUid();
        long timestamp = System.currentTimeMillis();

        Message message = new Message(senderId, messageText, timestamp, currentUser.getDisplayName());

        db.collection("conversations").document(conversationId)
                .collection("messages").add(message)
                .addOnSuccessListener(documentReference -> {
                    editTextMessage.setText("");  // Clear the text box
                    Toast.makeText(MessagingActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error sending message: ", e);
                    Toast.makeText(MessagingActivity.this, "Error sending message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String generateConversationId(String userId1, String userId2) {
        return userId1.compareTo(userId2) < 0 ? userId1 + "_" + userId2 : userId2 + "_" + userId1;
    }
}
