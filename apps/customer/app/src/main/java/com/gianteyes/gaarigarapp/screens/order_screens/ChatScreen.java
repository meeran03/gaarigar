package com.gianteyes.gaarigarapp.screens.order_screens;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gianteyes.gaarigarapp.R;
import com.gianteyes.gaarigarapp.adapters.ChatScreenAdapter;
import com.gianteyes.gaarigarapp.models.ChatMessage;
import com.gianteyes.gaarigarapp.models.LoginResponseModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public class ChatScreen extends Fragment {
    RecyclerView chatsRecyclerView;
    RecyclerView.Adapter adapter;
    String orderId;
    LoginResponseModel user;
    String CHAT_SOCKET_URI = "wss://backend-production-17213.up.railway.app/chat";
    ArrayList<ChatMessage> messages;
    private StompClient mStompClient;

    public ChatScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, CHAT_SOCKET_URI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_screen, container, false);
    }


    @SuppressLint("CheckResult")
    public void sendJoinMessage() {
        ChatMessage message = new ChatMessage();
        message.setChannelId(orderId);
        message.setType(ChatMessage.MessageType.JOIN);
        message.setSender(user.getFirstName());
        message.setContent("Joined the chat");
        // convert message to json
        Gson gson = new Gson();
        String json = gson.toJson(message);
        mStompClient.send("/app/chat/" + orderId + "/addSubscriber", json).subscribe(
                () -> Log.d("message", "Message sent!"),
                throwable -> Log.e("message", "Error sending message", throwable)
        );
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.orderId = getArguments().getString("orderId");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.gianteyes.gaarigarapp", Context.MODE_PRIVATE);
        String authToken = sharedPreferences.getString("accessToken", "");
        final Gson gson = new Gson();
        this.user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
        this.chatsRecyclerView = view.findViewById(R.id.chatRecyclerView);
        this.adapter = new ChatScreenAdapter(this.getContext(), new ArrayList<>(), this.user);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        this.chatsRecyclerView.setLayoutManager(linearLayoutManager);
        this.chatsRecyclerView.setAdapter(adapter);
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("X-Authorization", authToken));

        mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    mStompClient.topic("/channel/" + orderId).subscribe(message -> {
                                onMessageReceived(message);
                            },
                            throwable -> {
                                Log.e("message", "Error on subscribe topic", throwable);
                            }
                    );
                    // send join message
                    sendJoinMessage();
                    Log.d("message", "Stomp connection opened");
                    break;
                case CLOSED:
                    Log.d("message", "Stomp connection closed");
                    break;
                case ERROR:
                    Log.e("message", "Stomp connection error", lifecycleEvent.getException());
                    break;
            }
        });
        mStompClient.connect(headers);
        getView().findViewById(R.id.sendButton).setOnClickListener(v -> this.onSendButtonClick(v));
    }

    public void onSendButtonClick(final View v) {
        String message = ((android.widget.EditText) getView().findViewById(R.id.messageBox)).getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelId(orderId);
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setSender(user.getFirstName());
        chatMessage.setContent(message);
        sendMessageWithJSON(chatMessage);
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatScreen.this.adapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("CheckResult")
    void sendMessageWithJSON(final ChatMessage message) {
        final Gson gson = new Gson();
        final String json = gson.toJson(message);
        mStompClient.send("/app/chat/" + orderId + "/send", json).subscribe(
                () -> {
                    Toast.makeText(getContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                    ((android.widget.EditText) getView().findViewById(R.id.messageBox)).setText("");
                },
                throwable -> {
                    Toast.makeText(getContext(), "Error sending message", Toast.LENGTH_SHORT).show();
                    Log.e("message", "Error sending message", throwable);
                }
        );
    }

    void onMessageReceived(StompMessage message) {
        Gson gson = new Gson();
        ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
        ((ChatScreenAdapter) adapter).addMessage(chatMessage);
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChatScreen.this.adapter.notifyDataSetChanged();
            }
        });
    }

}
