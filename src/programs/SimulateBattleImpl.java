package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog; // Позволяет логировать. Использовать после каждой атаки юнита

    //  Сложность: O(n * m). (n - количество юнитов в армии игрока, m - количество юнитов в армии компьютера)
    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        Set<Unit> playerUnits = playerArmy.getUnits().stream().filter(Unit::isAlive).collect(java.util.stream.Collectors.toSet());
        Set<Unit> computerUnits = computerArmy.getUnits().stream().filter(Unit::isAlive).collect(java.util.stream.Collectors.toSet());;

        while ( playerUnits.stream().anyMatch(Unit::isAlive) && computerUnits.stream().anyMatch(Unit::isAlive)) {
            executeAttacks(playerUnits);
            executeAttacks(computerUnits);
        }


    }

    private void executeAttacks(Set<Unit> attackingUnits) throws InterruptedException {
        for (var attackingUnit : attackingUnits) {
            Unit target = attackingUnit.getProgram().attack();
            if (target != null) {
                printBattleLog.printBattleLog(attackingUnit, target);
                System.out.println(attackingUnit.getName() + "is attacked to " + target.getName());
                System.out.println(target.getName() + " received " + attackingUnit.getBaseAttack() + " damage"
                        + (!target.isAlive() ? " and died" : ""));
            }
        }
    }
}