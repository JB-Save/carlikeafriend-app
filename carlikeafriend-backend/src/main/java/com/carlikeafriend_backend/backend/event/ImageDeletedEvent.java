package com.carlikeafriend_backend.backend.event;

public class ImageDeletedEvent {
    private final String imagePath;

    public ImageDeletedEvent(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
