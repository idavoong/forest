import processing.core.PImage;

import java.util.List;

public class Egg implements EntityAnimation{
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private double animationPeriod;

    public Egg(String id, Point position, List<PImage> images, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.animationPeriod = animationPeriod;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public PImage getCurrentImage() {
        return images.get(imageIndex % images.size());
    }

    public void nextImage() {
        imageIndex = imageIndex + 1;
    }

    public double getAnimationPeriod() {
        return animationPeriod;
    }

    public double getActionPeriod() {
        return getActionPeriod();
    }

    public int getImageIndex() {
        return imageIndex;
    }
}
