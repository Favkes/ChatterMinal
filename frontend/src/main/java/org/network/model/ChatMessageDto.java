package org.network.model;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class ChatMessageDto {

    @SerializedName("id")
    private String Id;

    @SerializedName("text")
    private String Text;

    @SerializedName("sender")
    private User Sender;

    public ChatMessageDto(String id, String text, User sender) {
        Id = id;
        Text = text;
        Sender = sender;
    }

    public String getId() {
        return Id;
    }

    public String getText() {
        return Text;
    }

    public User getSender() {
        return Sender;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getFormattedTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                "dd/MM HH:mm"
        ).withZone(ZoneOffset.UTC);
        return formatter.format(timestamp);
    }

    private Instant timestamp;
}