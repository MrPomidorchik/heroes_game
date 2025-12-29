package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {

    public PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // System.out.println("SimulateBattleImpl called");

        if (playerArmy == null || computerArmy == null) return;

        while (hasAlive(playerArmy) && hasAlive(computerArmy)) {
            Set<Unit> acted = new HashSet<>();

            int aliveAtRoundStart = countAlive(playerArmy) + countAlive(computerArmy);
            int noTargetMoves = 0;

            PriorityQueue<Unit> q = buildQueue(playerArmy, computerArmy, acted);

            while (!q.isEmpty() && hasAlive(playerArmy) && hasAlive(computerArmy)) {
                Unit attacker = q.poll();
                if (attacker == null || !attacker.isAlive() || acted.contains(attacker)) {
                    continue;
                }

                Unit target = attacker.getProgram().attack();

                printBattleLog.printBattleLog(attacker, target);

                acted.add(attacker);

                if (target == null) {
                    noTargetMoves++;
                    if (noTargetMoves >= aliveAtRoundStart) return;
                } else {
                    noTargetMoves = 0;
                }

                q = buildQueue(playerArmy, computerArmy, acted);
            }
        }
    }

    private PriorityQueue<Unit> buildQueue(Army player, Army computer, Set<Unit> acted) {
        PriorityQueue<Unit> pq = new PriorityQueue<>((a, b) -> {
            int c = Integer.compare(b.getBaseAttack(), a.getBaseAttack());
            if (c != 0) return c;
            return String.valueOf(a.getName()).compareTo(String.valueOf(b.getName()));
        });

        addAliveNotActed(pq, player, acted);
        addAliveNotActed(pq, computer, acted);
        return pq;
    }

    private void addAliveNotActed(PriorityQueue<Unit> pq, Army army, Set<Unit> acted) {
        if (army == null || army.getUnits() == null) return;
        for (Unit u : army.getUnits()) {
            if (u != null && u.isAlive() && !acted.contains(u)) pq.add(u);
        }
    }

    private boolean hasAlive(Army army) {
        return countAlive(army) > 0;
    }

    private int countAlive(Army army) {
        if (army == null || army.getUnits() == null) return 0;
        int cnt = 0;
        for (Unit u : army.getUnits()) if (u != null && u.isAlive()) cnt++;
        return cnt;
    }
}
