package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int MAX_PER_TYPE = 11;

    private static final int[] X_SLOTS = {0, 1, 2};
    private static final int HEIGHT = 21;

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        if (unitList == null || unitList.isEmpty() || maxPoints <= 0) {
            return new Army();
        }

        List<Unit> prototypes = new ArrayList<>(unitList);
        prototypes.removeIf(Objects::isNull);

        prototypes.sort((a, b) -> {
            double aAtk = (double) a.getBaseAttack() / Math.max(1, a.getCost());
            double bAtk = (double) b.getBaseAttack() / Math.max(1, b.getCost());
            int c1 = Double.compare(bAtk, aAtk);
            if (c1 != 0) return c1;

            double aHp = (double) a.getHealth() / Math.max(1, a.getCost());
            double bHp = (double) b.getHealth() / Math.max(1, b.getCost());
            int c2 = Double.compare(bHp, aHp);
            if (c2 != 0) return c2;

            return Integer.compare(a.getCost(), b.getCost());
        });

        Map<String, Integer> countByType = new HashMap<>();
        List<Unit> resultUnits = new ArrayList<>();
        int points = 0;

        while (true) {
            boolean added = false;

            for (Unit proto : prototypes) {
                String type = proto.getUnitType();
                int cnt = countByType.getOrDefault(type, 0);
                if (cnt >= MAX_PER_TYPE) continue;

                int cost = proto.getCost();
                if (points + cost > maxPoints) continue;

                String name = type + " " + (cnt + 1);

                Unit u = new Unit(
                        name,
                        proto.getUnitType(),
                        proto.getHealth(),
                        proto.getBaseAttack(),
                        proto.getCost(),
                        proto.getAttackType(),
                        proto.getAttackBonuses(),
                        proto.getDefenceBonuses(),
                        0,
                        0
                );

                resultUnits.add(u);
                points += cost;
                countByType.put(type, cnt + 1);

                added = true;
                break;
            }

            if (!added) break;
        }

        List<int[]> cells = new ArrayList<>(X_SLOTS.length * HEIGHT);
        for (int x : X_SLOTS) {
            for (int y = 0; y < HEIGHT; y++) {
                cells.add(new int[]{x, y});
            }
        }
        Collections.shuffle(cells, new Random(System.nanoTime()));

        for (int i = 0; i < resultUnits.size(); i++) {
            Unit u = resultUnits.get(i);
            int[] c = cells.get(i);
            u.setxCoordinate(c[0]);
            u.setyCoordinate(c[1]);
        }

        Army army = new Army(resultUnits);
        army.setPoints(points);
        return army;
    }
}
