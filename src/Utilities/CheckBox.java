package Utilities;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.Objects;

public class CheckBox {

    PVector pos;
    boolean value;
    float size;
    PImage sprite, checked, unchecked;
    Color tint;
    String title;
    PVector pScreenSize;
    PApplet sketch;

    public CheckBox(PVector screenSize, PApplet sketch, PVector pos, boolean value, float size, Color tint) {
        this.pos = pos;
        this.value = value;
        this.size = size;
        this.sketch = sketch;
        this.checked = sketch.loadImage("src\\Utilities\\CheckBoxSprites\\WhiteChecked.png");
        this.unchecked = sketch.loadImage("src\\Utilities\\CheckBoxSprites\\WhiteUnchecked.png");
        this.sprite = (value) ? checked : unchecked;
        this.tint = tint;
        this.title = "";
        this.pScreenSize = screenSize.copy();
    }

    public CheckBox(PVector screenSize, PApplet sketch, PVector pos, boolean value, float size, Color tint, String title) {
        this.pos = pos;
        this.value = value;
        this.size = size;
        this.sketch = sketch;
        this.checked = sketch.loadImage("src\\Utilities\\CheckBoxSprites\\WhiteChecked.png");
        this.unchecked = sketch.loadImage("src\\Utilities\\CheckBoxSprites\\WhiteUnchecked.png");
        this.sprite = (value) ? checked : unchecked;
        this.tint = tint;
        this.title = title;
        this.pScreenSize = screenSize.copy();
    }


    public void update(PVector screenSize, PVector mousePos, boolean mousePressed, boolean pMousePressed) {
        boolean collision = Collisions.isTouching(mousePos,this.pos,this.size,this.size);
        if (mousePressed && !pMousePressed && collision) this.value = !this.value;
        if (this.value) this.sprite = checked;
        if (!this.value) this.sprite = unchecked;

        this.pos = new PVector(this.pos.x * screenSize.x/pScreenSize.x,this.pos.y * screenSize.y/pScreenSize.y);
//        debug
        this.size *= ((screenSize.x+screenSize.y)/(pScreenSize.x+pScreenSize.y));
        pScreenSize = screenSize.copy();
    }

    public void draw() {
        this.sketch.tint(this.tint.getRGB());
        this.sketch.image(this.sprite, this.pos.x + 4, this.pos.y + 4, this.size, this.size);
        if (!Objects.equals(this.title, "")) {
            this.sketch.textAlign(this.sketch.RIGHT, this.sketch.CENTER);
            this.sketch.textSize(this.size);
            this.sketch.fill(this.tint.getRGB());
            this.sketch.text(this.title,this.pos.x - (this.size*0.6f),this.pos.y + (this.size*0.5f));
            this.sketch.textAlign(this.sketch.LEFT,this.sketch.TOP);
        }
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public PImage getSprite() {
        return sprite;
    }

    public void setSprite(PImage sprite) {
        this.sprite = sprite;
    }

    public PImage getChecked() {
        return checked;
    }

    public void setChecked(PImage checked) {
        this.checked = checked;
    }

    public PImage getUnchecked() {
        return unchecked;
    }

    public void setUnchecked(PImage unchecked) {
        this.unchecked = unchecked;
    }

    public Color getTint() {
        return tint;
    }

    public void setTint(Color tint) {
        this.tint = tint;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PVector getpScreenSize() {
        return pScreenSize;
    }

    public void setpScreenSize(PVector pScreenSize) {
        this.pScreenSize = pScreenSize;
    }

    public PApplet getSketch() {
        return sketch;
    }

    public void setSketch(PApplet sketch) {
        this.sketch = sketch;
    }
}
