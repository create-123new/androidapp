package com.example.aidsappdetection;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aidsappdetection.api.ChatRequest;
import com.example.aidsappdetection.api.ChatResponse;
import com.example.aidsappdetection.api.ChatbotApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chatbot extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText editTextMessage;
    private ImageButton buttonSend;

    private ChatAdapter chatAdapter;
    private ChatbotApi chatbotApi;
    private ArrayList<Message> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        recyclerView = findViewById(R.id.recyclerView);
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);


        chatList = (ArrayList<Message>) loadChatHistory();

        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);
        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        chatbotApi = RetroClient.getRetrofitInstance().create(ChatbotApi.class);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = editTextMessage.getText().toString().trim();
                if (!userMessage.isEmpty()) {
                    chatList.add(new Message(userMessage, true));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    recyclerView.scrollToPosition(chatList.size() - 1);
                    sendMessageToAPI(userMessage);
                    editTextMessage.setText("");
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_clear_chat) {
            chatList.clear(); // or however you're managing chat history
            chatAdapter.notifyDataSetChanged();
            saveChatHistory(chatList);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveChatHistory(List<Message> messages) {
            SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            String email = prefs.getString("email", null);
            SharedPreferences.Editor editor = prefs.edit();

            Gson gson = new Gson();
            String json = gson.toJson(chatList);
            editor.putString(email+"chat_history", json);
            editor.apply();
    }

    private List<Message> loadChatHistory() {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String email = prefs.getString("email", null);
        String json = prefs.getString(email+"chat_history", null);

        if (json != null) {
            Type type = new TypeToken<ArrayList<Message>>(){}.getType();
            return new Gson().fromJson(json, type);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }



    private void sendMessageToAPI(String userMessage) {
        ChatRequest chatRequest = new ChatRequest(userMessage);
        Call<ChatResponse> call = chatbotApi.getChatResponse(chatRequest);

        call.enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String botMessage = response.body().getAnswer();
                    chatList.add(new Message(botMessage, false));
                    chatAdapter.notifyItemInserted(chatList.size() - 1);
                    recyclerView.scrollToPosition(chatList.size() - 1);
                    saveChatHistory(chatList);
                } else {
                    Toast.makeText(chatbot.this, "Response error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t) {
                Toast.makeText(chatbot.this, "Failed to get response: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", "Error: ", t);
            }
        });
    }
}


