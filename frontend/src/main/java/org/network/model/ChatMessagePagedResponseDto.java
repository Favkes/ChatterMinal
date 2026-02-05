package org.network.model;

import com.google.gson.annotations.SerializedName;

import java.time.Instant;

public class ChatMessagePagedResponseDto {

    @SerializedName("chatRoomId")
    public String ChatRoomId;
    @SerializedName("messages")
    public ChatMessageDto[] Messages;
    @SerializedName("nextCursor")
    public Instant NextCursor;
    @SerializedName("hasMore")
    public Boolean HasMore;

    public ChatMessagePagedResponseDto(
            String chatroomId,
            ChatMessageDto[] messages,
            Instant nextCursor,
            Boolean hasMore
    ) {
        ChatRoomId = chatroomId;
        Messages = messages;
        NextCursor = nextCursor;
        HasMore = hasMore;
    }
}