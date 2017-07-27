package com.example.heminghang.hmhwhiteboard.drawings.brush;

import android.graphics.Path;

/**
 * Created by yanghanlin on 24/05/2017.
 */

public class RectBrush extends Brush {
    private float originX;
    private float originY;

    @Override
    public void mouseDown(Path path, float x, float y) {
        originX = x;
        originY = y;
    }

    @Override
    public void mouseMove(Path path, float x, float y) {
        mouseUp(path, x, y);
    }

    @Override
    public void mouseUp(Path path, float x, float y) {
        path.moveTo(originX, originY);
        path.lineTo(originX, y);

        path.moveTo(originX, originY);
        path.lineTo(x, originY);

        path.moveTo(x, y);
        path.lineTo(originX, y);

        path.moveTo(x, y);
        path.lineTo(x, originY);
    }
}
