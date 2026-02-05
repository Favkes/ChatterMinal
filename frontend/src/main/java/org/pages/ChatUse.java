package org.pages;

import com.github.favkes.simpletui.components.AdvancedTexture;
import com.github.favkes.simpletui.components.Frame;
import com.github.favkes.simpletui.components.Page;
import com.github.favkes.simpletui.components.Text;
import com.github.favkes.simpletui.ui.Color;
import com.github.favkes.simpletui.ui.KeyBind;
import com.github.favkes.simpletui.ui.ModeManager;
import org.network.NetworkManager;
import org.network.model.ChatMessageDto;
import org.network.model.ChatMessagePagedResponseDto;
import org.network.model.ChatRoom;
import org.network.model.MessageRequest;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatUse extends Page {
    public ModeManager<Text> inputFieldModeManager = new ModeManager<>();
    public ModeManager<Frame> focusedMessageModeManager = new ModeManager<>();

    public final Map<String, AdvancedTexture> texturesMap;

    public Text messageInputField;
    private ScheduledExecutorService pollingScheduler;
    private volatile boolean pollingActive = false;

    private List<ChatRoom> chatRoomsList;
    private String currentRoomId;

    private final List<Text> messageAuthorTexts = new ArrayList<>();
    private final List<Text> messageContentTexts = new ArrayList<>();

    private final List<ChatMessageDto> pendingMessages = Collections.synchronizedList(new ArrayList<>());
//    private Instant lastRead = Instant.EPOCH;

    public ChatUse() {
        super();

        texturesMap = new HashMap<>();

        buildTextures();
        buildWidgets();
        buildBinds();
    }

    private void buildTextures() {
        texturesMap.put("mainFrame",
                new AdvancedTexture(
                        Color.generateBgFg(240, 200, 200, 240, 200, 200)
                                + ".",
                        1,
                        r -> r
                ));
        texturesMap.put("submitFrame",
                new AdvancedTexture(
                        Color.generateBgFg(150, 200, 200, 150, 200, 200)
                                + ".",
                        1,
                        r -> r
                ));
        texturesMap.put("activeFrame",
                new AdvancedTexture(
                        Color.generateBgFg(240, 100, 200, 240, 100, 200)
                                + ".",
                        1,
                        r -> r
                ));
        texturesMap.put("messageWriteFrame",
                new AdvancedTexture(
                        Color.generateBgFg(140, 50, 100, 140, 50, 100)
                                + ".",
                        1,
                        r -> r
                ));
        texturesMap.put("messageIncomingFrame",
                new AdvancedTexture(
                        Color.generateBgFg(140, 50, 200, 140, 50, 200)
                                + ".",
                        1,
                        r -> r
                ));
    }

    private void buildWidgets() {
        Frame chatFrame = new Frame(this.root,
                2, 15,
                18, 61,
                texturesMap.get("mainFrame")
        ); this.components.add(chatFrame);

        String mainFrameText1Content = " - Your messages - ";
        Text mainFrameText1 = new Text(chatFrame,
                0, (chatFrame.width - mainFrameText1Content.length()) / 2,
                mainFrameText1Content
        ); this.components.add(mainFrameText1);

        Frame chatlistFrame = new Frame(this.root,
                2, 1,
                18, 13,
                texturesMap.get("mainFrame")
        ); this.components.add(chatlistFrame);

        String chatlistFrameTextContent = " - Rooms - ";
        Text chatlistFrameText = new Text(chatlistFrame,
                0, (chatlistFrame.width - chatlistFrameTextContent.length()) / 2,
                chatlistFrameTextContent
        ); this.components.add(chatlistFrameText);



        Frame messageWriteFrame = new Frame(chatFrame,
                chatFrame.height - 2, 2,
                2, 56,
                texturesMap.get("messageWriteFrame")
        ); this.components.add(messageWriteFrame);
//        focusedFrameModeManager.add("messageWriteFrame", messageWriteFrame);

        String messageWriteFrameText1Content = "Your message:";
        Text mainFrameText2 = new Text(messageWriteFrame,
                0, 1,
                messageWriteFrameText1Content
        ); this.components.add(mainFrameText2);

        String messageWriteContent = "";
        messageInputField = new Text(messageWriteFrame,
                1, 0,
                messageWriteContent
        ); this.components.add(messageInputField);
        messageInputField.charLimit = 56;



        for (int i=0; i<5; i++) {
            Frame messageIncomingFrame = new Frame(chatFrame,
                    1 + 3*i, 2,
                    2, 56,
                    texturesMap.get("messageIncomingFrame")
            ); this.components.add(messageIncomingFrame);
            focusedMessageModeManager.add("messageIncomingFrame" + i, messageIncomingFrame);

            Text author = new Text(messageIncomingFrame,
                    0, 1,
                    "Author unknown:"
            ); this.components.add(author); messageAuthorTexts.add(author);

            Text messageContent = new Text(messageIncomingFrame,
                    1, 0,
                    "Content unknown:"
            ); this.components.add(messageContent); messageContentTexts.add(messageContent);
        }
    }

    private void buildBinds() {
        NetworkManager manager = NetworkManager.getInstance();
        for (char c = 32; c <=126 ; c++) { // 33 - 126
            char keyChar = c;
            this.keyBinds.add(new KeyBind(
                    String.valueOf(keyChar),
                    () -> {
                        messageInputField
                                .updateContent(messageInputField.content + keyChar);
                    }
            ));
        }
        keyBinds.add(new KeyBind(
                "\177",
                messageInputField::removeLast
        ));
        keyBinds.add(new KeyBind(
                "\r",
                () -> {
                    if (!messageInputField.content.isEmpty()) {
                        manager.sendMessage(
                                new MessageRequest(currentRoomId, messageInputField.content)
                        );

                        messageInputField.updateContent("");
                    }
                }
        ));
    }

    public void updateLastMessages() {
        int maxMessages = focusedMessageModeManager.modeItems.size();
        List<ChatMessageDto> lastMessages;

        synchronized (pendingMessages) {
            int end = Math.min(maxMessages, pendingMessages.size());
            lastMessages = new ArrayList<>(pendingMessages.subList(0, end));
        }

        for (int i = 0; i < maxMessages; i++) {
            Frame frame = focusedMessageModeManager.modeItems.get(i);
            frame.children.clear();
            if (i < lastMessages.size()) {
                ChatMessageDto msg = lastMessages.get(i);
                messageAuthorTexts.get(i).updateContent(msg.getSender().getName() + ":");
                messageContentTexts.get(i).updateContent(msg.getText());
            } else {
                // Clear if no message
                messageAuthorTexts.get(i).updateContent("");
                messageContentTexts.get(i).updateContent("");
            }
        }
    }


    public void startPolling(String roomId) {
        if (pollingActive) return;

        pollingActive = true;

        pollingScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "chat-polling-thread");
            t.setDaemon(true);
            return t;
        });

        pollingScheduler.scheduleAtFixedRate(() -> {
            try {
                NetworkManager nm = NetworkManager.getInstance();

                ChatMessagePagedResponseDto response =
                        nm.ReadMessages(roomId, Instant.now(), 10);

                synchronized (pendingMessages) {
                    pendingMessages.clear();
                    pendingMessages.addAll(Arrays.asList(response.Messages));
                }

                // --- DEBUG DUMP ---
                try (FileWriter fw = new FileWriter("chat_dump.txt")) {
                    for (ChatMessageDto msg : response.Messages) {
                        fw.write("Sender: " + msg.getSender().getName() + "\n");
                        fw.write("Content: " + msg.getText() + "\n");
                        fw.write("-----\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//
//                // Exit the program to inspect the file
//                System.out.println("Messages dumped to chat_dump.txt, exiting.");
//                System.exit(1);
                updateLastMessages();

            } catch (Exception ignored) {}
        }, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void show() {
        super.show();
        NetworkManager manager = NetworkManager.getInstance();
        chatRoomsList = manager.getAvailableRooms();
        currentRoomId = chatRoomsList.get(0).getId();
        manager.joinRoom(UUID.fromString(currentRoomId));
        startPolling(currentRoomId);
    }

    @Override
    public void hide() {
        super.hide();
        if (pollingScheduler != null) {
            pollingScheduler.shutdownNow();
            pollingActive = false;
        }
    }
}
