package com.interrupt.helpers;

import com.badlogic.gdx.Gdx;
import com.interrupt.dungeoneer.achievements.AchievementTriggerEvent;
import com.interrupt.dungeoneer.achievements.AchievementTriggerType;
import com.interrupt.dungeoneer.entities.Entity;
import com.interrupt.dungeoneer.entities.Item;
import com.interrupt.dungeoneer.entities.Monster;
import com.interrupt.dungeoneer.game.Game;

public class PlayerHistory {
	public int monstersKilled = 0;
	public int foodEaten = 0;
	public int damageTaken = 0;
	public int potionsDrank = 0;
	public int scrollsUsed = 0;
	public int wandsUsed = 0;
	public int trapsActivated = 0;
	public int timesTeleported = 0;
	public int timesPoisoned = 0;
	public int thingsIdentified = 0;
	public int secretsFound = 0;
	public int goldTaken = 0;

	public PlayerHistory() { }

	public void addMonsterKill(Monster m) {
		Gdx.app.log("PlayerHistory", "Killed a monster");
		monstersKilled++;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.MONSTER_KILLED, monstersKilled)
        );
	}

	public void ateFood(Item item) {
		Gdx.app.log("PlayerHistory", "Ate food");
		foodEaten++;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.FOOD_EATEN, foodEaten)
        );
	}

	public void drankPotion(Item item) {
		Gdx.app.log("PlayerHistory", "Drank a potion");
		potionsDrank++;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.POTION_DRANK, potionsDrank)
        );
	}

	public void usedScroll(Item item) {
		Gdx.app.log("PlayerHistory", "Used a scroll");
		scrollsUsed++;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.SCROLL_USED, scrollsUsed)
        );
	}

	public void tookDamage(int damage) {
		Gdx.app.log("PlayerHistory", "Took damage");
		damageTaken += damage;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.DAMAGE_TAKEN, damageTaken)
        );
	}

	public void usedWand(Item item) {
		Gdx.app.log("PlayerHistory", "Used a wand");
		wandsUsed++;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.WAND_USED, wandsUsed)
        );
	}

	public void activatedTrap(Entity trap) {
		Gdx.app.log("PlayerHistory", "Tripped a trap");
		trapsActivated++;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.TRAP_ACTIVATED, trapsActivated)
        );
	}

	public void teleported() {
		Gdx.app.log("PlayerHistory", "Was teleported");
		timesTeleported++;

		Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.TELEPORTED, timesTeleported)
        );
	}

	public void poisoned() {

		Gdx.app.log("PlayerHistory", "Was poisoned");
		timesPoisoned++;

		Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.POISONED, timesPoisoned)
        );
	}

	public void identified(Item item) {
		Gdx.app.log("PlayerHistory", "Identified an item");
		thingsIdentified++;

        Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.IDENTIFIED, thingsIdentified)
        );
	}

	public void foundSecret() {
		Gdx.app.log("PlayerHistory", "Found a secret");
		secretsFound++;

		Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.SECRET_FOUND, secretsFound)
        );
	}

	public void tookGold(int amount) {
		Gdx.app.log("PlayerHistory", "Took gold");
		goldTaken += amount;

		Game.achievementManager.triggerAchievement(
            new AchievementTriggerEvent(AchievementTriggerType.GOLD_TAKEN, goldTaken)
        );
	}
}
