import Utilities.Time;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;

public class ParticleGenerator {

    PVector spawnPos, particleVelRange;
    float particleVelSpread, particleVelDirection;
    PVector spawnTimeRange;
    PVector force;
    float pTime, randomTime;
    float NANO_TO_MILLI = 0.000001f;
    PVector sizeRange;
    float particleVelFriction;
    ParticleShape shape;
    ArrayList<Particle> particles;
    Time time;
    PApplet sketch;
    boolean pulse;
    float particleNum;
    float resetTime;
    float rotate, rotateFriction;
    Color color, colorRange;
    float lifeTime;
    ArrayList<Float> sizeList;
    ArrayList<Float> opacityList;
    float opacity;
    String spritePath;
    PImage sprite;
    SpawnShape spawnShape;
    PVector spawnShapeSize;
    PVector originSpawnPos;

    public ParticleGenerator(UIHandler uiHandler, PApplet sketch, Time time) {
        this.particles = new ArrayList<>();
        this.sketch = sketch;
        this.pTime = System.nanoTime()*NANO_TO_MILLI;
        this.time = time;
        this.spawnShape = SpawnShape.RECT;

        try {
            String[] a = uiHandler.textBoxes[5][0].getLettersString().split(",");
            this.spawnTimeRange = new PVector(Float.parseFloat(a[0]), Float.parseFloat(a[1]));
        } catch (Exception ignored) {}
    }
    public ParticleGenerator(PVector spawnPos, ParticleShape shape, PVector force, PVector sizeRange,
                             PVector particleVelRange, float particleVelDirection, float particleVelSpread,
                             PVector spawnTimeRange, Time time, PApplet sketch, boolean pulse,
                             float particleNum, float resetTime, float particleVelFriction, float rotate,
                             float rotateFriction, Color color, Color colorRange, float lifeTime,
                             ArrayList<Float> sizeList, ArrayList<Float> opacityList, Float opacity, String spritePath,
                             SpawnShape spawnShape, PVector spawnShapeSize) {
        this.time = time;
        this.sketch = sketch;
        setVar(spawnPos, shape, force,
                sizeRange, particleVelRange,
                particleVelDirection, particleVelSpread, spawnTimeRange,
                pulse, particleNum, resetTime, particleVelFriction,
                rotate, rotateFriction, color, colorRange, lifeTime,
                sizeList, opacityList, opacity, spritePath, spawnShape,
                spawnShapeSize);
    }

    private void setVar(PVector spawnPos, ParticleShape shape, PVector force, PVector sizeRange,
                        PVector particleVelRange, float particleVelDirection, float particleVelSpread,
                        PVector spawnTimeRange, boolean pulse, float particleNum, float resetTime,
                        float particleVelFriction, float rotate, float rotateFriction, Color color,
                        Color colorRange, float lifeTime, ArrayList<Float> sizeList, ArrayList<Float> opacityList,
                        Float opacity, String spritePath, SpawnShape spawnShape, PVector spawnShapeSize) {
        this.spawnPos = spawnPos;
        this.particleVelRange = particleVelRange;
        this.particleVelDirection = particleVelDirection;
        this.particleVelSpread = particleVelSpread;
        this.spawnTimeRange = spawnTimeRange;
        this.force = force;
        this.randomTime = getRandom(this.spawnTimeRange);
        this.particles = new ArrayList<>();
        this.pTime = System.nanoTime()*NANO_TO_MILLI;
        this.sizeRange = sizeRange;
        this.shape = shape;
        this.pulse = pulse;
        this.particleNum = particleNum;
        this.resetTime = resetTime;
        this.particleVelFriction = particleVelFriction;
        this.rotate = rotate;
        this.rotateFriction = rotateFriction;
        this.color = color;
        this.colorRange = colorRange;
        this.lifeTime = lifeTime;
        this.sizeList = sizeList;
        this.opacityList = opacityList;
        this.opacity = opacity;
        this.spritePath = spritePath;
        this.sprite = sketch.loadImage(spritePath);
        this.spawnShape = spawnShape;
        this.spawnShapeSize = spawnShapeSize;
        this.originSpawnPos = spawnPos.copy();
    }


    public void update() {
        if (this.pulse) {
            if (System.nanoTime()*NANO_TO_MILLI - pTime >= this.resetTime) {
                this.pTime = System.nanoTime()*NANO_TO_MILLI;
                spawnParticles(pulse);
            }
        } else {
            if (System.nanoTime()*NANO_TO_MILLI - pTime >= this.randomTime) {
                float diff = System.nanoTime()*NANO_TO_MILLI - pTime;
                int spawnCount = (int) (diff / this.randomTime);

                pTime = System.nanoTime()*NANO_TO_MILLI;
                this.randomTime = getRandom(this.spawnTimeRange);

                for (int i = 0; i < spawnCount; i++) spawnParticle();
            }
        }

        updateParticles();
    }

    private void updateParticles() {
        ArrayList<Particle> toRemove = new ArrayList<>();
        for (Particle p : this.particles)  {
            p.update(this.time);
            cullParticle(toRemove, p);
        }

        for (Particle p : toRemove) this.particles.remove(p);
    }

    private void cullParticle(ArrayList<Particle> toRemove, Particle p) {
        if (p.isDead()) toRemove.add(p);
    }

    private void spawnParticle() {
        PVector rspawnPos = getRandomspawnPos();
        PVector rparticleVel = getRandomparticleVel();
        float rSize = getRandomSize();

        this.particles.add(new Particle(rspawnPos, rparticleVel, rSize,
                this.force, this.shape, this.particleVelFriction,
                this.rotate, this.rotateFriction, getRandomColor(),
                this.lifeTime, this.sizeList, this.opacityList,
                this.opacity, this.sprite));

    }

    private void spawnParticles(boolean pulse) {
        if (!pulse) return;

        for (int i = 0; i < this.particleNum; i++) {
            PVector rspawnPos = getRandomspawnPos();
            PVector rparticleVel = getRandomparticleVel();
            float rSize = getRandomSize();

            this.particles.add(new Particle(rspawnPos, rparticleVel, rSize,
                    this.force, this.shape, this.particleVelFriction,
                    this.rotate, this.rotateFriction, getRandomColor(),
                    this.lifeTime, this.sizeList, this.opacityList,
                    this.opacity, this.sprite));
        }
    }

    private PVector getRandomspawnPos() {
        if (this.spawnShape == SpawnShape.RECT) {
            float rX = (float) (Math.random()*(this.spawnShapeSize.x + 1)) + (this.spawnPos.x - (this.spawnShapeSize.x*0.5f));
            float rY = (float) (Math.random()*(this.spawnShapeSize.y + 1)) + (this.spawnPos.y - (this.spawnShapeSize.y*0.5f));
            return new PVector(rX, rY);
        }
        else if (this.spawnShape == SpawnShape.ELLIPSE) {
            float rTheta = getRandom(0, (float) Math.PI*2);
            float a = Math.max(this.spawnShapeSize.x*0.5f, this.spawnShapeSize.y*0.5f);
            float b = Math.min(this.spawnShapeSize.x*0.5f, this.spawnShapeSize.y*0.5f);
            float radius = (float) ((a*b) /
                    Math.sqrt((a*a*Math.sin(rTheta)*Math.sin(rTheta)) + (b*b*Math.cos(rTheta)*Math.cos(rTheta))));
            float rRadius = (float) (Math.random()*radius);
            PVector rSpawnPos = polarToCartesian(new PVector(rRadius, rTheta));
            return new PVector(this.spawnPos.x + rSpawnPos.x, this.spawnPos.y + rSpawnPos.y);
        }
        return new PVector(0,0);
    }

    private float getRandomSize() {
        return getRandom(sizeRange);
    }

    private PVector getRandomparticleVel() {
        float rRadius, rTheta;

        rRadius = getRandom(particleVelRange);
        rTheta = getRandom(this.particleVelDirection - (this.particleVelSpread*0.5f), this.particleVelDirection + (this.particleVelSpread*0.5f));
        return polarToCartesian(new PVector(rRadius, rTheta));
    }

    private Color getRandomColor() {
        int r = (int) getRandom(this.color.getRed() - (this.colorRange.getRed()*0.5f),
                this.color.getRed() + (this.colorRange.getRed()*0.5f));
        int g = (int) getRandom(this.color.getGreen() - (this.colorRange.getGreen()*0.5f),
                this.color.getGreen() + (this.colorRange.getGreen()*0.5f));
        int b = (int) getRandom(this.color.getBlue() - (this.colorRange.getBlue()*0.5f),
                this.color.getBlue() + (this.colorRange.getBlue()*0.5f));
        r = Math.max(Math.min(r, 255),0);
        g = Math.max(Math.min(g, 255),0);
        b = Math.max(Math.min(b, 255),0);

        return new Color(r,g,b);
    }

    public void update(PVector mousePos, boolean followMouse, boolean editingSpritePath, UIHandler uiHandler, ArrayList<Float> lifeTimeSize, ArrayList<Float> lifeTimeOpacity, String pSpritePath) {
        String[] a;
        try {
            if (!followMouse) {
                a = uiHandler.textBoxes[3][0].getLettersString().split(",");
                this.spawnPos = new PVector(Float.parseFloat(a[0]), Float.parseFloat(a[1]));
            } else {
                this.spawnPos = mousePos.copy();
            }
        } catch (Exception ignored) {}
        try {
            a = uiHandler.textBoxes[4][0].getLettersString().split(",");
            this.spawnShapeSize = new PVector(Float.parseFloat(a[0]), Float.parseFloat(a[1]));
        } catch (Exception ignored) {}
        try {
            a = uiHandler.textBoxes[5][0].getLettersString().split(",");
            this.spawnTimeRange = new PVector(Float.parseFloat(a[0]), Float.parseFloat(a[1]));
        } catch (Exception ignored) {}
        try {
            this.lifeTime = Float.parseFloat(uiHandler.textBoxes[6][0].getLettersString());
        } catch (Exception ignored) {}
        try {
            this.pulse = uiHandler.checkBoxes[7][0].isValue();
        } catch (Exception ignored) {}
        try {
            this.resetTime = Float.parseFloat(uiHandler.textBoxes[8][0].getLettersString());
        } catch (Exception ignored) {}
        try {
            this.particleNum = Float.parseFloat(uiHandler.textBoxes[9][0].getLettersString());
        } catch (Exception ignored) {}
//        try {
//            this.shape = ParticleShape.valueOf(uiHandler.textBoxes[11][0].getLettersString().toUpperCase(Locale.ROOT));
//        } catch (Exception ignored) {}
        this.shape = ParticleShape.ELLIPSE;
        try {
            a = uiHandler.textBoxes[12][0].getLettersString().split(",");
            this.sizeRange = new PVector(Float.parseFloat(a[0]), Float.parseFloat(a[1]));
        } catch (Exception ignored) {}
        try {
            a = uiHandler.textBoxes[13][0].getLettersString().split(",");
            this.color = new Color(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
        } catch (Exception ignored) {}
        try {
            a = uiHandler.textBoxes[14][0].getLettersString().split(",");
            this.colorRange = new Color(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
        } catch (Exception ignored) {}
        try {
            this.opacity = Float.parseFloat(uiHandler.textBoxes[15][0].getLettersString());
        } catch (Exception ignored) {}
        try {
            if (editingSpritePath) {
                this.spritePath = "default/shapes/" + uiHandler.textBoxes[16][0].getLettersString();
                System.out.println(spritePath);
                this.sprite = this.sketch.loadImage(this.spritePath);
            }
        } catch (Exception ignored) {}
        try {
            a = uiHandler.textBoxes[3][1].getLettersString().split(",");
            this.particleVelRange = new PVector(Float.parseFloat(a[0]), Float.parseFloat(a[1]));
        } catch (Exception ignored) {}
        try {
            this.particleVelDirection = Float.parseFloat(uiHandler.textBoxes[4][1].getLettersString());
        } catch (Exception ignored) {}
        try {
            this.particleVelSpread = Float.parseFloat(uiHandler.textBoxes[5][1].getLettersString());
        } catch (Exception ignored) {}

        try {
            this.particleVelFriction = Float.parseFloat(uiHandler.textBoxes[6][1].getLettersString());
        } catch (Exception ignored) {}

        try {
            this.rotate = Float.parseFloat(uiHandler.textBoxes[7][1].getLettersString());
        } catch (Exception ignored) {}
        try {
            this.rotateFriction = Float.parseFloat(uiHandler.textBoxes[8][1].getLettersString());
        } catch (Exception ignored) {}
        try {
            a = uiHandler.textBoxes[9][1].getLettersString().split(",");
            this.force = new PVector(Float.parseFloat(a[0]), Float.parseFloat(a[1]));
        } catch (Exception ignored) {}

        this.sizeList = lifeTimeSize;
        this.opacityList = lifeTimeOpacity;
        this.randomTime = getRandom(this.spawnTimeRange);
    }

    private float getRandom(PVector range) {
        return (float) ((Math.random()*(range.y - range.x)) + range.x);
    }

    private float getRandom(float min, float max) {
        return (float) ((Math.random()*(max - min)) + min);
    }

    private PVector polarToCartesian(PVector spawnPos) {
        return new PVector((float) (spawnPos.x*Math.cos(spawnPos.y)), (float) (spawnPos.x*Math.sin(spawnPos.y)));
    }

    public void drawParticles(boolean paused) {
        for (Particle p : this.particles) {
            p.draw(this.sketch, paused);
        }
    }

    public PVector getSpawnPos() {
        return spawnPos;
    }

    public void setSpawnPos(PVector spawnPos) {
        this.spawnPos = spawnPos;
    }

    public PVector getSpawnTimeRange() {
        return spawnTimeRange;
    }

    public void setSpawnTimeRange(PVector spawnTimeRange) {
        this.spawnTimeRange = spawnTimeRange;
    }

    public float getpTime() {
        return pTime;
    }

    public void setpTime(float pTime) {
        this.pTime = pTime;
    }

    public float getRandomTime() {
        return randomTime;
    }

    public void setRandomTime(float randomTime) {
        this.randomTime = randomTime;
    }

    public float getNANO_TO_MILLI() {
        return NANO_TO_MILLI;
    }

    public void setNANO_TO_MILLI(float NANO_TO_MILLI) {
        this.NANO_TO_MILLI = NANO_TO_MILLI;
    }

    public ArrayList<Particle> getParticles() {
        return particles;
    }

    public void setParticles(ArrayList<Particle> particles) {
        this.particles = particles;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public PApplet getSketch() {
        return sketch;
    }

    public void setSketch(PApplet sketch) {
        this.sketch = sketch;
    }

    public PVector getparticleVelRange() {
        return particleVelRange;
    }

    public void setparticleVelRange(PVector particleVelRange) {
        this.particleVelRange = particleVelRange;
    }

    public float getparticleVelSpread() {
        return particleVelSpread;
    }

    public void setparticleVelSpread(float particleVelSpread) {
        this.particleVelSpread = particleVelSpread;
    }

    public float getparticleVelDirection() {
        return particleVelDirection;
    }

    public void setparticleVelDirection(float particleVelDirection) {
        this.particleVelDirection = particleVelDirection;
    }

    public PVector getGravity() {
        return force;
    }

    public void setGravity(PVector force) {
        this.force = force;
    }

    public float getparticleVelFriction() {
        return particleVelFriction;
    }

    public void setparticleVelFriction(float particleVelFriction) {
        this.particleVelFriction = particleVelFriction;
    }

    public ParticleShape getShape() {
        return shape;
    }

    public void setShape(ParticleShape shape) {
        this.shape = shape;
    }

    public boolean isPulse() {
        return pulse;
    }

    public void setPulse(boolean pulse) {
        this.pulse = pulse;
    }

    public float getParticleNum() {
        return particleNum;
    }

    public void setParticleNum(float particleNum) {
        this.particleNum = particleNum;
    }

    public float getResetTime() {
        return resetTime;
    }

    public void setResetTime(float resetTime) {
        this.resetTime = resetTime;
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
}
