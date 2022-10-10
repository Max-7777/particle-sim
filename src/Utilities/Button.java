package Utilities;

import processing.core.PApplet;
import processing.core.PVector;

public class Button {

    PVector pos;
    float width, height;
    boolean hover, clicked;
    PVector pScreenSize;
    String text;
    PVector tOffset;

    public Button(PVector screenSize, PVector pos, float width, float height, String text) {
        this.pos = pos;
        this.width = width;
        this.height = height;
        this.hover = false;
        this.clicked = false;
        this.pScreenSize = screenSize;
        this.text = text;
        this.tOffset = new PVector(15,17);
    }

    public Button(PVector screenSize, float x, float y, float width, float height) {
        this.pos = new PVector(x,y);
        this.width = width;
        this.height = height;
        this.hover = false;
        this.clicked = false;
        this.pScreenSize = screenSize;
    }


    public void update(PVector screenSize, PVector mousePos, boolean mousePressed, boolean pMousePressed) {
        boolean collision = Collisions.isTouching(mousePos, this.pos, this.width, this.height);
        this.hover = collision;

        if (this.clicked) clicked = false;
        if (!pMousePressed && mousePressed && collision) clicked = true;

        this.pos = new PVector(this.pos.x * screenSize.x/pScreenSize.x,this.pos.y * screenSize.y/pScreenSize.y);
        this.tOffset = new PVector(this.tOffset.x * screenSize.x/pScreenSize.x,
                this.tOffset.y * screenSize.y/pScreenSize.y);
        this.width = width * (screenSize.x/pScreenSize.x);
        this.height = height * (screenSize.y/pScreenSize.y);
        pScreenSize = screenSize.copy();
    }

    public void draw(PApplet s, int style, PVector screenSize) {
        if (style == 0) draw(s, screenSize);
        if (style == 1) {
            s.noStroke();
            s.fill(255,200);
            if (this.hover) s.fill(255,120);
            if (this.clicked || (this.hover && s.mousePressed)) s.fill(255,100);
            s.rect(this.pos.x,this.pos.y,this.width,this.height,5);
        }
        else {
            draw(s, screenSize);
        }
    }

    public void draw(PApplet s, PVector screenSize) {
        s.noStroke();
        s.fill(255,0);
        if (this.hover) s.fill(255,50);
        if (this.clicked || (this.hover && s.mousePressed)) s.fill(255,150);
        s.rect(this.pos.x,this.pos.y,this.width,this.height,5);
        s.fill(255);
        s.text(text, this.pos.x + this.tOffset.x, this.pos.y + this.tOffset.y);
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isHover() {
        return hover;
    }

    public void setHover(boolean hover) {
        this.hover = hover;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
