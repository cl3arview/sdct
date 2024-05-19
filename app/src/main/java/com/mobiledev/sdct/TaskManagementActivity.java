package com.mobiledev.sdct;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mobiledev.sdct.adapters.TaskAdapter;
import com.mobiledev.sdct.models.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TaskManagementActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener {

    private static final String TAG = "TaskManagementActivity";

    private EditText editTextTask;
    private Button buttonAddTask;
    private Button buttonToggleTasks;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private TextView weatherInfo;

    private FirebaseFirestore db;
    private CollectionReference tasksRef;
    private FirebaseAuth mAuth;

    private OkHttpClient client;
    private ExecutorService executorService;

    private static final String API_KEY = BuildConfig.WEATHER_API_KEY;
    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Paris&units=metric&appid=" + API_KEY;

    private boolean viewingCompletedTasks = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_management);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        tasksRef = db.collection("tasks");

        // Setup views
        editTextTask = findViewById(R.id.editTextTask);
        buttonAddTask = findViewById(R.id.buttonAddTask);
        buttonToggleTasks = findViewById(R.id.buttonToggleTasks);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        weatherInfo = findViewById(R.id.weatherInfo);

        // Setup RecyclerView
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this, viewingCompletedTasks);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        // Load tasks
        loadTasks(false);  // Load active tasks

        // Add task button click listener
        buttonAddTask.setOnClickListener(view -> addTask());

        // Toggle tasks button click listener
        buttonToggleTasks.setOnClickListener(view -> toggleTasks());

        // Initialize OkHttpClient and ExecutorService
        client = new OkHttpClient();
        executorService = Executors.newSingleThreadExecutor();

        // Load weather information
        loadWeatherInfo();
    }

    private void loadTasks(boolean completed) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        taskList.clear();
        tasksRef.whereEqualTo("userId", currentUser.getUid())
                .whereEqualTo("completed", completed)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error loading tasks: ", e);
                        Toast.makeText(TaskManagementActivity.this, "Error loading tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        Task task = dc.getDocument().toObject(Task.class);
                        task.setId(dc.getDocument().getId());

                        switch (dc.getType()) {
                            case ADDED:
                                taskList.add(task);
                                taskAdapter.notifyItemInserted(taskList.size() - 1);
                                break;
                            case REMOVED:
                                taskList.remove(task);
                                taskAdapter.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                int index = taskList.indexOf(task);
                                if (index != -1) {
                                    taskList.set(index, task);
                                    taskAdapter.notifyItemChanged(index);
                                }
                                break;
                        }
                    }

                    recyclerViewTasks.scrollToPosition(taskList.size() - 1);
                });
    }

    private void addTask() {
        String taskText = editTextTask.getText().toString().trim();
        if (TextUtils.isEmpty(taskText) || taskText.contains("\n")) {
            Toast.makeText(this, "Task cannot be empty or contain line breaks", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(taskText, System.currentTimeMillis(), false, currentUser.getUid());

        tasksRef.add(task)
                .addOnSuccessListener(documentReference -> {
                    editTextTask.setText("");  // Clear the text box
                    Toast.makeText(TaskManagementActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error adding task: ", e);
                    Toast.makeText(TaskManagementActivity.this, "Error adding task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleTasks() {
        viewingCompletedTasks = !viewingCompletedTasks;
        if (viewingCompletedTasks) {
            buttonToggleTasks.setText("View Active Tasks");
            taskAdapter = new TaskAdapter(taskList, this, true);
            recyclerViewTasks.setAdapter(taskAdapter);
            loadTasks(true);  // Load completed tasks
        } else {
            buttonToggleTasks.setText("View Completed Tasks");
            taskAdapter = new TaskAdapter(taskList, this, false);
            recyclerViewTasks.setAdapter(taskAdapter);
            loadTasks(false);  // Load active tasks
        }
    }

    private void loadWeatherInfo() {
        Request request = new Request.Builder()
                .url(WEATHER_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error fetching weather information: ", e);
                runOnUiThread(() -> weatherInfo.setText("Failed to fetch weather information"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);
                    String weatherDescription = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").getAsString();
                    String temperature = jsonObject.getAsJsonObject("main").get("temp").getAsString();
                    String weatherText = "Weather: " + weatherDescription + ", " + temperature + "°C";
                    runOnUiThread(() -> weatherInfo.setText(weatherText));
                } else {
                    Log.e(TAG, "Error response from weather API: " + response.message());
                    runOnUiThread(() -> weatherInfo.setText("Failed to fetch weather information"));
                }
            }
        });
    }

    @Override
    public void onCompleteTask(Task task) {
        task.setCompleted(true);
        tasksRef.document(task.getId()).set(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task marked as completed"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating task: ", e));
    }

    @Override
    public void onDeleteTask(Task task) {
        tasksRef.document(task.getId()).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task deleted"))
                .addOnFailureListener(e -> Log.e(TAG, "Error deleting task: ", e));
    }

    @Override
    public void onUndoTask(Task task) {
        task.setCompleted(false);
        tasksRef.document(task.getId()).set(task)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Task marked as active"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating task: ", e));
    }
}
