package org.network;

import org.network.model.ChatMessagePagedResponseDto;
import org.network.model.ChatRoom;
import org.network.model.MessageRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

interface INetworkManager {
    int saveCredentials(char[] login, char[] password);
    boolean createAccount(char[] login, char[] password);
    boolean checkConnection();
    List<ChatRoom> getAvailableRooms();
    boolean sendMessage(MessageRequest request);
    ChatMessagePagedResponseDto ReadMessages(String chatroomId, Instant before, int limit);
    UUID createRoom(String name);
    boolean joinRoom(UUID roomId);
}