package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    private static final int[] DX = {-1, -1, -1,  0, 0, 1, 1, 1};
    private static final int[] DY = {-1,  0,  1, -1, 1,-1, 0, 1};

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        if (attackUnit == null || targetUnit == null) return Collections.emptyList();

        int sx = attackUnit.getxCoordinate();
        int sy = attackUnit.getyCoordinate();
        int tx = targetUnit.getxCoordinate();
        int ty = targetUnit.getyCoordinate();

        if (!inBounds(sx, sy) || !inBounds(tx, ty)) return Collections.emptyList();

        if (sx == tx && sy == ty) {
            return List.of(new Edge(sx, sy));
        }

        boolean[][] blocked = new boolean[WIDTH][HEIGHT];
        if (existingUnitList != null) {
            for (Unit u : existingUnitList) {
                if (u == null || !u.isAlive()) continue;
                int x = u.getxCoordinate();
                int y = u.getyCoordinate();
                if (!inBounds(x, y)) continue;

                if ((x == sx && y == sy) || (x == tx && y == ty)) continue;

                blocked[x][y] = true;
            }
        }

        boolean[][] visited = new boolean[WIDTH][HEIGHT];

        int[][] parentX = new int[WIDTH][HEIGHT];
        int[][] parentY = new int[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            Arrays.fill(parentX[i], -1);
            Arrays.fill(parentY[i], -1);
        }

        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sx, sy});
        visited[sx][sy] = true;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1];

            for (int k = 0; k < 8; k++) {
                int nx = x + DX[k];
                int ny = y + DY[k];

                if (!inBounds(nx, ny)) continue;
                if (blocked[nx][ny]) continue;
                if (visited[nx][ny]) continue;

                visited[nx][ny] = true;
                parentX[nx][ny] = x;
                parentY[nx][ny] = y;

                if (nx == tx && ny == ty) {
                    return reconstructPath(sx, sy, tx, ty, parentX, parentY);
                }

                q.add(new int[]{nx, ny});
            }
        }

        return Collections.emptyList();
    }

    private List<Edge> reconstructPath(int sx, int sy, int tx, int ty, int[][] parentX, int[][] parentY) {
        LinkedList<Edge> path = new LinkedList<>();
        int cx = tx, cy = ty;
        path.addFirst(new Edge(cx, cy));

        while (!(cx == sx && cy == sy)) {
            int px = parentX[cx][cy];
            int py = parentY[cx][cy];
            if (px == -1 && py == -1) return Collections.emptyList();

            cx = px;
            cy = py;
            path.addFirst(new Edge(cx, cy));
        }

        return path;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
}