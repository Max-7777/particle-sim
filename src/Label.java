import processing.core.PApplet;
import processing.core.PVector;

public class Label {
    PVector pos;
    PVector screenSize;
    String text;
    PApplet sketch;
    int textSize;

    public Label(PVector pos, PVector screenSize, String text, PApplet sketch, int textSize) {
        this.pos = pos;
        this.screenSize = screenSize;
        this.text = text;
        this.sketch = sketch;
        this.textSize = textSize;
    }

    public void update(PVector screenSize) {
        float newX = this.pos.x * (screenSize.x / this.screenSize.x);
        float newY = this.pos.y * (screenSize.y / this.screenSize.y);
        float newTextSize = this.textSize * (screenSize.x / this.screenSize.x);
        this.pos = new PVector(newX, newY);
        this.screenSize = screenSize.copy();
        this.textSize = (int) newTextSize;
    }

    public void draw() {
        sketch.textSize(this.textSize);
        sketch.fill(255);
        sketch.noStroke();
        sketch.text(this.text, this.pos.x, this.pos.y);
    }
}
