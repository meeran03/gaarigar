package com.gianteyes.gaarigarmechanic.screens.order_screens;

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

import com.gianteyes.gaarigarmechanic.R;
import com.gianteyes.gaarigarmechanic.adapters.ChatScreenAdapter;
import com.gianteyes.gaarigarmechanic.models.ChatMessage;
import com.gianteyes.gaarigarmechanic.models.LoginResponseModel;
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
    private StompClient mStompClient;

    public ChatScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, this.CHAT_SOCKET_URI);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_screen, container, false);
    }


    @SuppressLint("CheckResult")
    public void sendJoinMessage() {
        final ChatMessage message = new ChatMessage();
        message.setChannelId(this.orderId);
        message.setType(ChatMessage.MessageType.JOIN);
        message.setSender(this.user.getPhone());
        message.setContent("Joined the chat");
        // convert message to json
        final Gson gson = new Gson();
        final String json = gson.toJson(message);
        this.mStompClient.send("/app/chat/" + this.orderId + "/addSubscriber", json).subscribe(
                () -> Log.d("message", "Message sent!"),
                throwable -> Log.e("message", "Error sending message", throwable)
        );
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        orderId = this.getArguments().getString("orderId");
        final SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("com.gianteyes.gaarigarmechanic", Context.MODE_PRIVATE);
        final String authToken = sharedPreferences.getString("accessToken", "");
        Gson gson = new Gson();
        user = gson.fromJson(sharedPreferences.getString("user", ""), LoginResponseModel.class);
        chatsRecyclerView = view.findViewById(R.id.chatRecyclerView);
        adapter = new ChatScreenAdapter(getContext(), new ArrayList<>(), user);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        chatsRecyclerView.setLayoutManager(linearLayoutManager);
        chatsRecyclerView.setAdapter(this.adapter);
        final List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("X-Authorization", authToken));

        this.mStompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    this.mStompClient.topic("/channel/" + this.orderId).subscribe(message -> {
                                this.onMessageReceived(message);
                            },
                            throwable -> {
                                Log.e("message", "Error on subscribe topic", throwable);
                            }
                    );
                    // send join message
                    this.sendJoinMessage();
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
        this.mStompClient.connect(headers);
        this.getView().findViewById(R.id.sendButton).setOnClickListener(v -> onSendButtonClick(v));
    }

    public void onSendButtonClick(View v) {
        final String message = ((android.widget.EditText) this.getView().findViewById(R.id.messageBox)).getText().toString();
        if (message.isEmpty()) {
            Toast.makeText(this.getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChannelId(this.orderId);
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setSender(this.user.getPhone());
        chatMessage.setContent(message);
        this.sendMessageWithJSON(chatMessage);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @SuppressLint("CheckResult")
    void sendMessageWithJSON(ChatMessage message) {
        Gson gson = new Gson();
        String json = gson.toJson(message);
        this.mStompClient.send("/app/chat/" + this.orderId + "/send", json).subscribe(
                () -> {
                    Toast.makeText(this.getContext(), "Message sent!", Toast.LENGTH_SHORT).show();
                    ((android.widget.EditText) this.getView().findViewById(R.id.messageBox)).setText("");
                },
                throwable -> {
                    Toast.makeText(this.getContext(), "Error sending message", Toast.LENGTH_SHORT).show();
                    Log.e("message", "Error sending message", throwable);
                }
        );
    }

    void onMessageReceived(final StompMessage message) {
        final Gson gson = new Gson();
        final ChatMessage chatMessage = gson.fromJson(message.getPayload(), ChatMessage.class);
        ((ChatScreenAdapter) this.adapter).addMessage(chatMessage);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

}
