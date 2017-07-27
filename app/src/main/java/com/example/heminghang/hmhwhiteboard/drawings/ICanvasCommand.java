package com.example.heminghang.hmhwhiteboard.drawings;


import android.graphics.Canvas;

public interface ICanvasCommand {
    public void draw(Canvas canvas);
    public void undo();
}
