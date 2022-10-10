import Utilities.Time;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;

public class Particle {

    PVector offset, pos, particleVel, acc;
    ParticleShape shape;
    float size;
    float particleVelFriction;
    boolean first;
    float rotate, rotateFriction, angle;
    Color color;
    float lifeTime, initialTime;
    boolean dead;
    float NANO_TO_MILLI = 0.000001f;
    ArrayList<Float> sizeList, opacityList;
    float opacity;
    PImage sprite;

    public Particle(PVector offset, PVector particleVel, float size,
                    PVector force, ParticleShape shape, float particleVelFriction,
                    float rotate, float rotateFriction, Color color, float lifeTime,
                    ArrayList<Float> sizeList, ArrayList<Float> opacityList, float opacity,
                    PImage sprite) {
        this.pos = new PVector(0,0);
        this.offset = offset;
        this.particleVel = new PVector(particleVel.x, particleVel.y*-1);
        this.acc = force.copy();
        this.size = size;
        this.shape = shape;
        this.particleVelFriction = particleVelFriction;
        this.first = true;
        this.rotate = rotate;
        this.rotateFriction = rotateFriction;
        this.angle = 0;
        this.color = color;
        this.lifeTime = lifeTime;
        this.initialTime = System.nanoTime()*NANO_TO_MILLI;
        this.dead = false;
        this.sizeList = sizeList;
        this.opacityList = opacityList;
        this.opacity = opacity;
        this.sprite = sprite;
    }

    public void update(Time time) {
        if (!first) {
            this.particleVel.add(new PVector((float) (this.acc.x * time.getDeltaTime()),
                    (float) (this.acc.y * time.getDeltaTime())));
            this.particleVel.mult(this.particleVelFriction);

            this.pos.add(new PVector((float) (this.particleVel.x * time.getDeltaTime()),
                    (float) (this.particleVel.y * time.getDeltaTime())));

            this.angle += rotate * time.getDeltaTime();
            this.rotate *= rotateFriction;

            if (System.nanoTime()*NANO_TO_MILLI - this.initialTime >= this.lifeTime) this.dead = true;
        } else {
            first = false;
        }
    }

    private float interpolate(ArrayList<Float> list, float input, boolean paused) {
        if (paused) return input;
        float proportion = (System.nanoTime()*NANO_TO_MILLI - this.initialTime) / this.lifeTime;
        if (proportion >= 1) return 0;
        int min = (int) Math.floor(proportion*10);
        int max = (int) Math.ceil(proportion*10);
        float diff = list.get(max) - list.get(min);

        return (((proportion * 10) % 1) * diff + list.get(min)) * input;
    }

    public void draw(PApplet sketch, boolean paused) {
        sketch.noStroke();
        sketch.rectMode(PConstants.CENTER);
        sketch.pushMatrix();

        float drawSize = interpolate(this.sizeList, this.size, paused);
        float drawOpacity = interpolate(this.opacityList, this.opacity, paused);
        sketch.fill(this.color.getRGB(), drawOpacity);
        sketch.tint(this.color.getRGB(), drawOpacity);

        sketch.translate(this.offset.x + this.pos.x, this.offset.y + this.pos.y);
        sketch.rotate(this.angle);

        if (this.sprite == null) {
            switch (this.shape) {
                case ELLIPSE -> sketch.ellipse(0,0, drawSize, drawSize);
                case SQUARE -> sketch.rect(0, 0, drawSize, drawSize);
                case SPRITE -> {
                    try {
                        sketch.image(this.sprite, drawSize*-0.5f,drawSize*-0.5f, drawSize, drawSize);
                    } catch (Exception ignored) {}
                }
            }
        } else {
            try {
                sketch.image(this.sprite, drawSize*-0.5f,drawSize*-0.5f, drawSize, drawSize);
            } catch (Exception ignored) {}
        }



        sketch.popMatrix();
        sketch.rectMode(PConstants.CORNER);
    }

    public PVector getOffset() {
        return offset;
    }

    public void setOffset(PVector offset) {
        this.offset = offset;
    }

    public PVector getPos() {
        return pos;
    }

    public void setPos(PVector pos) {
        this.pos = pos;
    }

    public PVector getparticleVel() {
        return particleVel;
    }

    public void setparticleVel(PVector particleVel) {
        this.particleVel = particleVel;
    }

    public PVector getAcc() {
        return acc;
    }

    public void setAcc(PVector acc) {
        this.acc = acc;
    }

    public ParticleShape getShape() {
        return shape;
    }

    public void setShape(ParticleShape shape) {
        this.shape = shape;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getparticleVelFriction() {
        return particleVelFriction;
    }

    public void setparticleVelFriction(float particleVelFriction) {
        this.particleVelFriction = particleVelFriction;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public float getRotate() {
        return rotate;
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
    }

    public float getRotateFriction() {
        return rotateFriction;
    }

    public void setRotateFriction(float rotateFriction) {
        this.rotateFriction = rotateFriction;
    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(float initialTime) {
        this.initialTime = initialTime;
    }

    public float getNANO_TO_MILLI() {
        return NANO_TO_MILLI;
    }

    public void setNANO_TO_MILLI(float NANO_TO_MILLI) {
        this.NANO_TO_MILLI = NANO_TO_MILLI;
    }

    public float getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(float lifeTime) {
        this.lifeTime = lifeTime;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}