import java.util.*;

import processing.core.PImage;
import processing.core.PApplet;

/**
 * This class contains many functions written in a procedural style.
 * You will reduce the size of this class over the next several weeks
 * by refactoring this codebase to follow an OOP style.
 */
public final class Functions {
    private static final int COLOR_MASK = 0xffffff;
    private static final int KEYED_IMAGE_MIN = 5;
    private static final int KEYED_RED_IDX = 2;
    private static final int KEYED_GREEN_IDX = 3;
    private static final int KEYED_BLUE_IDX = 4;

    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;

    private static final int PROPERTY_KEY = 0;
    private static final int PROPERTY_ID = 1;
    private static final int PROPERTY_COL = 2;
    private static final int PROPERTY_ROW = 3;
    private static final int ENTITY_NUM_PROPERTIES = 4;

    public static final String STUMP_KEY = "stump";
    private static final int STUMP_NUM_PROPERTIES = 0;

    public static final String SAPLING_KEY = "sapling";
    private static final int SAPLING_HEALTH = 0;
    private static final int SAPLING_NUM_PROPERTIES = 1;

    private static final String OBSTACLE_KEY = "obstacle";
    private static final int OBSTACLE_ANIMATION_PERIOD = 0;
    private static final int OBSTACLE_NUM_PROPERTIES = 1;

    private static final String DUDE_KEY = "dude";
    private static final int DUDE_ACTION_PERIOD = 0;
    private static final int DUDE_ANIMATION_PERIOD = 1;
    private static final int DUDE_LIMIT = 2;
    private static final int DUDE_NUM_PROPERTIES = 3;

    private static final String HOUSE_KEY = "house";
    private static final int HOUSE_NUM_PROPERTIES = 0;

    private static final String FAIRY_KEY = "fairy";
    private static final int FAIRY_ANIMATION_PERIOD = 0;
    private static final int FAIRY_ACTION_PERIOD = 1;
    private static final int FAIRY_NUM_PROPERTIES = 2;

    public static final String TREE_KEY = "tree";
    private static final int TREE_ANIMATION_PERIOD = 0;
    private static final int TREE_ACTION_PERIOD = 1;
    private static final int TREE_HEALTH = 2;
    private static final int TREE_NUM_PROPERTIES = 3;


    public static int getIntFromRange(int max, int min) {
        Random rand = new Random();
        return min + rand.nextInt(max-min);
    }

    public static double getNumFromRange(double max, double min) {
        Random rand = new Random();
        return min + rand.nextDouble() * (max - min);
    }

    private static void parseSapling(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = createSapling(id, pt, imageStore.getImageList(SAPLING_KEY), health);
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }

    private static void parseDude(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = createDudeNotFull(id, pt, Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]), Integer.parseInt(properties[DUDE_LIMIT]), imageStore.getImageList(DUDE_KEY));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }

    private static void parseFairy(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = createFairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(FAIRY_KEY));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    private static void parseTree(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = createTree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), imageStore.getImageList(TREE_KEY));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }

    private static void parseObstacle(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = createObstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList(OBSTACLE_KEY));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }

    private static void parseHouse(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = createHouse(id, pt, imageStore.getImageList(HOUSE_KEY));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }
    private static void parseStump(WorldModel world, String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = createStump(id, pt, imageStore.getImageList(STUMP_KEY));
            world.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }

    public static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = Point.distanceSquared(nearest.getPosition(), pos);

            for (Entity other : entities) {
                int otherDistance = Point.distanceSquared(other.getPosition(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public static Action createAnimationAction(EntityAnimation entity, int repeatCount) {
        return new Animation(entity, repeatCount);
    }

    public static Action createActivityAction(EntityActivity entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore);
    }

    public static Entity createHouse(String id, Point position, List<PImage> images) {
        return new House(EntityKind.HOUSE, id, position, images, 0, 0, 0, 0, 0, 0);
    }

    public static Entity createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(EntityKind.OBSTACLE, id, position, images, 0, 0, 0, animationPeriod, 0, 0);
    }

    public static Entity createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(EntityKind.TREE, id, position, images, 0, 0, actionPeriod, animationPeriod, health, 0);
    }

    public static Entity createStump(String id, Point position, List<PImage> images) {
        return new Stump(EntityKind.STUMP, id, position, images, 0, 0, 0, 0, 0, 0);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Entity createSapling(String id, Point position, List<PImage> images, int health) {
        return new Sapling(EntityKind.SAPLING, id, position, images, 0, 0, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public static Entity createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(EntityKind.FAIRY, id, position, images, 0, 0, actionPeriod, animationPeriod, 0, 0);
    }

    // need resource count, though it always starts at 0
    public static Entity createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeNotFull(EntityKind.DUDE_NOT_FULL, id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
    }

    // don't technically need resource count ... full
    public static Entity createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(EntityKind.DUDE_FULL, id, position, images, resourceLimit, 0, actionPeriod, animationPeriod, 0, 0);
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
                    case "Backgrounds:" -> world.setBackground(new Background[world.getNumRows()][world.getNumCols()]);
                    case "Entities:" -> {
                        world.setOccupancy(new Entity[world.getNumRows()][world.getNumCols()]);
                        world.setEntities(new HashSet<>());
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> world.setNumRows(Integer.parseInt(line));
                    case "Cols:" -> world.setNumCols(Integer.parseInt(line)); 
                    case "Backgrounds:" -> Functions.parseBackgroundRow(world, line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> Functions.parseEntity(world, line, imageStore);
                }
            }
        }
    }
    private static void parseBackgroundRow(WorldModel world, String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < world.getNumRows()){
            int rows = Math.min(cells.length, world.getNumCols());
            for (int col = 0; col < rows; col++){
                //world.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
                world.setBackground(new Background(cells[col], imageStore.getImageList(cells[col])), row, col);
            }
        }
    }

    private static void parseEntity(WorldModel world, String line, ImageStore imageStore) {
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

    public static int clamp(int value, int low, int high) {
        return Math.min(high, Math.max(value, low));
    }

    private static void processImageLine(Map<String, List<PImage>> images, String line, PApplet screen) {
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
    private static void setAlpha(PImage img, int maskColor, int alpha) {
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

    public static void loadImages(Scanner in, ImageStore imageStore, PApplet screen) {
        int lineNumber = 0;
        while (in.hasNextLine()) {
            try {
                processImageLine(imageStore.getImages(), in.nextLine(), screen);
            } catch (NumberFormatException e) {
                System.out.printf("Image format error on line %d\n", lineNumber);
            }
            lineNumber++;
        }
    }
}
