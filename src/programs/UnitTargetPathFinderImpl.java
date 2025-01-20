package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    // Сложность: O(n * m * log(n * m)). (n - ширина, m - высота поля)
    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        int[][] distance = initDistanceArray();
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Edge[][] previous = new Edge[WIDTH][HEIGHT];
        Set<String> occupiedCells = getOccupiedCells(existingUnitList, attackUnit, targetUnit);

        PriorityQueue<EdgeDistance> queue = new PriorityQueue<>(Comparator.comparingInt(EdgeDistance::getDistance));
        initStartPoint(attackUnit, distance, queue);

        // Алгоритм поиска пути
        while (!queue.isEmpty()) {
            EdgeDistance current = queue.poll();
            if (visited[current.getX()][current.getY()]) continue;
            visited[current.getX()][current.getY()] = true;

            if (isTargetReached(current, targetUnit)) {
                break;
            }

            checkNeighbors(current, occupiedCells, distance, previous, queue);
        }

        return constructPath(previous, attackUnit, targetUnit);
    }

    private int[][] initDistanceArray() {
        int[][] distance = new int[WIDTH][HEIGHT];
        for (int[] row : distance) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        return distance;
    }

    private Set<String> getOccupiedCells(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        Set<String> result = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                result.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }
        return result;
    }

    private void initStartPoint(Unit attackUnit, int[][] distance, PriorityQueue<EdgeDistance> queue) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        distance[startX][startY] = 0;
        queue.add(new EdgeDistance(startX, startY, 0));
    }

    private boolean isTargetReached(EdgeDistance current, Unit targetUnit) {
        return current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate();
    }

    private void checkNeighbors(EdgeDistance current, Set<String> occupiedCells, int[][] distance, Edge[][] previous, PriorityQueue<EdgeDistance> queue) {
        for (int[] dir : DIRECTIONS) {
            int neighborX = current.getX() + dir[0];
            int neighborY = current.getY() + dir[1];
            if (isValid(neighborX, neighborY, occupiedCells)) {
                int newDistance = distance[current.getX()][current.getY()] + 1;
                if (!queue.contains(new EdgeDistance(neighborX, neighborY, newDistance)) && newDistance < distance[neighborX][neighborY]) {
                    distance[neighborX][neighborY] = newDistance;
                    previous[neighborX][neighborY] = new Edge(current.getX(), current.getY());
                    queue.add(new EdgeDistance(neighborX, neighborY, newDistance));
                }
            }
        }
    }

    private boolean isValid(int x, int y, Set<String> occupiedCells) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT && !occupiedCells.contains(x + "," + y);
    }

    private List<Edge> constructPath(Edge[][] previous, Unit attackUnit, Unit targetUnit) {
        List<Edge> path = new ArrayList<>();
        int pathX = targetUnit.getxCoordinate();
        int pathY = targetUnit.getyCoordinate();

        while (pathX != attackUnit.getxCoordinate() || pathY != attackUnit.getyCoordinate()) {
            path.add(new Edge(pathX, pathY));
            Edge prev = previous[pathX][pathY];
            if (prev == null) return Collections.emptyList(); // Если путь не найден
            pathX = prev.getX();
            pathY = prev.getY();
        }
        path.add(new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate()));
        Collections.reverse(path);
        return path;
    }
}