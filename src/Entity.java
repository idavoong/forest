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
    EntityKind getKind();

    String getId();

    Point getPosition();

    void setPosition(Point position);

    String log();

    PImage getCurrentImage();

    void nextImage();
}
