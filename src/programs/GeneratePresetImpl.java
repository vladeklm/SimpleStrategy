package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    // Сложность: Сортировка - O(n log n). Выбор юнитов - O(n). В итоге - O(n log n)
    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army army = new Army();
        List<Unit> units = new ArrayList<>();

        sortUnits(unitList);

        int points = 0;

        for (Unit unit : unitList) {
            int quantity = calculateMaxUnitsToAdd(unit, maxPoints, points);
            addUnitsToArmy(unit, quantity, units);
            points += quantity * unit.getCost();
        }

        assignCoordinates(units);
        army.setUnits(units);
        army.setPoints(points);
        return army;
    }

    private void sortUnits(List<Unit> units) {
        units.sort(Comparator.comparingDouble(unit -> -((double) (unit.getBaseAttack() + unit.getHealth()) / unit.getCost())));
    }

    private int calculateMaxUnitsToAdd(Unit unit, int maxPoints, int points) {
        return Math.min(11, (maxPoints - points) / unit.getCost());
    }

    private void addUnitsToArmy(Unit unit, int quantity, List<Unit> selectedUnits) {
        for (int i = 0; i < quantity; i++) {
            Unit newUnit = new Unit(unit.getName(), unit.getUnitType(), unit.getHealth(),
                    unit.getBaseAttack(), unit.getCost(), unit.getAttackType(),
                    unit.getAttackBonuses(), unit.getDefenceBonuses(), -1, -1);
            newUnit.setName(unit.getUnitType() + " " + i);
            selectedUnits.add(newUnit);
        }
    }

    private void assignCoordinates(List<Unit> units) {
        Set<String> occupiedCoords = new HashSet<>();
        Random random = new Random();

        for (Unit unit : units) {
            int coordX, coordY;
            do {
                coordX = random.nextInt(3);
                coordY = random.nextInt(21);
            } while (occupiedCoords.contains(coordX + "," + coordY));
            occupiedCoords.add(coordX + "," + coordY);
            unit.setxCoordinate(coordX);
            unit.setyCoordinate(coordY);
        }
    }
}