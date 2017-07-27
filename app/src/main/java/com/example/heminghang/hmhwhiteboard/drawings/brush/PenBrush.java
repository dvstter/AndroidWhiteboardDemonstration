package com.example.heminghang.hmhwhiteboard.drawings.brush;

import android.graphics.Path;

public class PenBrush extends Brush{
    @Override
    public void mouseDown(Path path, float x, float y) {
        path.moveTo( x, y );
        path.lineTo(x, y);
    }

    @Override
    public void mouseMove(Path path, float x, float y) {
        path.lineTo( x, y );
    }

    @Override
    public void mouseUp(Path path, float x, float y) {
        path.lineTo( x, y );
    }
}
