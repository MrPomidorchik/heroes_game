package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
         List<Unit> result = new ArrayList<>();
        if (unitsByRow == null || unitsByRow.isEmpty()) return result;

        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) continue;

            Set<Integer> occupiedY = new HashSet<>(row.size() * 2);
            for (Unit u : row) {
                if (u != null && u.isAlive()) {
                    occupiedY.add(u.getyCoordinate());
                }
            }

            for (Unit u : row) {
                if (u == null || !u.isAlive()) continue;

                int y = u.getyCoordinate();
                if (isLeftArmyTarget) {
                    if (!occupiedY.contains(y - 1)) {
                        result.add(u);
                    }
                } else {
                    if (!occupiedY.contains(y + 1)) {
                        result.add(u);
                    }
                }
            }
        }

        return result;
    }
}
