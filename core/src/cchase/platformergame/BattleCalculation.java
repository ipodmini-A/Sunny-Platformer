package cchase.platformergame;

public class BattleCalculation
{
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
            damage = attacker.getAttack() * 2 - defender.getDefense();
        } else
        {
            damage = attacker.getAttack() * attacker.getAttack() / attacker.getDefense();
        }
        defender.setHealth(defender.getHealth() - damage);
    }

    public static void magicDamageCalculation(Player attacker, Player defender)
    {
        float damage = 0;
        // Value * 2 * mag * mag / (wis + mag)
        // value * 2 * mag / (wis + mag)
    }

}
