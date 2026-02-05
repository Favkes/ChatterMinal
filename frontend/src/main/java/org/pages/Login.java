package org.pages;

import com.github.favkes.simpletui.components.*;
import com.github.favkes.simpletui.ui.Color;
import com.github.favkes.simpletui.ui.KeyBind;
import com.github.favkes.simpletui.ui.ModeManager;
import org.network.NetworkManager;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

public class Login extends Page {
    public ModeManager<Text> inputFieldModeManager = new ModeManager<>();
    public ModeManager<Frame> focusedFrameModeManager = new ModeManager<>();

    public final Map<String, AdvancedTexture> texturesMap;

    public Text usernameInputField;
    public Text passwordInputField;

    public Frame submitFrame;

    public Login() {
        super();
        texturesMap = new HashMap<>();
        inputFieldModeManager.add("username", usernameInputField);
        inputFieldModeManager.add("password", passwordInputField);

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
    }

    private void buildWidgets() {
        Frame mainFrame = new Frame(this.root,
                6, 14,
                11, 50,
                texturesMap.get("mainFrame")
        ); this.components.add(mainFrame);

        String mainFrameText1Content = " - Log into an existing account - ";
        Text mainFrameText1 = new Text(mainFrame,
                1, (mainFrame.width - mainFrameText1Content.length()) / 2,
                mainFrameText1Content
        ); this.components.add(mainFrameText1);

        Frame usernameFrame = new Frame(mainFrame,
                3, 2,
                1, 30,
                texturesMap.get("activeFrame")
        ); this.components.add(usernameFrame);
        focusedFrameModeManager.add("usernameFrame", usernameFrame);
        String mainFrameText2Content = "Username:";
        Text mainFrameText2 = new Text(usernameFrame,
                0, 1,
                mainFrameText2Content
        ); this.components.add(mainFrameText2);

        Frame passwordFrame = new Frame(mainFrame,
                5, 2,
                1, 30,
                texturesMap.get("mainFrame")
        ); this.components.add(passwordFrame);
        focusedFrameModeManager.add("passwordFrame", passwordFrame);
        String mainFrameText3Content = "Password:";
        Text mainFrameText3 = new Text(passwordFrame,
                0, 1,
                mainFrameText3Content
        ); this.components.add(mainFrameText3);


        String username = "";
        usernameInputField = new Text(usernameFrame,
                0, 12,
                username
        ); this.components.add(usernameInputField);
        usernameInputField.charLimit = 20;

        String password = "";
        passwordInputField = new Text(passwordFrame,
                0, 12,
                password
        ); this.components.add(passwordInputField);
        passwordInputField.charLimit = 20;

        submitFrame = new Frame(mainFrame,
                7, 20,
                3, 10,
                texturesMap.get("submitFrame")
        ); this.components.add(submitFrame);
        focusedFrameModeManager.add("submitFrame", submitFrame);
        Text submitFrameText = new Text(submitFrame,
                1, 1,
                "Submit"
        ); this.components.add(submitFrameText);
        inputFieldModeManager.add("submitFrame", submitFrameText);
    }

    private void shiftFocus() {
        if (focusedFrameModeManager.modeNames.get(focusedFrameModeManager.mode).equals("submitFrame")) {
            focusedFrameModeManager.modeItems.get(focusedFrameModeManager.mode).texture
                    = texturesMap.get("submitFrame");
        }
        else {
            focusedFrameModeManager.modeItems.get(focusedFrameModeManager.mode).texture
                    = texturesMap.get("mainFrame");
        }

        inputFieldModeManager.modeSwitchUp();
        focusedFrameModeManager.modeSwitchUp();

        focusedFrameModeManager.modeItems.get(focusedFrameModeManager.mode).texture
                = texturesMap.get("activeFrame");
    }

    public void buildBinds() {
        for (char c = 32; c <=126 ; c++) { // 33 - 126
            char keyChar = c;
            this.keyBinds.add(new KeyBind(
                    String.valueOf(keyChar),
                    () -> {
                        if (inputFieldModeManager.modeNames.get(inputFieldModeManager.mode).equals("username")) {
                            usernameInputField
                                    .updateContent(usernameInputField.content + keyChar);
                        }
                        else if (inputFieldModeManager.modeNames.get(inputFieldModeManager.mode).equals("password")) {
                            passwordInputField
                                    .updateContent(passwordInputField.content + keyChar);
                        }
                    }
            ));
        }
        keyBinds.add(new KeyBind(
                "\t",
                this::shiftFocus
        ));
        keyBinds.add(new KeyBind(
                "\177",
                () -> {
                    if (inputFieldModeManager.modeNames.get(inputFieldModeManager.mode).equals("username")) {
                        usernameInputField
                                .removeLast();
                    }
                    else if (inputFieldModeManager.modeNames.get(inputFieldModeManager.mode).equals("password")) {
                        passwordInputField
                                .removeLast();
                    }
                }
        ));
    }

}
