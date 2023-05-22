import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public final class Sapling extends Plant implements Entity, EntityAnimation, EntityActivity {
    private int healthLimit;

    public Sapling(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod, int health, int healthLimit) {
        super(id, position, images, actionPeriod, animationPeriod, health);
        this.healthLimit = healthLimit;
    }

    public int getHealthLimit() {
        return healthLimit;
    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        addHealth();
        super.executeActivity(world, imageStore, scheduler);
    }
}
