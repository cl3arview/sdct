package com.mobiledev.sdct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.mobiledev.sdct.adapters.UserListAdapter;
import com.mobiledev.sdct.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity implements UserListAdapter.OnUserClickListener {

    private static final String TAG = "UserListActivity";

    private RecyclerView recyclerViewUsers;
    private UserListAdapter userListAdapter;
    private List<User> userList;

    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private FirebaseAuth mAuth;
    private String currentUserId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        userList = new ArrayList<>();
        userListAdapter = new UserListAdapter(userList, this);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userListAdapter);

        loadUsers();
    }

    private void loadUsers() {
        usersRef.orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error loading users: ", e);
                        Toast.makeText(UserListActivity.this, "Error loading users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    userList.clear();
                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        User user = dc.getDocument().toObject(User.class);
                        if (user.getUid() != null && !user.getUid().equals(currentUserId)) {
                            switch (dc.getType()) {
                                case ADDED:
                                    userList.add(user);
                                    break;
                                case MODIFIED:
                                    // Handle modified user if needed
                                    break;
                                case REMOVED:
                                    userList.remove(user);
                                    break;
                            }
                        }
                    }
                    userListAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(this, MessagingActivity.class);
        intent.putExtra("userId", user.getUid());
        intent.putExtra("userName", user.getName());
        startActivity(intent);
    }
}
