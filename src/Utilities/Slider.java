package Utilities;

import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.*;

public class Slider {

    PVector pos;
    float width;
    float padding;
    float value;
    float sliderPos;
    PVector range;
    boolean dragging;
    boolean vertical;
    PVector pScreenSize;

    public Slider(PVector screenSize, PVector pos, float width, float value, PVector range) {
        this.pos = pos;
        this.width = width;
        this.padding = 10;
        this.value = value;
        this.sliderPos = this.pos.x;
        this.range = range;
        this.dragging = false;
        this.vertical = false;
        this.pScreenSize = screenSize;
    }

    public Slider(PVector screenSize, PVector pos, float width, float value, PVector range, boolean vertical) {
        this.pos = pos;
        this.width = width;
        this.padding = 10;
        this.value = value;
        this.sliderPos = PApplet.map(value,range.y,range.x,pos.y,pos.y+width);
        this.range = range;
        this.dragging = false;
        this.vertical = vertical;
        this.pScreenSize = screenSize;
    }

    public void update(PVector screenSize, PVector mousePos, boolean mousePressed, boolean pMousePressed) {
        PVector TL;
        PVector BR;

        if (pMousePressed && !mousePressed) dragging = false;

        if (!vertical) {
            TL = new PVector(this.pos.x - padding,this.pos.y - padding);
            BR = new PVector(this.pos.x + this.width + padding,this.pos.y + padding);

            if (dragging) {
                sliderPos = Math.max(this.pos.x, Math.min(this.pos.x+this.width, mousePos.x));
                value = PApplet.map(sliderPos,this.pos.x,this.pos.x+this.width,range.x,range.y);
            }
        } else {
            TL = new PVector(this.pos.x - padding,this.pos.y - padding);
            BR = new PVector(this.pos.x + padding,this.pos.y + this.width + padding);

            if (dragging) {
                sliderPos = Math.max(this.pos.y, Math.min(this.pos.y + this.width, mousePos.y));
                value = PApplet.map(sliderPos,this.pos.y+this.width,this.pos.y,range.y,range.x);
            }
        }

        this.pos = new PVector(this.pos.x * (screenSize.x/pScreenSize.x),this.pos.y * (screenSize.y/pScreenSize.y));
        this.width = width * (screenSize.x/pScreenSize.x);
        pScreenSize = screenSize.copy();
        if (!vertical) {
            sliderPos = PApplet.map(this.value, this.range.x, this.range.y, this.pos.x,this.pos.x + this.width);
        } else {
            sliderPos = PApplet.map(this.value, this.range.x, this.range.y, this.pos.y,this.pos.y + this.width);
        }

        if (!Collisions.isPointInsideRect(mousePos, new PVector(TL.x + 3, TL.y),
                new PVector(BR.x - 3, BR.y))) return;

        if (!pMousePressed && mousePressed) dragging = true;
    }

    public void draw(PApplet s, int style) {
        s.noStroke();
        if (style == 0) draw(s);
        if (style == 1) {
            if (this.vertical) {
                s.fill(255,10);
                s.rectMode(CORNER);
                s.rect(this.pos.x-3,this.pos.y,6,this.width);
                s.fill(255,(this.dragging) ? 255 : 150);
                s.ellipseMode(CENTER);
                s.ellipse(this.pos.x,this.sliderPos,5,5);
            } else {
                s.noStroke();
                s.fill(255,10);
                s.rect(this.pos.x,this.pos.y-3,this.width,6);
                s.fill(255,(this.dragging) ? 255 : 150);
                s.ellipseMode(CENTER);
                s.ellipse(this.sliderPos,this.pos.y,12,12);
            }
            s.ellipseMode(CORNER);
        } else {
            draw(s);
        }
    }

    public void draw(PApplet s) {
        s.noStroke();
        if (this.vertical) {
            s.fill(255,30);
            s.rectMode(CORNER);
            s.rect(this.pos.x-3,this.pos.y,6,this.width);
            s.rectMode(CENTER);
            s.noStroke();
            s.fill(255,(this.dragging) ? 255 : 150);
            s.rect(this.pos.x,this.sliderPos,12,6);
        } else {
            s.noStroke();
            s.fill(255,10);
            s.rect(this.pos.x,this.pos.y-3,this.width,6);
            s.fill(255,(this.dragging) ? 255 : 150);
            s.rectMode(CENTER);
            s.rect(this.sliderPos,this.pos.y,8,12);
        }
        s.rectMode(CORNER);
    }


    public void printValue(PApplet s, PVector pos) {
        s.fill(255);
        s.noStroke();
        s.text(this.value,pos.x,pos.y);
    }

    public void printValue(PApplet s) {
        s.fill(255);
        s.noStroke();
        s.text(this.value,this.pos.x - 20,this.pos.y);
    }

    public void printValue(PApplet s, float x, float y) {
        s.fill(255);
        s.noStroke();
        s.text(this.value,x,y);
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public PVector getRange() {
        return range;
    }

    public void setRange(PVector range) {
        this.range = range;
    }

    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public float getSliderPos() {
        return sliderPos;
    }

    public void setSliderPos(float sliderPos) {
        this.sliderPos = sliderPos;
    }
}
