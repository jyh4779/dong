package com.example.mytest;

import android.app.Application;

public class InitApplication extends Application {
    private int widthInit, heightInit;
    //레이아웃 넓이, 레이아웃 높이

    @Override
    public void onCreate() {
        widthInit = 0;
        heightInit = 0;
        super.onCreate();
    }

    public void setWidth(int width){
        this.widthInit = width;
    }

    public int getWidth(){
        return widthInit;
    }

    public void setHeight(int height){
        this.heightInit = height;
    }

    public int getHeight(){
        return heightInit;
    }
}
