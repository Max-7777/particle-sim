package Utilities;

public class Time {
    double deltaTime;
    long pnt;

    public Time() {
        this.pnt = System.nanoTime();
    }

    public void update() {
        this.deltaTime = (System.nanoTime() - pnt)*0.000000001;
        pnt = System.nanoTime();
    }

    public double getDeltaTime() {
        return deltaTime;
    }
}
