import Utilities.*;
import Utilities.Button;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

import java.io.IOException;
import java.text.DecimalFormat;

public class Main extends PApplet {

    boolean pMousePressed;
    PVector mousePos;
    PVector screenSize, pScreenSize;
    ParticleGenerator pg;
    Time time;
    float heightPadding;
    int textSize;
    String pSpritePath;
    TextBox savePathTextBox, loadPathTextBox;
    Button saveButton, loadButton;
    boolean paused, saving, loading, saved, loaded;
    boolean pENTER;
    boolean noUi;
    float currTextSize;

    UIHandler uiHandler;
    SliderGroup sizeSliderGroup;
    SliderGroup opacitySliderGroup;

    public void settings() {
        noSmooth();
        size(640,360);
    }

    public void setup() {
        surface.setResizable(true);
        time = new Time();
        pMousePressed = false;
        mousePos = new PVector(0,0);
        screenSize = new PVector(width, height);
        pScreenSize = new PVector(width, height);
        DecimalFormat df = new DecimalFormat("#.##");
        saving = false;
        loading = false;
        pENTER = false;
        noUi = false;

        heightPadding = height / 18f;

        String[] fontList = PFont.list();
        printArray(fontList);

        textSize = 10;
        currTextSize = textSize;
        PFont font = createFont("Source Sans Pro", textSize);
        textFont(font);

        pSpritePath = "";

        sizeSliderGroup = new SliderGroup(new PVector(width*0.88f,height*0.68f),
                new PVector(75,30), 10, 2, screenSize, this);
        opacitySliderGroup = new SliderGroup(new PVector(width*0.88f,height*0.85f),
                new PVector(75,30), 10, 2, screenSize, this);
        saveButton = new Button(screenSize, new PVector(190,10), 50, 25, "Save");
        loadButton = new Button(screenSize, new PVector(250,10), 50, 25, "Load");
        savePathTextBox = new TextBox(new PVector(width*0.4f,height*0.55f), textSize, width*0.2f, width, height, this);
        loadPathTextBox = new TextBox(new PVector(width*0.4f,height*0.55f), textSize, width*0.2f, width, height, this);

        uiHandler = new UIHandler(screenSize, new PVector(width*0.5f,10),18, 2,width*0.25f, height/18f, textSize, this);
        uiHandler.loadFile("UIData/UIHandlerDefaultData.txt", screenSize);
        pg = new ParticleGenerator(uiHandler, this, time);

        pg.update(mousePos, uiHandler.checkBoxes[1][1].isValue(), uiHandler.textBoxes[16][0].isClickedOn(), uiHandler, sizeSliderGroup.getValues(), opacitySliderGroup.getValues(), pSpritePath);

        frameRate(999);
    }

    public void draw() {
        preDraw();
        background(0);

        float d = ((screenSize.x) / (pScreenSize.x));
        currTextSize *= d;

        textSize(currTextSize);
        pScreenSize = screenSize.copy();

        if (!paused) {
            pSpritePath = pg.spritePath;
            pg.update(mousePos, uiHandler.checkBoxes[1][1].isValue(), uiHandler.textBoxes[16][0].isClickedOn(), uiHandler, sizeSliderGroup.getValues(),
                    opacitySliderGroup.getValues(), pSpritePath);
            pg.update();

            sizeSliderGroup.update(screenSize, mousePos, mousePressed, pMousePressed);
            opacitySliderGroup.update(screenSize, mousePos, mousePressed, pMousePressed);
        }

        if (noUi) {
            pg.drawParticles(paused);
            return;
        }

        uiHandler.update(screenSize, mousePos, mousePressed, pMousePressed);

        noFill();
        stroke(255);
        rect(saveButton.getPos().x, saveButton.getPos().y, saveButton.getWidth(), saveButton.getHeight());
        rect(loadButton.getPos().x, loadButton.getPos().y, loadButton.getWidth(), loadButton.getHeight());

        saveButton.draw(this, screenSize);
        loadButton.draw(this, screenSize);

        pg.drawParticles(paused);

        uiHandler.draw();

        sizeSliderGroup.draw();
        opacitySliderGroup.draw();

        saveLoad();

        if (uiHandler.checkBoxes[1][0].isValue()) {
            noFill();
            stroke(0,255,0);
            rect(pg.spawnPos.x - (pg.spawnShapeSize.x*0.5f), pg.spawnPos.y - (pg.spawnShapeSize.y*0.5f),
                    pg.spawnShapeSize.x, pg.spawnShapeSize.y);
            float avgVel = (pg.getparticleVelRange().x + pg.getparticleVelRange().y) / 2;

            float start = pg.getparticleVelDirection() - pg.getparticleVelSpread()/2;
            float end = pg.getparticleVelDirection() + pg.getparticleVelSpread()/2;
            pushMatrix();
            translate(pg.spawnPos.x, pg.spawnPos.y);
            scale(1,-1);
            arc(0,0,avgVel, avgVel, start, end);
            line(0,0,avgVel/2*cos(start),avgVel/2*sin(start));
            line(0,0,avgVel/2*cos(end),avgVel/2*sin(end));
            popMatrix();
        }

        noStroke();
        fill(255);
        text(frameRate, 0, 20);

        postDraw();
    }

    private void saveLoad() {
        saveButton.update(screenSize, mousePos, mousePressed, pMousePressed);
        loadButton.update(screenSize, mousePos, mousePressed, pMousePressed);

        if (saveButton.isClicked()) {
            saving = !saving;
            if (loading) loading = false;
        }

        if (loadButton.isClicked()) {
            loading = !loading;
            if (saving) saving = false;
        }

        paused = loading || saving;
        float promptWidth = width*0.5f;
        float promptHeight = height*0.2f;

        if (!saving) savePathTextBox.makeEmpty();
        if (!loading) loadPathTextBox.makeEmpty();

        if (saving) {
            fill(0);
            stroke(255);
            rect((width*0.5f) - (promptWidth/2), (height*0.5f) - (promptHeight/2), promptWidth, promptHeight);
            String s = "Save path:";
            fill(255);
            text(s, (width*0.5f) - (textWidth(s)/2), height*0.475f);
            savePathTextBox.update(width, height, mousePos, mousePressed, pMousePressed);
            savePathTextBox.draw();
            if (!pENTER && key == ENTER) {
                saved = true;
                try {
                    uiHandler.saveFile("default\\particles\\" + savePathTextBox.getLettersString().trim(), sizeSliderGroup, opacitySliderGroup);
                } catch (IOException e) {
                    e.printStackTrace();
                    saved = false;
                }
            }
            if (saved) {
                text("File saved!", (width*0.6f) - (textWidth(s)/2), height*0.475f);
            }
        } else saved = false;

        if (loading) {
            fill(0);
            stroke(255);
            rect((width*0.5f) - (promptWidth/2), (height*0.5f) - (promptHeight/2), promptWidth, promptHeight);
            String s = "Load path:";
            fill(255);
            text(s, (width*0.5f) - (textWidth(s)/2), height*0.475f);
            loadPathTextBox.update(width, height, mousePos, mousePressed, pMousePressed);
            loadPathTextBox.draw();
            if (!pENTER && key == ENTER) {
                loaded = true;
                String filePath = "default\\particles\\" + loadPathTextBox.getLettersString().trim();
                loaded = uiHandler.loadFile(filePath, screenSize);
                uiHandler.loadSliderGroup(22, sizeSliderGroup, 10, filePath);
                uiHandler.loadSliderGroup(32, opacitySliderGroup, 10, filePath);
                pg.update(mousePos, uiHandler.checkBoxes[1][1].isValue(), true, uiHandler, sizeSliderGroup.getValues(),
                        opacitySliderGroup.getValues(), pSpritePath);
            }
            if (loaded) {
                text("File loaded!", (width*0.6f) - (textWidth(s)/2), height*0.475f);
            }
        } else loaded = false;
    }

    private void preDraw() {
        mousePos = new PVector(mouseX, mouseY);
        screenSize = new PVector(width, height);
        time.update();
    }

    public void keyPressed() {
        uiHandler.updateTextBoxKey(keyCode, key);
        loadPathTextBox.updateKeyPressed(keyCode, key);
        savePathTextBox.updateKeyPressed(keyCode, key);
        if (key == 'u' && !loading && !saving) noUi = !noUi;
    }

    public void keyReleased() {
        if (keyCode == SHIFT) {
            uiHandler.updateTextBoxShift();
            loadPathTextBox.setShift(false);
            savePathTextBox.setShift(false);
        }
        if (keyCode == 17) {
            uiHandler.updateTextBoxCtrl();
            loadPathTextBox.setCtrl(false);
            savePathTextBox.setCtrl(false);
        }
    }

    private void postDraw() {
        pMousePressed = mousePressed;
        pENTER = key == ENTER;
    }

    public static void main(String[] args) {
        PApplet.main("Main");
    }
}