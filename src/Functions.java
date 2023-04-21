import java.util.*;

import processing.core.PImage;
import processing.core.PApplet;

/**
 * This class contains many functions written in a procedural style.
 * You will reduce the size of this class over the next several weeks
 * by refactoring this codebase to follow an OOP style.
 */
public final class Functions {
    public static final Random rand = new Random();

    public static final int COLOR_MASK = 0xffffff;
    public static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;

    public static final List<String> PATH_KEYS = new ArrayList<>(Arrays.asList("bridge", "dirt", "dirt_horiz", "dirt_vert_left", "dirt_vert_right", "dirt_bot_left_corner", "dirt_bot_right_up", "dirt_vert_left_bot"));

    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;

    public static final int PROPERTY_KEY = 0;
    public static final int PROPERTY_ID = 1;
    public static final int PROPERTY_COL = 2;
    public static final int PROPERTY_ROW = 3;
    public static final int ENTITY_NUM_PROPERTIES = 4;

    public static final String STUMP_KEY = "stump";
    public static final int STUMP_NUM_PROPERTIES = 0;

    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;

    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_ANIMATION_PERIOD = 0;
    public static final int OBSTACLE_NUM_PROPERTIES = 1;

    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;

    public static final String HOUSE_KEY = "house";
    public static final int HOUSE_NUM_PROPERTIES = 0;

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;

    public static final String TREE_KEY = "tree";
    public static final int TREE_ANIMATION_PERIOD = 0;
    public static final int TREE_ACTION_PERIOD = 1;
    public static final int TREE_HEALTH = 2;
    public static final int TREE_NUM_PROPERTIES = 3;

    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;

    public static boolean transformSapling(Entity entity, WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (entity.health <= 0) {
            Entity stump = createStump(STUMP_KEY + "_" + entity.id, entity.position, getImageList(imageStore, STUMP_KEY));

            removeEntity(world, scheduler, entity);

            world.addEntity(stump);

            return true;
        } else if (entity.health >= entity.healthLimit) {
            Entity tree = createTree(TREE_KEY + "_" + entity.id, entity.position, getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), getImageList(imageStore, TREE_KEY));

            removeEntity(world, scheduler, entity);

            world.addEntity(tree);
            tree.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }

    public static boolean moveToFairy(Entity fairy, WorldModel world, Entity target, EventScheduler scheduler) {
        if (adjacent(fairy.position, target.position)) {
            removeEntity(world, scheduler, target);
            return true;
        } else {
            Point nextPos = fairy.nextPositionFairy(world, target.position);

            if (!fairy.position.equals(nextPos)) {
                moveEntity(world, scheduler, fairy, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToNotFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler) {
        if (adjacent(dude.position, target.position)) {
            dude.resourceCount += 1;
            target.health--;
            return true;
        } else {
            Point nextPos = dude.nextPositionDude(world, target.position);

            if (!dude.position.equals(nextPos)) {
                moveEntity(world, scheduler, dude, nextPos);
            }
            return false;
        }
    }

    public static boolean moveToFull(Entity dude, WorldModel world, Entity target, EventScheduler scheduler) {
        if (adjacent(dude.position, target.position)) {
            return true;
        } else {
            Point nextPos = dude.nextPositionDude(world, target.position);

            if (!dude.position.equals(nextPos)) {
                moveEntity(world, scheduler, dude, nextPos);
            }
            return false;
        }
    }

    public static boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
    }

    public static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    public static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    public static void unscheduleAllEvents(EventScheduler scheduler, Entity entity) {
        List<Event> pending = scheduler.pendingEvents.remove(entity);

        if (pending != null) {
            for (Event event : pending) {
                scheduler.eventQueue.remove(event);
            }
        }
    }

    public static void removePendingEvent(EventScheduler scheduler, Event event) {
        List<Event> pending = scheduler.pendingEvents.get(event.entity);

        if (pending != null) {
            pending.remove(event);
        }
    }

    public static void updateOnTime(EventScheduler scheduler, double time) {
        double stopTime = scheduler.currentTime + time;
        while (!scheduler.eventQueue.isEmpty() && scheduler.eventQueue.peek().time <= stopTime) {
            Event next = scheduler.eventQueue.poll();
            Functions.removePendingEvent(scheduler, next);
            scheduler.currentTime = next.time;
            next.action.executeAction(scheduler);
        }
        scheduler.currentTime = stopTime;
    }

    public static void parseSapling(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = createSapling(id, pt, Functions.getImageList(imageStore, SAPLING_KEY), health);
            tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    public static void parseDude(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), Functions.getImageList(imageStore, DUDE_KEY));
            tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    public static void parseFairy(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), Functions.getImageList(imageStore, FAIRY_KEY));
            tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    public static void parseTree(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), Functions.getImageList(imageStore, TREE_KEY));
            tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    public static void parseObstacle(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), Functions.getImageList(imageStore, OBSTACLE_KEY));
            tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    public static void parseHouse(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = createHouse(id, pt, Functions.getImageList(imageStore, HOUSE_KEY));
            tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }
    public static void parseStump(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = createStump(id, pt, Functions.getImageList(imageStore, STUMP_KEY));
            tryAddEntity(world, entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    public static void tryAddEntity(WorldModel world, Entity entity) {
        if (isOccupied(world, entity.position)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        world.addEntity(entity);
    }

    public static boolean isOccupied(WorldModel world, Point pos) {
        return world.withinBounds(pos) && getOccupancyCell(world, pos) != null;
    }

    public static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = distanceSquared(nearest.position, pos);

            for (Entity other : entities) {
                int otherDistance = distanceSquared(other.position, pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public static int distanceSquared(Point p1, Point p2) {
        int deltaX = p1.x - p2.x;
        int deltaY = p1.y - p2.y;

        return deltaX * deltaX + deltaY * deltaY;
    }

    public static Optional<Entity> findNearest(WorldModel world, Point pos, List<EntityKind> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (EntityKind kind : kinds) {
            for (Entity entity : world.entities) {
                if (entity.kind == kind) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }

    public static void moveEntity(WorldModel world, EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.position;
        if (world.withinBounds(pos) && !pos.equals(oldPos)) {
            setOccupancyCell(world, oldPos, null);
            Optional<Entity> occupant = getOccupant(world, pos);
            occupant.ifPresent(target -> removeEntity(world, scheduler, target));
            setOccupancyCell(world, pos, entity);
            entity.position = pos;
        }
    }

    public static void removeEntity(WorldModel world, EventScheduler scheduler, Entity entity) {
        unscheduleAllEvents(scheduler, entity);
        removeEntityAt(world, entity.position);
    }

    public static void removeEntityAt(WorldModel world, Point pos) {
        if (world.withinBounds(pos) && getOccupancyCell(world, pos) != null) {
            Entity entity = getOccupancyCell(world, pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.position = new Point(-1, -1);
            world.entities.remove(entity);
            setOccupancyCell(world, pos, null);
        }
    }


    public static Optional<Entity> getOccupant(WorldModel world, Point pos) {
        if (isOccupied(world, pos)) {
            return Optional.of(getOccupancyCell(world, pos));
        } else {
            return Optional.empty();
        }
    }

    public static Entity getOccupancyCell(WorldModel world, Point pos) {
        return world.occupancy[pos.y][pos.x];
    }

    public static void setOccupancyCell(WorldModel world, Point pos, Entity entity) {
        world.occupancy[pos.y][pos.x] = entity;
    }

    public static Action createAnimationAction(Entity entity, int repeatCount) {
        return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
    }

    public static Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
        return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
    }

    public static Entity createHouse(String id, Point position, List<PImage> images) {
        return new Entity(EntityKind.HOUSE, id, position, images, 0, 0, 0, 0, 0, 0);
    }

    public static Entity createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Entity(EntityKind.OBSTACLE, id, position, images, 0, 0, 0, animationPeriod, 0, 0);
    }

    public static Entity createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Entity(EntityKind.TREE, id, position, images, 0, 0, actionPeriod, animationPeriod, health, 0);
    }

    public static Entity createStump(String id, Point position, List<PImage> images) {
        return new Entity(EntityKind.STUMP, id, position, images, 0, 0, 0, 0, 0, 0);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Entity createSapling(String id, Point position, List<PImage> images, int health) {
        return new Entity(EntityKind.SAPLING, id, position, images, 0, 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public static Entity createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Entity(EntityKind.FAIRY, id, position, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }

    // need resource count, though it always starts at 0
    public static Entity createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Entity(EntityKind.DUDE_NOT_FULL, id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }

    // don't technically need resource count ... full
    public static Entity createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new Entity(EntityKind.DUDE_FULL, id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }

    public static void load(WorldModel world, Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        parseSaveFile(world, saveFile, imageStore, defaultBackground);
        if(world.background == null){
            world.background = new Background[world.numRows][world.numCols];
            for (Background[] row : world.background)
                Arrays.fill(row, defaultBackground);
        }
        if(world.occupancy == null){
            world.occupancy = new Entity[world.numRows][world.numCols];
            world.entities = new HashSet<>();
        }
    }
    public static void parseSaveFile(WorldModel world, Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> world.background = new Background[world.numRows][world.numCols];
                    case "Entities:" -> {
                        world.occupancy = new Entity[world.numRows][world.numCols];
                        world.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> world.numRows = Integer.parseInt(line);
                    case "Cols:" -> world.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> Functions.parseBackgroundRow(world, line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> Functions.parseEntity(world, line, imageStore);
                }
            }
        }
    }
    public static void parseBackgroundRow(WorldModel world, String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < world.numRows){
            int rows = Math.min(cells.length, world.numCols);
            for (int col = 0; col < rows; col++){
                world.background[row][col] = new Background(cells[col], Functions.getImageList(imageStore, cells[col]));
            }
        }
    }

    public static void parseEntity(WorldModel world, String line, ImageStore imageStore) {
        String[] properties = line.split(" ", Functions.ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= Functions.ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[Functions.PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[Functions.PROPERTY_COL]), Integer.parseInt(properties[Functions.PROPERTY_ROW]));

            properties = properties.length == Functions.ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[Functions.ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Functions.OBSTACLE_KEY -> Functions.parseObstacle(world, properties, pt, id, imageStore);
                case Functions.DUDE_KEY -> Functions.parseDude(world, properties, pt, id, imageStore);
                case Functions.FAIRY_KEY -> Functions.parseFairy(world, properties, pt, id, imageStore);
                case Functions.HOUSE_KEY -> Functions.parseHouse(world, properties, pt, id, imageStore);
                case Functions.TREE_KEY -> Functions.parseTree(world, properties, pt, id, imageStore);
                case Functions.SAPLING_KEY -> Functions.parseSapling(world, properties, pt, id, imageStore);
                case Functions.STUMP_KEY -> Functions.parseStump(world, properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }

    // public static PImage getCurrentImage(Object object) {
    //     if (object instanceof Background background) {
    //         return background.images.get(background.imageIndex);
    //     } else if (object instanceof Entity entity) {
    //         return entity.images.get(entity.imageIndex % entity.images.size());
    //     } else {
    //         throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
    //     }
    // }

    public static Background getBackgroundCell(WorldModel world, Point pos) {
        return world.background[pos.y][pos.x];
    }

    public static void setBackgroundCell(WorldModel world, Point pos, Background background) {
        world.background[pos.y][pos.x] = background;
    }

    public static Point viewportToWorld(Viewport viewport, int col, int row) {
        return new Point(col + viewport.col, row + viewport.row);
    }

    public static Point worldToViewport(Viewport viewport, int col, int row) {
        return new Point(col - viewport.col, row - viewport.row);
    }

    public static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    public static void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen) {
        String[] attrs = line.split("\\s");
        if (attrs.length >= 2) {
            String key = attrs[0];
            PImage img = screen.loadImage(attrs[1]);
            if (img != null && img.width != -1) {
                List<PImage> imgs = getImages(images, key);
                imgs.add(img);

                if (attrs.length >= KEYED_IMAGE_MIN) {
                    int r = Integer.parseInt(attrs[KEYED_RED_IDX]);
                    int g = Integer.parseInt(attrs[KEYED_GREEN_IDX]);
                    int b = Integer.parseInt(attrs[KEYED_BLUE_IDX]);
                    setAlpha(img, screen.color(r, g, b), 0);
                }
            }
        }
    }

    public static List<PImage> getImages(Map<String, List<PImage>> images, String key) {
        return images.computeIfAbsent(key, k -> new LinkedList<>());
    }

    /*
      Called with color for which alpha should be set and alpha value.
      setAlpha(img, color(255, 255, 255), 0));
    */
    public static void setAlpha(PImage img, int maskColor, int alpha) {
        int alphaValue = alpha << 24;
        int nonAlpha = maskColor & COLOR_MASK;
        img.format = PApplet.ARGB;
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            if ((img.pixels[i] & COLOR_MASK) == nonAlpha) {
                img.pixels[i] = alphaValue | nonAlpha;
            }
        }
        img.updatePixels();
    }

    public static boolean contains(Viewport viewport, Point p) {
        return p.y >= viewport.row && p.y < viewport.row + viewport.numRows && p.x >= viewport.col && p.x < viewport.col + viewport.numCols;
    }

    public static Optional<PImage> getBackgroundImage(WorldModel world, Point pos) {
        if (world.withinBounds(pos)) {
            return Optional.of(getBackgroundCell(world, pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public static void drawBackground(WorldView view) {
        for (int row = 0; row < view.viewport.numRows; row++) {
            for (int col = 0; col < view.viewport.numCols; col++) {
                Point worldPoint = viewportToWorld(view.viewport, col, row);
                Optional<PImage> image = getBackgroundImage(view.world, worldPoint);
                if (image.isPresent()) {
                    view.screen.image(image.get(), col * view.tileWidth, row * view.tileHeight);
                }
            }
        }
    }

    public static void drawEntities(WorldView view) {
        for (Entity entity : view.world.entities) {
            Point pos = entity.position;

            if (contains(view.viewport, pos)) {
                Point viewPoint = worldToViewport(view.viewport, pos.x, pos.y);
                view.screen.image(entity.getCurrentImage(), viewPoint.x * view.tileWidth, viewPoint.y * view.tileHeight);
            }
        }
    }

    public static void drawViewport(WorldView view) {
        drawBackground(view);
        drawEntities(view);
    }
    public static List<PImage> getImageList(ImageStore imageStore, String key) {
        return imageStore.images.getOrDefault(key, imageStore.defaultImages);
    }
    public static void loadImages(Scanner in, ImageStore imageStore, PApplet screen) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(imageStore.images, in.nextLine(), screen);
            } catch (NumberFormatException e) {
                System.out.printf("Image format error on line %d\n", lineNumber);
            }
            lineNumber++;
        }
    }
}
