
/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import java.util.Arrays;

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players. ");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player. " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		boolean[][] categoryIsEmpty = new boolean[nPlayers][TOTAL];
		int[][] everyScore = new int[nPlayers][TOTAL];
		// Plays game, counts lower score, upper score and shows winner.
		playGame(categoryIsEmpty, everyScore);
		upperScoreCounter(everyScore);
		bottomScoreCounter(everyScore);
		highestScoreNumFndr(everyScore);
		String winnerName = playerNames[highestScoreNumFndr(everyScore)];
		display.printMessage("Winner is " + winnerName + "! Good job! ");

	}

	// Everything is done here, this runs the game.
	private void playGame(boolean[][] categoryIsEmpty, int[][] everyScore) {

		for (int i = 1; i < N_SCORING_CATEGORIES + 1; i++) {

			for (int j = 1; j < nPlayers + 1; j++) {

				display.printMessage(playerNames[j - 1] + "'s turn. Click \"Roll Dice\" button to roll the dice. ");
				display.waitForPlayerToClickRoll(j);
				int[] diceRollerSaver = diceRoller();
				display.displayDice(diceRollerSaver);

				for (int g = 0; g < 2; g++) {

					display.printMessage("Select the dice you wish to re-roll and click \"Roll Again\". ");
					display.waitForPlayerToSelectDice();
					diceReroller(diceRollerSaver);
					display.displayDice(diceRollerSaver);

				}
				display.printMessage("Select a category for this roll. ");

				categoryChooser(j, categoryIsEmpty, everyScore, diceRollerSaver);

			}

		}

	}

	// Lets player choose category but only if it's empty.
	private void categoryChooser(int j, boolean[][] categoryIsEmpty, int[][] everyScore, int[] diceRollerSaver) {
		while (true) {
			int emptyCategory = display.waitForPlayerToSelectCategory();

			if (!categoryIsEmpty[j - 1][emptyCategory]) {

				display.updateScorecard(emptyCategory, j, scoreOnTheDices(emptyCategory, diceRollerSaver));
				categoryIsEmpty[j - 1][emptyCategory] = true;
				everyScore[j - 1][emptyCategory] = scoreOnTheDices(emptyCategory, diceRollerSaver);
				everyScore[j - 1][TOTAL - 1] += everyScore[j - 1][emptyCategory];
				display.updateScorecard(TOTAL, j, everyScore[j - 1][TOTAL - 1]);
				break;
			}
			display.printMessage("Please, selecet an empty Category. ");

		}
	}

	// Counts upper score and gives bonus if upper score is higher than 63.
	private void upperScoreCounter(int[][] everyScore) {
		int upperScoreSum = 0;
		int upperBonus = 0;
		for (int j = 1; j < nPlayers + 1; j++) {
			for (int i = ONES; i <= SIXES; i++) {
				upperScoreSum += everyScore[j - 1][i];
			}
			display.updateScorecard(UPPER_SCORE, j, upperScoreSum);
			if (upperScoreSum > 63) {

				upperBonus = 35;

			}
			display.updateScorecard(UPPER_BONUS, j, upperBonus);
			everyScore[j - 1][TOTAL - 1] += upperBonus;
			display.updateScorecard(TOTAL, j, everyScore[j - 1][TOTAL - 1]);
			upperScoreSum = 0;
		}

	}

	// Counts the bottom score.
	private void bottomScoreCounter(int[][] everyScore) {
		int bottomScoreSum = 0;
		for (int j = 1; j < nPlayers + 1; j++) {
			for (int i = THREE_OF_A_KIND; i <= CHANCE; i++) {
				bottomScoreSum += everyScore[j - 1][i];
			}
			display.updateScorecard(LOWER_SCORE, j, bottomScoreSum);
			bottomScoreSum = 0;
		}

	}

	// Chooses five numbers between 1 and 6 and saves the dices in array.
	private int[] diceRoller() {

		int[] rolledDice = new int[N_DICE];

		for (int i = 0; i < N_DICE; i++) {

			rolledDice[i] = rgen.nextInt(1, 6);
			;

		}
		return rolledDice;
	}

	// Checks if the chosen category is right and returns proper number(if
	// correct category) or returns 0.
	private int scoreOnTheDices(int chosen, int[] scoreOnAdice) {

		if (chosen == YAHTZEE) {
			if (isYahtzee(scoreOnAdice)) {
				return 50;
			}
		} else if (chosen == FULL_HOUSE) {
			if (isFullHouse(scoreOnAdice)) {
				return 25;
			}
		} else if (chosen == LARGE_STRAIGHT) {
			if (isBigStraigt(scoreOnAdice)) {
				return 40;
			}
		} else if (chosen == SMALL_STRAIGHT) {
			if (isSmallStraight(scoreOnAdice)) {
				return 30;
			}
		} else if (chosen == FOUR_OF_A_KIND) {
			if (fourSame(scoreOnAdice)) {

				return allDiceAdder(scoreOnAdice);
			}
		} else if (chosen == THREE_OF_A_KIND) {
			if (threeSame(scoreOnAdice)) {
				return allDiceAdder(scoreOnAdice);
			}
		} else if (chosen == CHANCE) {

			return allDiceAdder(scoreOnAdice);

		} else if (chosen >= ONES && chosen <= SIXES) {

			return adder(chosen, scoreOnAdice);

		}

		return 0;

	}

	// Checks if it's big straight.
	private boolean isBigStraigt(int[] scoreOnAdice) {
		boolean bigStraight = false;
		Arrays.sort(scoreOnAdice);
		if (scoreOnAdice[0] == scoreOnAdice[1] - 1 && scoreOnAdice[1] + 1 == scoreOnAdice[2]
				&& scoreOnAdice[2] + 1 == scoreOnAdice[3] && scoreOnAdice[3] == scoreOnAdice[4] - 1) {
			bigStraight = true;
		}
		return bigStraight;
		// returns true or false.
	}

	// Adds all the dices.
	private int allDiceAdder(int[] scoreOnAdice) {
		int total = 0;
		for (int i = 0; i < N_DICE; i++) {

			total += scoreOnAdice[i];

		}
		return total;
		// returns sum.
	}

	// Checks if it's small straight.
	private boolean isSmallStraight(int[] scoreOnAdice) {
		boolean smallStraight = false;
		Arrays.sort(scoreOnAdice);
		if (scoreOnAdice[0] == scoreOnAdice[1] - 1 && scoreOnAdice[1] + 1 == scoreOnAdice[2]
				&& scoreOnAdice[2] + 1 == scoreOnAdice[3]) {
			smallStraight = true;
		}
		if (scoreOnAdice[1] + 1 == scoreOnAdice[2] && scoreOnAdice[2] + 1 == scoreOnAdice[3]
				&& scoreOnAdice[3] == scoreOnAdice[4] - 1) {
			smallStraight = true;
		}
		return smallStraight;
		// returns true or false.

	}

	// Finds the highest total score.
	private int highestTotalFinder(int[][] everyScore) {
		int highestTotal = 0;
		for (int j = 1; j < nPlayers + 1; j++) {
			if (highestTotal < everyScore[j - 1][TOTAL - 1]) {
				highestTotal = everyScore[j - 1][TOTAL - 1];
			}
		}

		return highestTotal;
	}

	// Finds which player had the highest total score, returns his number.
	private int highestScoreNumFndr(int[][] everyScore) {
		int i = 0;
		while (highestTotalFinder(everyScore) != everyScore[i][TOTAL - 1]) {
			i++;
		}
		return i;
	}

	// Adds chosen dices and returns number.
	private int adder(int chosen, int[] scoreOnAdice) {
		int total = 0;
		for (int i = 0; i < N_DICE; i++) {
			if (scoreOnAdice[i] != chosen) {
				continue;
			} else {
				total += scoreOnAdice[i];
			}
		}
		return total;
	}

	// Checks if it's yahtzee or not.
	private boolean isYahtzee(int[] scoreOnAdice) {
		int countFive = 1;
		Arrays.sort(scoreOnAdice);
		for (int i = 0; i < N_DICE - 1; i++) {
			if (scoreOnAdice[i] == scoreOnAdice[i + 1]) {
				countFive += 1;
			}

		}
		if (countFive == 5) {
			return true;
		} else {
			return false;
		}
		// returns true or false.

	}

	// Checks if it's full house or not.
	private boolean isFullHouse(int[] scoreOnAdice) {

		boolean fullHouseFound = false;

		Arrays.sort(scoreOnAdice);

		if (scoreOnAdice[0] == scoreOnAdice[1] && scoreOnAdice[0] == scoreOnAdice[2]
				&& scoreOnAdice[3] == scoreOnAdice[4]) {
			fullHouseFound = true;
		} else if (scoreOnAdice[0] == scoreOnAdice[1] && scoreOnAdice[2] == scoreOnAdice[3]
				&& scoreOnAdice[3] == scoreOnAdice[4]) {
			fullHouseFound = true;
		}

		return fullHouseFound;
		// returns true or false.

	}

	// Rerolls chosen dices.
	private void diceReroller(int[] diceRollerSaver) {
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) {
				diceRollerSaver[i] = rgen.nextInt(1, 6);
				
			}
		}
	}

	// Checks if there is three of a kind.
	private boolean threeSame(int[] scoreOnAdice) {
		int countThree = 0;
		int foundTargets = 0;
		for (int j = 0; j < N_DICE - 1; j++) {
			for (int i = 0; i < N_DICE; i++) {
				if (scoreOnAdice[j] == scoreOnAdice[i]) {
					countThree += 1;
				}

			}

			if (countThree >= 3) {

				foundTargets += 1;
				countThree = 0;

			} else {

				countThree = 0;
			}

		}
		if (foundTargets > 0) {
			return true;
		} else {
			return false;
		}
		// returns true or false.
	}

	// Checks if there is four of a kind.
	private boolean fourSame(int[] scoreOnAdice) {
		int countFour = 1;
		int foundTargets = 0;
		for (int j = 0; j < N_DICE - 1; j++) {
			for (int i = 1; i < N_DICE; i++) {
				if (scoreOnAdice[j] == scoreOnAdice[i]) {
					countFour += 1;
				}

			}

			if (countFour >= 4) {

				foundTargets += 1;
				countFour = 1;

			} else {
				countFour = 1;
			}

		}
		if (foundTargets > 0) {
			return true;
		} else {
			return false;
		}
		// returns true or false.
	}

	/* Private instance variables */
	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();

}
