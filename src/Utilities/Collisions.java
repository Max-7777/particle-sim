package Utilities;

import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;


public class Collisions extends PApplet {

    public Collisions() {}

    public static boolean isAABBCollided(PVector pos1, float width1, float height1, PVector pos2, float width2, float height2) {
        boolean xCol = false;
        boolean yCol = false;

        if ((pos1.x >= pos2.x && pos1.x <= pos2.x + width2) || (pos1.x + width1 >= pos2.x && pos1.x + width1 <= pos2.x + width2)) xCol = true;
        if ((pos1.y >= pos2.y && pos1.y <= pos2.y + height2) || (pos1.y + height1 >= pos2.y && pos1.y + height1 <= pos2.y + height2)) yCol = true;
        return xCol && yCol;
    }

    public static boolean isPointInsideRect(PVector point, PVector TL, PVector BR) {
        //convert tl and br points to min/max x and y values (because TL and BR aren't always top left and bottom right)
        float minX = Math.min(TL.x, BR.x);
        float maxX = Math.max(TL.x, BR.x);
        float minY = Math.min(TL.y, BR.y);
        float maxY = Math.max(TL.y, BR.y);

        return (point.x > minX && point.x < maxX && point.y > minY && point.y < maxY);
    }

    public static boolean isTouching(PVector point, PVector TL, float width, float height) {
        //convert tl and br points to min/max x and y values (because TL and BR aren't always top left and bottom right)
        float minX = Math.min(TL.x, TL.x+width);
        float maxX = Math.max(TL.x, TL.x+width);
        float minY = Math.min(TL.y, TL.y+height);
        float maxY = Math.max(TL.y, TL.y+height);

        return (point.x > minX && point.x < maxX && point.y > minY && point.y < maxY);
    }





    public static boolean isLinesIntersecting(PVector a, PVector b, PVector c, PVector d) {
        float denominator = ((b.x - a.x) * (d.y - c.y)) - ((b.y - a.y) * (d.x - c.x));
        float numerator1 = ((a.y - c.y) * (d.x - c.x)) - ((a.x - c.x) * (d.y - c.y));
        float numerator2 = ((a.y - c.y) * (b.x - a.x)) - ((a.x - c.x) * (b.y - a.y));

        // Detect coincident lines
        //if (denominator == 0) return numerator1 == 0 && numerator2 == 0;

        float r = numerator1 / denominator;
        float s = numerator2 / denominator;

        return (r >= 0 && r <= 1) && (s >= 0 && s <= 1);
    }

    public static boolean isRectOverlapping(PVector corner1, PVector corner2, PVector corner3, PVector corner4) {

        PVector TL1 = new PVector(Math.min(corner1.x,corner2.x),Math.min(corner1.y,corner2.y));
        PVector BR1 = new PVector(Math.max(corner1.x,corner2.x),Math.max(corner1.y,corner2.y));
        PVector TL2 = new PVector(Math.min(corner3.x,corner4.x),Math.min(corner3.y,corner4.y));
        PVector BR2 = new PVector(Math.max(corner3.x,corner4.x),Math.max(corner3.y,corner4.y));

        PVector TR1 = new PVector(BR1.x,TL1.y);
        PVector BL1 = new PVector(TR1.x, BR1.y);
        PVector TR2 = new PVector(BR2.x,TL2.y);
        PVector BL2 = new PVector(TR2.x, BR2.y);

        LineCl A1 = new LineCl(TL1, TR1);
        LineCl B1 = new LineCl(TR1, BR1);
        LineCl C1 = new LineCl(BR1, BL1);
        LineCl D1 = new LineCl(BL1, TL1);
        LineCl A2 = new LineCl(TL2, TR2);
        LineCl B2 = new LineCl(TR2, BR2);
        LineCl C2 = new LineCl(BR2, BL2);
        LineCl D2 = new LineCl(BL2, TL2);

        ArrayList<LineCl> lines = new ArrayList<>();
        lines.add(A1);
        lines.add(B1);
        lines.add(C1);
        lines.add(D1);
        lines.add(A2);
        lines.add(B2);
        lines.add(C2);
        lines.add(D2);

        boolean b3 = false;

        for (int i = 0; i < 4; i++) {
            for (int j = 4; j < 8; j++) {
                if (isLinesIntersecting(lines.get(i).pos1, lines.get(i).pos2, lines.get(j).pos1, lines.get(j).pos2)) {
                    b3 = true;
                    break;
                }
            }
        }

        boolean b1 = isAABBCollided(TL1,BR1.x - TL1.x,BR1.y-TL1.y,TL2,BR2.x - TL2.x,BR2.y-TL2.y);
        boolean b2 = isAABBCollided(TL2,BR2.x - TL2.x,BR2.y-TL2.y,TL1,BR1.x - TL1.x,BR1.y-TL1.y);

        return b1 || b2 || b3;
    }

    public static boolean isTouching(PVector pos1, PVector pos2, float width1, float width2, float height1, float height2) {

        PVector TL1 = new PVector(Math.min(pos1.x,pos1.x+width1),Math.min(pos1.y,pos1.y+height1));
        PVector BR1 = new PVector(Math.max(pos1.x,pos1.x+width1),Math.max(pos1.y,pos1.y+height1));
        PVector TL2 = new PVector(Math.min(pos2.x,pos2.x+width2),Math.min(pos2.y,pos2.y+height2));
        PVector BR2 = new PVector(Math.max(pos2.x,pos2.x+width2),Math.max(pos2.y,pos2.y+height2));

        PVector TR1 = new PVector(BR1.x,TL1.y);
        PVector BL1 = new PVector(TR1.x, BR1.y);
        PVector TR2 = new PVector(BR2.x,TL2.y);
        PVector BL2 = new PVector(TR2.x, BR2.y);

        LineCl A1 = new LineCl(TL1, TR1);
        LineCl B1 = new LineCl(TR1, BR1);
        LineCl C1 = new LineCl(BR1, BL1);
        LineCl D1 = new LineCl(BL1, TL1);
        LineCl A2 = new LineCl(TL2, TR2);
        LineCl B2 = new LineCl(TR2, BR2);
        LineCl C2 = new LineCl(BR2, BL2);
        LineCl D2 = new LineCl(BL2, TL2);

        ArrayList<LineCl> lines = new ArrayList<>();
        lines.add(A1);
        lines.add(B1);
        lines.add(C1);
        lines.add(D1);
        lines.add(A2);
        lines.add(B2);
        lines.add(C2);
        lines.add(D2);

        boolean b3 = false;

        for (int i = 0; i < 4; i++) {
            for (int j = 4; j < 8; j++) {
                if (isLinesIntersecting(lines.get(i).pos1, lines.get(i).pos2, lines.get(j).pos1, lines.get(j).pos2)) {
                    b3 = true;
                    break;
                }
            }
        }

        boolean b1 = isAABBCollided(TL1,BR1.x - TL1.x,BR1.y-TL1.y,TL2,BR2.x - TL2.x,BR2.y-TL2.y);
        boolean b2 = isAABBCollided(TL2,BR2.x - TL2.x,BR2.y-TL2.y,TL1,BR1.x - TL1.x,BR1.y-TL1.y);

        return b1 || b2 || b3;
    }

}