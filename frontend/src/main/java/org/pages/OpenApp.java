package org.pages;

import com.github.favkes.simpletui.components.AdvancedTexture;
import com.github.favkes.simpletui.components.Frame;
import com.github.favkes.simpletui.components.Page;
import com.github.favkes.simpletui.components.Text;
import com.github.favkes.simpletui.ui.Color;
import com.github.favkes.simpletui.ui.KeyBind;
import com.github.favkes.simpletui.ui.ModeManager;

import java.util.HashMap;
import java.util.Map;

public class OpenApp extends Page {
    public ModeManager<Frame> modeManager = new ModeManager<>();

    private final Map<String, AdvancedTexture> texturesMap;


    public OpenApp() {
        super();
        texturesMap = new HashMap<>();

        buildTextures();
        buildWidgets();
        buildBinds();
    }

    private void buildTextures() {
        texturesMap.put("mainFrame",
                new AdvancedTexture(
                Color.generateBgFg(200, 130, 200, 200, 130, 200)
                        + ".",
                1,
                r -> r
        ));
        texturesMap.put("optionFrame",
                new AdvancedTexture(
                Color.generateBgFg(200, 100, 180, 200, 100, 180)
                        + "."
                        + Color.generateBgFg(180, 100, 180, 180, 100, 180)
                        + "."
        ));
        texturesMap.put("optionFrameActive",
                new AdvancedTexture(
                Color.generateBgFg(180, 130, 220, 180, 130, 220)
                        + "."
                        + Color.generateBgFg(180, 130, 200, 180, 130, 200)
                        + "."
        ));
    }

    private void buildWidgets() {
        Frame mainFrame = new Frame(this.root,
                6, 14,
                10, 50,
                texturesMap.get("mainFrame")
        ); this.components.add(mainFrame);

        String mainFrameText1Content = " - Welcome to CHATTER - ";
        Text mainFrameText1 = new Text(mainFrame,
                1, (mainFrame.width - mainFrameText1Content.length()) / 2,
                mainFrameText1Content
        ); this.components.add(mainFrameText1);


        Frame option1Frame = new Frame(mainFrame,
                3, 3,
                5, 20,
                texturesMap.get("optionFrameActive")
        ); this.components.add(option1Frame);
        Text option1Text = new Text(option1Frame,
                2, 3,
                "Create account"
        ); this.components.add(option1Text);
        modeManager.add("signup", option1Frame);

        Frame option2Frame = new Frame(mainFrame,
                3, mainFrame.width - 3 - 20,
                5, 20,
                texturesMap.get("optionFrame")
        ); this.components.add(option2Frame);
        Text option2Text = new Text(option2Frame,
                1, 6,
                "Log into"
        ); this.components.add(option2Text);
        Text option2_2Text = new Text(option2Frame,
                2, 2,
                "existing account"
        ); this.components.add(option2_2Text);
        modeManager.add("signin", option2Frame);
    }

    public void buildBinds() {
        keyBinds.add(new KeyBind("\t", () ->{
            Frame f1 = modeManager.modeItems.get(modeManager.mode);
            setTextureUnfocused(f1);

            modeManager.modeSwitchUp();

            Frame f2 = modeManager.modeItems.get(modeManager.mode);
            setTextureFocused(f2);
        }));

//        keyBinds.add(new KeyBind("\033\r", () ->{
//            Frame f1 = modeManager.modeItems.get(modeManager.mode);
//            f1.y += 1;
//        }));
    }

    public void setTextureFocused(Frame f) {
        f.texture = texturesMap.get("optionFrameActive");
    }
    public void setTextureUnfocused(Frame f) {
        f.texture = texturesMap.get("optionFrame");
    }
}
