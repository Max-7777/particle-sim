import Utilities.CheckBox;
import Utilities.TextBox;
import processing.core.PApplet;
import processing.core.PVector;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class UIHandler {

    PVector pos;
    PVector originalScreenSize;
    Label[][] labels;
    CheckBox[][] checkBoxes;
    TextBox[][] textBoxes;
    PApplet sketch;
    float width, height;
    int rows, columns;
    int textSize;
    float[] textBoxOffsets;
    float textBoxMargin;

    public UIHandler(PVector originalScreenSize, PVector pos, int rows, int columns, float width, float height, int textSize, PApplet sketch) {
        this.pos = pos;
        this.rows = rows;
        this.columns = columns;
        this.labels = new Label[rows][columns];
        this.checkBoxes = new CheckBox[rows][columns];
        this.textBoxes = new TextBox[rows][columns];
        this.sketch = sketch;
        this.width = width;
        this.height = height;
        this.textSize = textSize;
        this.textBoxOffsets = new float[]{90,75};
        this.textBoxMargin = 2;
        this.originalScreenSize = originalScreenSize;
    }

    public void draw() {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                try {
                    this.labels[i][j].draw();
                } catch (Exception ignored) {}
                try {
                    this.checkBoxes[i][j].draw();
                } catch (Exception ignored) {}
                try {
                    this.textBoxes[i][j].draw();
                } catch (Exception ignored) {}
            }
        }
    }

    public void update(PVector screenSize, PVector mousePos, boolean mousePressed, boolean pMousePressed) {
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.columns; j++) {
                try {
                    this.labels[i][j].update(screenSize);
                } catch (Exception ignored) {}
                try {
                    this.checkBoxes[i][j].update(screenSize, mousePos, mousePressed, pMousePressed);
                } catch (Exception ignored) {}
                try {
                    this.textBoxes[i][j].update(screenSize.x, screenSize.y, mousePos, mousePressed, pMousePressed);
                } catch (Exception ignored) {}
            }
        }
    }

    public void updateTextBoxKey(int keyCode, char key) {
        for (int i = 0; i < this.textBoxes.length; i++) {
            for (int j = 0; j < this.textBoxes[0].length; j++) {
                try {
                    this.textBoxes[i][j].updateKeyPressed(keyCode, key);
                } catch (Exception ignored) {}
            }
        }
    }

    public void updateTextBoxShift() {
        for (int i = 0; i < this.textBoxes.length; i++) {
            for (int j = 0; j < this.textBoxes[0].length; j++) {
                try {
                    this.textBoxes[i][j].setShift(false);
                } catch (Exception ignored) {}
            }
        }
    }

    public void updateTextBoxCtrl() {
        for (int i = 0; i < this.textBoxes.length; i++) {
            for (int j = 0; j < this.textBoxes[0].length; j++) {
                try {
                    this.textBoxes[i][j].setCtrl(false);
                } catch (Exception ignored) {}
            }
        }
    }

    public PVector getPos(int row, int column) {
        return new PVector((column*this.width) + this.pos.x, (row*this.height) + this.pos.y);
    }

    public void saveFile(String filePath, SliderGroup sizeSliderGroup, SliderGroup opacitySliderGroup) throws IOException {
        File file = new File(filePath);
        file.createNewFile();
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (checkBoxes[i][j] == null) continue;
                bw.write(i + "," + j + "," + labels[i][j].text + "," + "checkbox" + "," + checkBoxes[i][j].isValue());
                bw.newLine();
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (textBoxes[i][j] == null) continue;
                bw.write(i + "," + j + "," + labels[i][j].text + "," + "textbox" + "," + textBoxes[i][j].getLettersString());
                bw.newLine();
            }
        }

        for (int i = 0; i < sizeSliderGroup.sliderNum; i++) {
            bw.write(String.valueOf(sizeSliderGroup.sliders[i].getValue()));
            bw.newLine();
        }

        for (int i = 0; i < opacitySliderGroup.sliderNum; i++) {
            bw.write(String.valueOf(opacitySliderGroup.sliders[i].getValue()));
            bw.newLine();
        }

        bw.close();
    }

    public boolean loadFile(String filePath, PVector screenSize) {
        Scanner sc = new Scanner(System.in);
        File file = new File(filePath);

        try {
            sc = new Scanner(file);
        } catch (Exception ignored) {
            System.out.println("load fail: " + file.getPath());
            return false;
        }

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("")) continue;

            String[] lineArray = line.split(",",5);

            if (lineArray.length <= 1) continue;

            PVector pos = getPos(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[1]));
            int row = Integer.parseInt(lineArray[0]);
            int column = Integer.parseInt(lineArray[1]);

            this.labels[row][column] = new Label(pos, this.originalScreenSize, lineArray[2], this.sketch, this.textSize);

            //only label needed
            if (lineArray.length < 4) continue;

            switch (lineArray[3].toLowerCase(Locale.ROOT)) {
                case "checkbox" -> {
                    float xOffset = (this.sketch.textWidth(this.labels[row][column].text) + 5) * (this.originalScreenSize.x / screenSize.x);

                    this.checkBoxes[row][column] = new CheckBox(this.originalScreenSize, sketch,
                            new PVector(getPos(row, column).x + xOffset, getPos(row, column).y - textSize*1.1f),
                            Boolean.parseBoolean(lineArray[4]), textSize, new Color(255,255,255));
                }

                case "textbox" -> {
                    float xOffset = textBoxOffsets[column];

                    this.textBoxes[row][column] = new TextBox(new PVector(pos.x + xOffset, pos.y + textBoxMargin/2),
                            textSize - (textBoxMargin*2f), 50, this.originalScreenSize.x, this.originalScreenSize.y, this.sketch);
                    this.textBoxes[row][column].setLetters(lineArray[4]);
                }
            }
        }

        sc.close();

        return true;
    }

    public void loadSliderGroup(int lineNum, SliderGroup sliderGroup, int size, String filePath) {
        Scanner sc = new Scanner(System.in);
        File file = new File(filePath);

        try {
            sc = new Scanner(file);
        } catch (Exception ignored) {
            System.out.println("load slider group fail: " + file.getPath());
            return;
        }

        for (int i = 0; i < lineNum - 1; i++) {
            sc.nextLine();
        }

        for (int i = 0; i < size; i++) {
            String line = sc.nextLine();
            if (line.equals("")) continue;

            String[] lineArray = line.split(",",5);

            if (lineArray.length > 1) continue;

            float val = Float.parseFloat(lineArray[0]);
            sliderGroup.sliders[i].setValue(val);
        }

        sc.close();
    }
}