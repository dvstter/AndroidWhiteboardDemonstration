package com.example.heminghang.hmhwhiteboard.drawings.brush;

import android.graphics.Path;
import java.lang.*;

public class CircleBrush extends Brush{

    float centerX;
    float centerY;
    int radius;

    @Override
    public void mouseMove(Path path, float x, float y) {
        mouseUp(path, x, y);
    }

    @Override
    public void mouseDown(Path path, float x, float y) {
        centerX = x;
        centerY = y;
    }

    @Override
    public void mouseUp(Path path, float x, float y) {
        radius = (int)Math.sqrt((centerX-x)*(centerX-x)+(centerY-y)*(centerY-y));
        path.addCircle(centerX, centerY, radius, Path.Direction.CW);
    }
}
