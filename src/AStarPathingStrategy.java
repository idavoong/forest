import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class AStarPathingStrategy implements PathingStrategy {
    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> path = new LinkedList<Point>();

        //HashMap<Node, Point> closedList = new HashMap<Node, Point>();
        HashSet<Point> closedList = new HashSet<Point>();
        HashMap<Point, Integer> openList = new HashMap<Point, Integer>();

        Comparator<Node> priority = (n1, n2) -> n1.f - n2.f;
        PriorityQueue<Node> orderedOpenList = new PriorityQueue<>(priority);

        Node beginning = new Node(start);
        Node goal = new Node(end);

        //add start
        openList.put(beginning.point, beginning.g);
        orderedOpenList.add(beginning);

        Node cur = orderedOpenList.peek();
        boolean found = false;

        while (!orderedOpenList.isEmpty()) {
            //create neighbors
            Stream<Point> potential = potentialNeighbors.apply(cur.point).filter(canPassThrough).filter(s -> !closedList.contains(s));
            List<Node> neighbors = potential.map(Node::new).toList();
            if (withinReach.test(cur.point, goal.point)) {
                found = true;
                goal.prior = cur;
                break;
            }
            for (Node neighbor : neighbors) {
                neighbor.prior = cur;
                int g = cur.g + 1;
                int h = neighbor.distance(goal);
                int f = g + h;
                if (openList.containsKey(neighbor.point)) {
                    if (openList.get(neighbor.point) > g) {
                        openList.remove(neighbor.point);
                        orderedOpenList.remove(neighbor);
                        neighbor.g = g;
                        neighbor.h = h;
                        neighbor.f = f;
                        openList.put(neighbor.point, neighbor.g);
                        orderedOpenList.add(neighbor);
                    }
                }
                else {
                    neighbor.g = g;
                    neighbor.h = h;
                    neighbor.f = f;
                    openList.put(neighbor.point, neighbor.g);
                    orderedOpenList.add(neighbor);
                }
            }

            closedList.add(cur.point);
            openList.remove(cur.point);
            orderedOpenList.remove(cur);
            cur = orderedOpenList.peek();
        }

        cur = goal.prior;
        while (found && !cur.equals(beginning)) {
            path.add(0, cur.point);
            cur = cur.prior;
        }

        return path;
    }
}
