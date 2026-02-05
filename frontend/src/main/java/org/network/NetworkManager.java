package org.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import org.network.model.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.UUID;

public class NetworkManager implements INetworkManager{

    private static NetworkManager instance;
    private BasicCredentials credentials;
    private final HttpClient httpClient;
    private final Gson gson;

    private String baseUrl = "https://matikson.ovh/";

    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    private NetworkManager() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        gson = new GsonBuilder()
                .registerTypeAdapter(
                        Instant.class,
                        (JsonDeserializer<Instant>) (json, type, ctx) -> {
                            String s = json.getAsString();
                            try {
                                return Instant.parse(s);
                            } catch (Exception ignored) {
                                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                                        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                        .toFormatter()
                                        .withZone(ZoneOffset.UTC);
                                return Instant.from(formatter.parse(s));
                            }
                        }
                )
                .create();
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }


    @Override
    public int saveCredentials(char[] login, char[] password) {
        credentials = new BasicCredentials(login, password);
        return 0;
    }

    @Override
    public boolean createAccount(char[] login, char[] password) {
        CreateAccountRequestDto requestDto = new CreateAccountRequestDto(login, password);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestDto)))
                .header("content-type", "application/json")
                .uri(
                        URI.create(baseUrl + "api/auth/register")
                )
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );
            return response.statusCode() == 200;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean checkConnection() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Authorization", credentials.getAuthorizationHeader())
                .uri(URI.create(baseUrl + "api/auth/test"))
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.discarding()
            );
//            System.out.println(response.statusCode());
            return response.statusCode() == 200;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ChatRoom> getAvailableRooms() {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .header("Authorization", credentials.getAuthorizationHeader())
                .uri(URI.create( baseUrl + "api/chat/rooms"))
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            String body = response.body();

            TypeToken<List<ChatRoom>> listType = new TypeToken<>() {
            };

            return gson.fromJson(body, listType);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean sendMessage(MessageRequest request) {
        TextMessageRequestDto requestDto = new TextMessageRequestDto(request.text);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(requestDto)))
                .header("content-type", "application/json")
                .header("Authorization", credentials.getAuthorizationHeader())
                .uri(
                        URI.create(
                                String.format(
                                        baseUrl + "api/chat/%s/messages",
                                        request.roomId.toString()
                                )
                        )
                )
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.discarding()
            );
            return response.statusCode() == 200;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public ChatMessagePagedResponseDto ReadMessages(String chatroomId, Instant before, int limit) {

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .header("content-type", "application/json")
                .header("Authorization", credentials.getAuthorizationHeader())
                .uri(
                        URI.create(
                                String.format(baseUrl + "api/chat/%s/messages?limit=%d&before=%s",
                                        chatroomId,
                                        limit,
                                        before.toString()
                                )
                        )
                )
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    httpRequest,
                    HttpResponse.BodyHandlers.ofString()
            );

            String body = response.body();
//            System.out.println(body);

            TypeToken<ChatMessagePagedResponseDto> typeToken = new TypeToken<>() {};

            return gson.fromJson(body, typeToken);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public UUID createRoom(String name) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(new CreateChatRoomRequestDto(name))))
                .header("content-type", "application/json")
                .header("Authorization", credentials.getAuthorizationHeader())
                .uri(URI.create(baseUrl + "api/chat/rooms/create"))
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            String body = response.body();
            String idStr = gson.fromJson(body, String.class);
            return UUID.fromString(idStr);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean joinRoom(UUID roomId) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Authorization", credentials.getAuthorizationHeader())
                .uri(URI.create(baseUrl + "api/chat/rooms/" + roomId + "/join"))
                .timeout(Duration.ofSeconds(10))
                .build();

        try {
            HttpResponse<Void> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
            int code = response.statusCode();
            return code == 200 || code == 409; // Joined or AlreadyMember
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}