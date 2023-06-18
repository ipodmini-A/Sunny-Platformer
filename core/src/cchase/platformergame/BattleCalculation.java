package cchase.platformergame;

import java.util.Random;

public class BattleCalculation
{
    private static final Random rand = new Random();
    /**
     * damageCalculation calculates the damage between an attacker and a defender.
     * This uses attack and defense to do calculations.
     * Weapons will be added later
     * This formula was taken from this site:
     * <a href="https://tung.github.io/posts/simplest-non-problematic-damage-formula/">...</a>
     * @param attacker Actor that is attacking
     * @param defender Actor that is defending
     */
    public static void damageCalculation(Player attacker, Player defender)
    {
        float damage = 0;
        if (attacker.getAttack() >= defender.getDefense())
        {
            damage = (attacker.getAttack() * 2 - defender.getDefense()) * randomCheck();
        } else
        {
            damage = (attacker.getAttack() * attacker.getAttack() / attacker.getDefense()) * randomCheck();
        }
        defender.setHealth(defender.getHealth() - damage);
    }

    /**
     * Magic damage calculation.
     * Currently not implemented
     * @param attacker
     * @param defender
     */
    public static void magicDamageCalculation(Player attacker, Player defender)
    {
        float damage = 0;
        // Value * 2 * mag * mag / (wis + mag)
        // value * 2 * mag / (wis + mag)
    }

    public static void defenseDamageCalculation(Player attacker, Player defender)
    {
        float damage = 0;
        damage = (attacker.getAttack() / ((defender.getDefense() + 100) / 100)) * randomCheck();
        defender.setHealth(defender.getHealth() - damage);
    }

    /**
     * Random damage check. If the player rolls a 0, a crit is performed.
     * the roll is determined on randomCheck.
     *
     * @return damage multiplier
     */
    public static int randomCheck()
    {
        int randomCheck = rand.nextInt(10);
        if (randomCheck == 0)
        {
            return 3; // Return 3 times the damage
        }else
        {
            return 1; // return 1 times the damage
        }
    }

}
