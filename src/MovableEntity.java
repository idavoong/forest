import java.nio.file.Path;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface MovableEntity extends EntityActivity{
    default Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strat = new SingleStepPathingStrategy();
        //exclude the start / end, and be in ascending order

        Class c;
        if (this.getClass() == Fairy.class) {
            c = House.class;
        } else {
            c = Stump.class;
        }

        int horiz = Integer.signum(destPos.x - getPosition().x);
        int vert = Integer.signum(destPos.y - getPosition().y);

        Predicate<Point> canPassThrough = curPoint -> {
            if (horiz == 0 || world.isOccupied(curPoint) && world.getOccupancyCell(curPoint).getClass() != c) {
                if (vert == 0 || world.isOccupied(curPoint) && world.getOccupancyCell(curPoint).getClass() != c) {
                    return false;
                }
            }
            return true;
        };

        BiPredicate<Point, Point> withinReach = (curPoint, destPoint) -> {
            if (Point.adjacent(curPoint, destPoint)) {
                return true;
            }
            return false;
        };

        List<Point> path = strat.computePath(this.getPosition(), destPos, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);

        if (path.isEmpty()) {
            return this.getPosition();
        }
        return path.get(0);

        //return the first point from path
        //handle the case where the path is empty - stay where we are

//        Class c;
//        if (this.getClass() == Fairy.class) {
//            c = House.class;
//        } else {
//            c = Stump.class;
//        }
//
//        int horiz = Integer.signum(destPos.x - getPosition().x);
//        Point newPos = new Point(getPosition().x + horiz, getPosition().y);
//
//        if (horiz == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != c) {
//            int vert = Integer.signum(destPos.y - getPosition().y);
//            newPos = new Point(getPosition().x, getPosition().y + vert);
//
//            if (vert == 0 || world.isOccupied(newPos) && world.getOccupancyCell(newPos).getClass() != c) {
//                newPos = getPosition();
//            }
//        }
//
//        return newPos;
    }

    default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (Point.adjacent(getPosition(), target.getPosition())) {
            moveToHelper(world, target, scheduler);
            return true;
        } else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    void moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);
}
