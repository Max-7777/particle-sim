import Utilities.Slider;
import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.Arrays;

public class SliderGroup {

    PVector pos, size;
    int sliderNum;
    float padding;
    float sliderWidth;
    Slider[] sliders;
    PApplet sketch;

    public SliderGroup(PVector pos, PVector size, int sliderNum, float padding, PVector screenSize, PApplet sketch) {
        this.pos = pos;
        this.size = size;
        this.sliderNum = sliderNum;
        this.padding = padding;
        this.sliderWidth = (this.size.x - (padding*sliderNum)) / sliderNum;
        this.sliders = new Slider[sliderNum];
        this.sketch = sketch;
        makeSliders(screenSize);
    }

    private void makeSliders(PVector screenSize) {
        for (int i = 0; i < sliderNum; i++) {
            sliders[i] = new Slider(screenSize, new PVector(this.pos.x + (sliderWidth*i) + (padding*i), this.pos.y),
                                    this.size.y, 1, new PVector(1,0), true);
        }
    }

    public void update(PVector screenSize, PVector mousePos, boolean mousePressed, boolean pMousePressed) {
        for (Slider slider : sliders) slider.update(screenSize, mousePos, mousePressed, pMousePressed);
    }

    public void draw() {
        for (Slider slider : sliders) {
            slider.draw(this.sketch);
        }
    }

    public ArrayList<Float> getValues() {
        ArrayList<Float> o = new ArrayList<>();
        for (int i = 0; i < sliders.length; i++) {
            o.add(sliders[i].getValue());
        }

        o.add(sliders[sliders.length - 1].getValue());

        return o;
    }
}