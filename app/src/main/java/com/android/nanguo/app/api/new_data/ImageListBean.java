package com.android.nanguo.app.api.new_data;

public class ImageListBean {
    private String name;
    private final int imageId;
    public ImageListBean(int imageId){
//        this.name = name;
        this.imageId = imageId;
    }
    public String getName() {
        return name;
    }
    public int getImageId() {
        return imageId;
    }

}
