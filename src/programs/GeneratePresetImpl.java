package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {
    //n - количество типов юнитов, m - количество юнитов в армии
    // Сложность: Добавление юнитов - O(m). (в цикле на 24 строчке мы для каждого типа генерируем соответствующее количество юнитов
    // в сумме их будет m)
    // Время вызова функий, связанных со случайными числами будем считать константным
    // Сортировка 4 элементов- O(nLogn)
    // В итоге - O(m + nlogn)
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        var army = new Army();
        var units = new ArrayList<Unit>();

        unitList.sort(Comparator.comparingDouble(unit -> -((double) (unit.getBaseAttack() + unit.getHealth()) / unit.getCost())));

        var points = 0;

        for (var unit: unitList) {
            var quantity = Math.min(11, (maxPoints - points) / unit.getCost());
            addUnitsToArmy(unit, quantity, units);
            points += quantity * unit.getCost();
        }

        setCoordinates(units);
        army.setUnits(units);
        army.setPoints(points);
        return army;
    }

    private void addUnitsToArmy(Unit unit, int quantity, List<Unit> selectedUnits) {
        for (int i = 0; i < quantity; i++) {
            var newUnit = new Unit(unit.getName(), unit.getUnitType(), unit.getHealth(),
                    unit.getBaseAttack(), unit.getCost(), unit.getAttackType(),
                    unit.getAttackBonuses(), unit.getDefenceBonuses(), -1, -1);
            newUnit.setName(unit.getUnitType() + " " + i);
            selectedUnits.add(newUnit);
        }
    }

    private void setCoordinates(List<Unit> units) {
        var alreadyUsed = new HashSet<Integer>();
        var random = new Random();
        var left = 0;
        var right = 21*3-1;


        for (Unit unit : units) {
            int number;
            var rand = random.nextInt(100);
            if (rand < 50) {
                number = left;
                left++;
            }
            else {
                number = right;
                right--;
            }
            var x = number / 21;
            var y = number % 21;
            if (alreadyUsed.contains(number)) {
                continue;
            }
            alreadyUsed.add(number);
            unit.setxCoordinate(x);
            unit.setyCoordinate(y);
        }
    }

}