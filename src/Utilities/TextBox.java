package Utilities;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

import static processing.core.PConstants.*;

public class TextBox {

    ArrayList<Character> letters;
    String letterString;
    int cursorPos;
    int selectedOrigin;
    float textHeight;
    PApplet sketch;
    PVector pos;
    boolean shift, ctrl;
    boolean selecting;
    float boxWidth;
    boolean clickedOn;
    float pWidth, pHeight;

    public TextBox(PVector pos, float textHeight, float boxWidth, float width, float height, PApplet sketch) {
        this.letters = new ArrayList<>();
        this.letterString = "";
        this.cursorPos = -1;
        this.selectedOrigin = -1;
        this.textHeight = textHeight;
        this.sketch = sketch;
        this.pos = pos;
        this.boxWidth = boxWidth;
        this.clickedOn = false;
        this.pWidth = width;
        this.pHeight = height;
    }

    public void draw() {
        sketch.pushMatrix();
        sketch.translate(this.pos.x, this.pos.y);

        //box
        sketch.fill(0);
        sketch.stroke(255);
        sketch.rect(-5,-(textHeight*1.6f), boxWidth, textHeight*2);

        //select
        if (this.selecting) {
            sketch.fill(255);

            int indexStart = Math.min(cursorPos, selectedOrigin);
            int indexEnd = Math.max(cursorPos, selectedOrigin);

            float selectStart = (indexStart > -1) ? sketch.textWidth(letterString.substring(0, indexStart)) : -1 * sketch.textWidth(letters.get(0));
            float selectEnd = (indexEnd > -1) ? sketch.textWidth(letterString.substring(0, indexEnd)) : -1 * sketch.textWidth(letters.get(0));
            float selectWidth = selectEnd - selectStart;

            //highlight
            sketch.rect(selectStart + sketch.textWidth(letters.get(0)), -textHeight,selectWidth,textHeight);

            float width = 0;

            for (int i = 0; i < letters.size(); i++) {
                if (i > Math.min(cursorPos,selectedOrigin) && i <= Math.max(cursorPos,selectedOrigin)) {
                    sketch.fill(0);
                } else {
                    sketch.fill(255);
                }
                sketch.text(letters.get(i).toString(),width,0);
                width += sketch.textWidth(letters.get(i));
            }
        //else
        } else {
            sketch.fill(255);

            float width = 0;

            for (int i = 0; i < letters.size(); i++) {

                sketch.text(letters.get(i).toString(),width,0);
                width += sketch.textWidth(letters.get(i));
            }
        }

        //cursor line

        if (clickedOn) {
            sketch.strokeWeight(1);
            sketch.stroke(255);
            if (cursorPos == -1) {
                sketch.line(0, -(textHeight*2), 0,0);
            } else {
                float linePos = sketch.textWidth(letterString.substring(0,cursorPos)) + sketch.textWidth(letters.get(0));
                sketch.line(linePos, -(textHeight*2), linePos,0);
            }
        }


        sketch.popMatrix();
    }

    public void updateCollision(PVector mousePos, boolean mousePressed, boolean pMousePressed) {

        PVector TL = new PVector(this.pos.x, this.pos.y - (this.textHeight*2));
        PVector BR = new PVector(this.pos.x + this.boxWidth, this.pos.y);

        boolean mouseCollision = Collisions.isPointInsideRect(mousePos, TL, BR);

        if (!pMousePressed && mousePressed && mouseCollision) {
            this.clickedOn = true;
        }
        if (!pMousePressed && mousePressed && !mouseCollision) {
            this.clickedOn = false;
        }
    }

    public void update(float width, float height, PVector mousePos, boolean mousePressed, boolean pMousePressed) {
        this.updateCollision(mousePos, mousePressed, pMousePressed);

        this.pos = new PVector(this.pos.x * (width/pWidth),this.pos.y * (height/pHeight));
        this.boxWidth = boxWidth * (width/pWidth);
        this.textHeight = textHeight * (height/pHeight);
        pWidth = width;
        pHeight = height;
    }

    public void updateKeyPressed(int keyCode, char key) {
        if (!clickedOn) return;

        if (keyCode == ENTER) return;
        if (keyCode == SHIFT) shift = true;
        if (keyCode == 17) ctrl = true;

        //movement
        if (keyCode == LEFT || keyCode == RIGHT) {
            if (shift && !this.selecting) {
                selectedOrigin = cursorPos;
                this.selecting = true;
                moveCursor(keyCode);
            }
            else if ((!shift && this.selecting) || Math.min(selectedOrigin,cursorPos) >= letters.size()) {
                selectedOrigin = -1;
                this.selecting = false;
            } else {
                moveCursor(keyCode);
            }
        }
        //delete
        else if (keyCode == 8) {
            try {
                if (selectedOrigin == -1) {
                    if (!ctrl) {
                        letters.remove(cursorPos);
                        cursorPos--;
                    } else {
                        int beginning = beginningOfWord(letters,cursorPos);
                        if (beginning == -1) {
                            letters.clear();
                            cursorPos = -1;
                        } else {
                            int length = cursorPos - beginning;
                            for (int i = 0; i < length; i++) {
                                letters.remove(beginning + 1);
                            }
                            cursorPos = beginning;
                        }
                    }
                }
                else {
                    int dist = Math.abs(cursorPos - selectedOrigin);
                    for (int i = 0; i < dist; i++) {
                        letters.remove(Math.min(cursorPos,selectedOrigin) + 1);
                    }
                    cursorPos = Math.min(cursorPos,selectedOrigin);
                    selectedOrigin = -1;
                    this.selecting = false;
                }
            } catch (Exception ignored) {}

            //type letters
        }
        //ctrl 'a' command
        else if (keyCode == 65) {
            if (ctrl) {
                cursorPos = -1;
                this.selecting = true;
                selectedOrigin = letters.size() - 1;
            } else {
                letters.add(cursorPos+1,key);
                cursorPos++;
            }
        }
        else {
            if (!ctrl && keyCode != 16) {
                if (selectedOrigin != -1 && this.selecting) {
                    int dist = Math.abs(cursorPos - selectedOrigin);
                    for (int i = 0; i < dist; i++) {
                        letters.remove(Math.min(cursorPos,selectedOrigin) + 1);
                    }
                    cursorPos = Math.min(cursorPos,selectedOrigin);
                    selectedOrigin = -1;
                    this.selecting = false;
                }
                letters.add(cursorPos+1,key);
                cursorPos++;
            }
        }

        StringBuilder sb = new StringBuilder();

        for (Character c : letters) {
            sb.append(c);
        }

        this.letterString = sb.toString();
    }

    public int beginningOfWord(ArrayList<Character> letters, int cursorPos) {
        int o = -1;

        for (int i = 0; i < cursorPos; i++) {
            if (letters.get(i) == ' ') o = i;
        }

        return o;
    }

    public int endOfWord(ArrayList<Character> letters, int cursorPos) {
        int o = letters.size() - 1;

        for (int i = letters.size() - 1; i > cursorPos; i--) {
            if (letters.get(i) == ' ') o = i;
        }

        return o;
    }


    private void moveCursor(int keyCode) {
        switch (keyCode) {
            case RIGHT -> {
                if (cursorPos != letters.size() - 1) {
                    if (ctrl) {
                        cursorPos = endOfWord(letters, cursorPos);
                    } else {
                        cursorPos++;
                    }
                }
            }
            case LEFT -> {
                if (cursorPos != -1) {
                    if (ctrl) {
                        cursorPos = beginningOfWord(letters, cursorPos);
                    } else {
                        cursorPos--;
                    }
                }
            }
        }
    }

    public void makeEmpty() {
        this.setLetterString("");
        this.letters = new ArrayList<>();
        this.cursorPos = -1;
    }

    public ArrayList<Character> getLetters() {
        return letters;
    }

    public void setLetters(ArrayList<Character> letters) {
        this.letters = letters;
    }

    public String getLetterString() {
        return letterString;
    }

    public void setLetterString(String letterString) {
        this.letterString = letterString;
    }

    public int getCursorPos() {
        return cursorPos;
    }

    public void setCursorPos(int cursorPos) {
        this.cursorPos = cursorPos;
    }

    public int getSelectedOrigin() {
        return selectedOrigin;
    }

    public void setSelectedOrigin(int selectedOrigin) {
        this.selectedOrigin = selectedOrigin;
    }

    public float getTextHeight() {
        return textHeight;
    }

    public void setTextHeight(float textHeight) {
        this.textHeight = textHeight;
    }

    public PApplet getSketch() {
        return sketch;
    }

    public void setSketch(PApplet sketch) {
        this.sketch = sketch;
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    public boolean isCtrl() {
        return ctrl;
    }

    public void setCtrl(boolean ctrl) {
        this.ctrl = ctrl;
    }

    public boolean isSelecting() {
        return selecting;
    }

    public void setSelecting(boolean selecting) {
        this.selecting = selecting;
    }

    public float getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(float boxWidth) {
        this.boxWidth = boxWidth;
    }

    public boolean isClickedOn() {
        return clickedOn;
    }

    public void setClickedOn(boolean clickedOn) {
        this.clickedOn = clickedOn;
    }

    public float getpWidth() {
        return pWidth;
    }

    public void setpWidth(float pWidth) {
        this.pWidth = pWidth;
    }

    public float getpHeight() {
        return pHeight;
    }

    public void setpHeight(float pHeight) {
        this.pHeight = pHeight;
    }

    public void setLetters(String s) {
        for (int i = 0; i < s.length(); i++) {
            this.letters.add(s.charAt(i));
        }
    }

    public String getLettersString() {
        StringBuilder o = new StringBuilder();
        for (int i = 0; i < letters.size(); i++) {
            o.append(letters.get(i));
        }

        return o.toString();
    }
}
