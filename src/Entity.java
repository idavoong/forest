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
public interface Entity {
    String getId();

    Point getPosition();

    void setPosition(Point position);

    PImage getCurrentImage();

    void nextImage();

    int getImageIndex();

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    default String log() {
        return this.getId().isEmpty() ? null :
                String.format("%s %d %d %d", this.getId(), this.getPosition().x, this.getPosition().y, this.getImageIndex());
    };
}
