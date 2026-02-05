package org;


import com.github.favkes.simpletui.components.*;
import com.github.favkes.simpletui.ui.Color;
import com.github.favkes.simpletui.ui.Displayer;
import com.github.favkes.simpletui.ui.KeyBind;
import org.network.NetworkManager;
import org.pages.*;



public class Main {
    public static void main(String[] args) {
        AdvancedTexture displayBackgroundTexture = new AdvancedTexture(
                Color.generateRGB(false, 30, 30, 30)
                        + Color.generateRGB(true, 30, 30, 30)
                        + "|"
                        + Color.generateRGB(true, 50, 50, 70)
                        + Color.generateRGB(false, 50, 50, 70)
                        + "."
                        + Color.generateRGB(true, 70, 70, 100)
                        + Color.generateRGB(false, 70, 70, 100)
                        + ".",
                1
        );

        NetworkManager networkManager = NetworkManager.getInstance();

        try (Displayer display = Displayer.init(displayBackgroundTexture)) {

            // App init ------------------------------------------------------------------------------------------------
            display.init();

//            Page page1 = display.pageManager.newPage();

            // Loading Pages -------------------------------------------------------------------------------------------
            OpenApp openPage = new OpenApp();
            display.pageManager.pages.add(openPage);
            display.inputManager.newMap("openPage");
            display.inputManager.loadFromIterable(openPage.keyBinds);

            ChatUse chatPage = new ChatUse();
            display.pageManager.pages.add(chatPage);
            display.inputManager.newMap("chatUsePage");
            display.inputManager.keyMapModeManager.modeSet("chatUsePage");
            display.inputManager.loadFromIterable(chatPage.keyBinds);

            ChatMake chatMakePage = new ChatMake();
            display.pageManager.pages.add(chatMakePage);
            display.inputManager.newMap("chatMakePage");
            display.inputManager.keyMapModeManager.modeSet("chatMakePage");
            display.inputManager.loadFromIterable(chatMakePage.keyBinds);

            Login loginPage = new Login();
            display.pageManager.pages.add(loginPage);
            display.inputManager.newMap("loginPage");
            display.inputManager.keyMapModeManager.modeSet("loginPage");
            display.inputManager.loadFromIterable(loginPage.keyBinds);

            Signin registerPage = new Signin();
            display.pageManager.pages.add(registerPage);
            display.inputManager.newMap("registerPage");
            display.inputManager.keyMapModeManager.modeSet("registerPage");
            display.inputManager.loadFromIterable(registerPage.keyBinds);


            ModeManagerOLD<Page> pagesModeManager = new ModeManagerOLD<>();
            pagesModeManager.add("openPage", openPage);         // 0
            pagesModeManager.add("chatUsePage", chatPage);      // 1
            pagesModeManager.add("chatMakePage", chatMakePage); // 2
            pagesModeManager.add("loginPage", loginPage);       // 3
            pagesModeManager.add("registerPage", registerPage); // 4


            display.inputManager.keyMapModeManager.modeSet("openPage");
            openPage.keyBinds.add(
                    new KeyBind("\033", () -> display.running.set(false)
            )); // escape
            openPage.keyBinds.add(
                    new KeyBind("\r", () -> {
                        openPage.hide();

                        if (openPage.modeManager.mode == 0) {
                            registerPage.show();

                            display.inputManager.keyMapModeManager.modeSet("registerPage");
                            display.inputManager.bindKey("\033", () -> display.running.set(false)); // escape
                            display.inputManager.switchMap();   // restart the input reader thread
                        } else {
                            loginPage.show();

                            display.inputManager.keyMapModeManager.modeSet("loginPage");
                            display.inputManager.bindKey("\033", () -> display.running.set(false)); // escape
                            display.inputManager.switchMap();   // restart the input reader thread
                        }
            }));
            display.inputManager.loadFromIterable(openPage.keyBinds);


            display.inputManager.keyMapModeManager.modeSet("registerPage");
            registerPage.keyBinds.add(new KeyBind(
                    "\r",
                    () -> {
                        if (registerPage.focusedFrameModeManager.modeNames
                                .get(registerPage.focusedFrameModeManager.mode)
                                .equals("submitFrame")) {
//                            NetworkManager networkManager = NetworkManager.getInstance();
                            char[] login = registerPage.usernameInputField.content.toCharArray();
                            char[] password = registerPage.passwordInputField.content.toCharArray();

                            registerPage.submitFrame.texture = registerPage.texturesMap.get("mainFrame");
                            boolean success = networkManager.createAccount(login, password);
                            registerPage.submitFrame.texture = registerPage.texturesMap.get("activeFrame");

                            if (success) {
                                networkManager.saveCredentials(login, password);
                                registerPage.hide();

                                chatPage.show();

                                display.inputManager.keyMapModeManager.modeSet("chatUsePage");
                                display.inputManager.loadFromIterable(chatPage.keyBinds);
                                display.inputManager.bindKey("\033", () -> display.running.set(false)); // escape

                                display.inputManager.switchMap();   // restart the input reader thread
                            } else {
                                registerPage.hide();

                                openPage.show();

                                display.inputManager.keyMapModeManager.modeSet("openPage");
                                display.inputManager.loadFromIterable(openPage.keyBinds);
                                display.inputManager.bindKey("\033", () -> display.running.set(false)); // escape

                                display.inputManager.switchMap();   // restart the input reader thread
                            }
                        }
                        else {
                            registerPage.focusedFrameModeManager.modeItems
                                    .get(registerPage.focusedFrameModeManager.mode).texture
                                    = registerPage.texturesMap.get("mainFrame");
                        }

                        registerPage.inputFieldModeManager.modeSwitchUp();
                        registerPage.focusedFrameModeManager.modeSwitchUp();

                        registerPage.focusedFrameModeManager.modeItems
                                .get(registerPage.focusedFrameModeManager.mode).texture
                                = registerPage.texturesMap.get("activeFrame");
                    }
            ));
            display.inputManager.loadFromIterable(registerPage.keyBinds);



            display.inputManager.keyMapModeManager.modeSet("loginPage");
            loginPage.keyBinds.add(new KeyBind(
                    "\r",
                    () -> {
                        if (loginPage.focusedFrameModeManager.modeNames
                                .get(loginPage.focusedFrameModeManager.mode)
                                .equals("submitFrame")) {
//                            NetworkManager networkManager = NetworkManager.getInstance();
                            char[] login = loginPage.usernameInputField.content.toCharArray();
                            char[] password = loginPage.passwordInputField.content.toCharArray();

                            loginPage.submitFrame.texture = loginPage.texturesMap.get("mainFrame");
                            networkManager.saveCredentials(login, password);
                            boolean success = networkManager.checkConnection();
                            loginPage.submitFrame.texture = loginPage.texturesMap.get("activeFrame");

                            if (success) {
                                networkManager.saveCredentials(login, password);
                                loginPage.hide();

                                chatPage.show();

                                display.inputManager.keyMapModeManager.modeSet("chatUsePage");
                                display.inputManager.loadFromIterable(chatPage.keyBinds);
                                display.inputManager.bindKey("\033", () -> display.running.set(false)); // escape

                                display.inputManager.switchMap();   // restart the input reader thread
                            } else {
                                loginPage.hide();

                                openPage.show();

                                display.inputManager.keyMapModeManager.modeSet("openPage");
                                display.inputManager.loadFromIterable(openPage.keyBinds);
                                display.inputManager.bindKey("\033", () -> display.running.set(false)); // escape

                                display.inputManager.switchMap();   // restart the input reader thread
                            }
                        }
                        else {
                            loginPage.focusedFrameModeManager.modeItems
                                    .get(loginPage.focusedFrameModeManager.mode).texture
                                    = loginPage.texturesMap.get("mainFrame");
                        }

                        loginPage.inputFieldModeManager.modeSwitchUp();
                        loginPage.focusedFrameModeManager.modeSwitchUp();

                        loginPage.focusedFrameModeManager.modeItems
                                .get(loginPage.focusedFrameModeManager.mode).texture
                                = loginPage.texturesMap.get("activeFrame");
                    }
            ));
            display.inputManager.loadFromIterable(loginPage.keyBinds);



            openPage.show();
            chatPage.hide();
            chatMakePage.hide();
            loginPage.hide();
            registerPage.hide();


//            display.inputManager.keyMapModeManager.modeSet("openPage");
//            display.inputManager.loadFromIterable(openPage.keyBinds);

//            display.inputManager.loadFromIterable(registerPage.keyBinds);



//            AdvancedTexture textureRedChecker = new AdvancedTexture(
//                    Color.generateBgFg(130, 30, 30, 30, 30, 30)
//                            + "|"
//                            + Color.generateBgFg(150, 50, 50, 150, 50, 50)
//                            + "."
//                            + Color.generateBgFg(170, 70, 70, 170, 70, 70)
//                            + "."
//                            + Color.generateBgFg(170, 100, 100, 170, 100, 100)
//                            + "."
//                            + Color.generateBgFg(230, 130, 130, 230, 130, 130)
//                            + ".",
//                    1,
//                    r -> (100 - r) % 5
//            );
//            System.out.print("\n");
//            textureRedChecker.test();
//
//            AdvancedTexture textureCyanChecker = new AdvancedTexture(
//                    Color.generateBgFg(120, 200, 200, 120, 200, 200)
//                            + "."
//                            + Color.generateBgFg(130, 210, 210, 130, 210, 210)
//                            + "."
//                            + Color.generateBgFg(140, 220, 220, 140, 220, 220)
//                            + ".",
//                    1,
//                    r -> r
//            );
//
//            AdvancedTexture textureYellowChecker = new AdvancedTexture(
//                    Color.generateBgFg(200, 200, 120, 200, 200, 120)
//                            + "."
//                            + Color.generateBgFg(210, 210, 130, 210, 210, 130)
//                            + "."
//                            + Color.generateBgFg(220, 220, 140, 220, 220, 140)
//                            + ".",
//                    1,
//                    r -> r
//            );
//
//            Frame frame1 = new Frame(
//                    page1.root, 5, 10, 5, 7, textureRedChecker
//            );
//            page1.components.add(frame1);
//
//
//            // WINDOW BORDERS ------------------------------------------------------------------------------------------
//            AdvancedTexture windowBorderTexture = new AdvancedTexture(
//                    Color.generateRGB(true, 60, 60, 60)
//                            + Color.generateRGB(false,  160, 250, 160)
//                            + "|",
//                    1,
//                    r -> 0
//            );
//            AdvancedTexture windowBorderTexture2 = new AdvancedTexture(
//                    Color.generateRGB(true, 60, 60, 60)
//                            + Color.generateRGB(false,  160, 250, 160)
//                            + "-",
//                    1,
//                    r -> 0
//            );
//            Frame windowBorderLeft = new Frame(
//                    page1.root, 0, 0, display.terminal.getHeight(), 1, windowBorderTexture
//            );
//            page1.components.add(windowBorderLeft);
//            Frame windowBorderRight = new Frame(
//                    page1.root, 0, display.terminal.getWidth()-1, display.terminal.getHeight(), 1, windowBorderTexture
//            );
//            page1.components.add(windowBorderRight);
//            Frame windowBorderTop = new Frame(
//                    page1.root, 0, 0, 1, display.terminal.getWidth(), windowBorderTexture2
//            );
//            page1.components.add(windowBorderTop);
//            Frame windowBorderBottom = new Frame(
//                    page1.root, display.terminal.getHeight()-1, 0, 1, display.terminal.getWidth(), windowBorderTexture2
//            );
//            page1.components.add(windowBorderBottom);
//
//            // vertical line between menu and chat
//            page1.components.add(
//                    new Frame(
//                            page1.root,
//                            1, 25, display.terminal.getHeight()-2, 1,
//                            windowBorderTexture
//                    )
//            );

            // FRAMES --------------------------------------------------------------------------------------------------

//            Frame menuAFrame = new Frame(
//                    page1.root,
//                    2, 3+1, 3, 14,
//                    textureCyanChecker
//            ); page1.components.add(menuAFrame);
//            Text menuAText1 = new Text(
//                    menuAFrame,
//                    1, 1,
//                    "Write Msg"
//            ); page1.components.add(menuAText1);
//            pageModeManager.add("writemsg", menuAFrame);
//
//
//            Frame menuBFrame = new Frame(
//                    page1.root,
//                    6, 3, 3, 14,
//                    textureYellowChecker
//            ); page1.components.add(menuBFrame);
//            Text menuBText1 = new Text(
//                    menuBFrame,
//                    1, 1,
//                    "Create Acc"
//            ); page1.components.add(menuBText1);
//            pageModeManager.add("signin", menuBFrame);
//
//
//            Frame menuCFrame = new Frame(
//                    page1.root,
//                    10, 3, 3, 14,
//                    textureYellowChecker
//            ); page1.components.add(menuCFrame);
//            Text menuCText1 = new Text(
//                    menuCFrame,
//                    1, 1,
//                    "Log In"
//            ); page1.components.add(menuCText1);
//            pageModeManager.add("login", menuCFrame);
//
//
//            Frame menuDFrame = new Frame(
//                    page1.root,
//                    14, 3, 3, 14,
//                    textureYellowChecker
//            ); page1.components.add(menuDFrame);
//            Text menuDText1 = new Text(
//                    menuDFrame,
//                    1, 1,
//                    "Create Chat"
//            ); page1.components.add(menuDText1);
//            pageModeManager.add("newchat", menuDFrame);



//            AdvancedTexture messageWriteBoxTexture = new AdvancedTexture(
//                    Color.generateBgFg(200, 200, 200, 30, 30, 80)
//                            + " "
//            );
//            Frame messageWriteBox = new Frame(
//                    page1.root,
//                    display.terminal.getHeight()-2-1,
//                    26,
//                    2,
//                    display.terminal.getWidth()-26-1,
//                    messageWriteBoxTexture
//            );
//            page1.components.add(messageWriteBox);
//
//            Text messageWriteBoxText = new Text(
//                    messageWriteBox,
//                    0, 0,
//                    "Content Preview (Click Enter to remove)"
//            );
//            page1.components.add(messageWriteBoxText);
//            messageWriteBoxText.updateContentLength();
//            messageWriteBoxText.renderToPixels();


            // Input thread setup --------------------------------------------------------------------------------------
//            display.inputManager.bindKey("\033[A", () -> frame1.y--, frame1);
//            display.inputManager.bindKey("\033[D", () -> frame1.x--, frame1);
//            display.inputManager.bindKey("\033[B", () -> frame1.y++, frame1);
//            display.inputManager.bindKey("\033[C", () -> frame1.x++, frame1);
//            display.inputManager.bindKey("\u0012", () -> frame1.x += 0);            // Ctrl+R
//            display.inputManager.bindKey("\t", () -> {
//                Frame f1 = modeManager.modeFrames.get(modeManager.mode);
//                f1.texture = textureYellowChecker;
//                f1.x -= 1;
//
//                modeManager.modeSwitchUp();
//
//                Frame f2 = modeManager.modeFrames.get(modeManager.mode);
//                f2.texture = textureCyanChecker;
//                f2.x += 1;
//            });                                                                          // Tab
//            display.inputManager.bindKey("\033\r", () -> {
//                frame1.shouldRender =! frame1.shouldRender;
//                frame1.isFocused =! frame1.isFocused;
//            });            // Alt+Enter



//            display.inputManager.bindKey("\r", () -> messageWriteBoxText.updateContent(""));    // enter
//            display.inputManager.bindKey("\177", messageWriteBoxText::removeLast);  // backspace

            // adding all the valid ASCII characters: ------------------------------------------------------------------
//            for (char c = 32; c <=126 ; c++) { // 33 - 126
//                char keyChar = c;
//                display.inputManager.bindKey(
//                        String.valueOf(keyChar),
//                        () -> messageWriteBoxText.updateContent(messageWriteBoxText.content + keyChar)
//                );
//            }


            // Application loop ----------------------------------------------------------------------------------------
//            frame1.isFocused = true;
//            openPage.show();
//            registerPage.hide();
//            page1.hide();

            // first page to open:



            display.inputManager.keyMapModeManager.modeSet("openPage");
            display.inputManager.switchMap();
            while (display.running.get()) {
                display.generateBlankPixelMatrix();
                display.rebuildEmpty();

                display.renderAll();

                display.refreshDisplay();
                Thread.sleep(1000 / 20);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}