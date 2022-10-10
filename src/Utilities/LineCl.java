package Utilities;

import processing.core.PVector;

public class LineCl {

    PVector pos1;
    PVector pos2;

    public LineCl(PVector pos1, PVector pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

    public PVector getPos1() {
        return pos1;
    }

    public void setPos1(PVector pos1) {
        this.pos1 = pos1;
    }

    public PVector getPos2() {
        return pos2;
    }

    public void setPos2(PVector pos2) {
        this.pos2 = pos2;
    }
}
