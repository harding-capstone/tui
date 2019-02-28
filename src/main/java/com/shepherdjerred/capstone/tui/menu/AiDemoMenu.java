package com.shepherdjerred.capstone.tui.menu;

import com.shepherdjerred.capstone.ai.ab.AlphaBetaQuoridorAi;
import com.shepherdjerred.capstone.ai.ab.evaluator.DefaultMatchEvaluator;
import com.shepherdjerred.capstone.ai.ab.evaluator.RandomMatchEvaluator;
import com.shepherdjerred.capstone.logic.match.MatchStatus.Status;
import com.shepherdjerred.capstone.logic.player.PlayerId;
import com.shepherdjerred.capstone.logic.turn.Turn;
import com.shepherdjerred.capstone.logic.turn.enactor.MatchTurnEnactor;
import com.shepherdjerred.capstone.logic.turn.enactor.TurnEnactorFactory;
import com.shepherdjerred.capstone.logic.turn.validator.TurnValidator;
import com.shepherdjerred.capstone.logic.util.MatchFormatter;
import com.shepherdjerred.capstone.tui.io.Utils;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class AiDemoMenu implements Menu {

  private final Scanner scanner;

  @Override
  public void show() {
    print();
    handleInput();
  }

  private void handleInput() {
    var input = scanner.next();

    if (input.equals("1")) {
      doDemo();
    } else if (input.equals("2")) {
      var vsAi = new VersusAiMenu(scanner, Utils.createMatch());
      vsAi.show();
    } else if (input.equals("3")) {
      var mainMenu = new MainMenu(scanner);
      mainMenu.show();
    } else {
      System.out.println("Invalid input; try again");
      show();
    }
  }

  private void print() {
    System.out.println("\n~ Choose an option: ~");
    System.out.println("1.) Demo.");
    System.out.println("2.) Play versus AI");
    System.out.println("3.) Back.");
  }

  private void doDemo() {
    var initialMatchState = Utils.createMatch();
    var enactor = new MatchTurnEnactor(new TurnEnactorFactory(), new TurnValidator());
    var alphaBetaAi = new AlphaBetaQuoridorAi(new DefaultMatchEvaluator(), 2);
    var randomAi = new AlphaBetaQuoridorAi(new RandomMatchEvaluator(), 2);
    var currentMatchState = initialMatchState;

    var matchFormatter = new MatchFormatter();
    matchFormatter.matchToString(initialMatchState);

    int currentTurn = 0;
    while (currentMatchState.getMatchStatus().getStatus() == Status.IN_PROGRESS) {
      Turn aiTurn;
      if (currentMatchState.getActivePlayerId() == PlayerId.ONE) {
        aiTurn = alphaBetaAi.calculateBestTurn(currentMatchState);
      } else {
        aiTurn = randomAi.calculateBestTurn(currentMatchState);
      }

      currentMatchState = currentMatchState.doTurn(aiTurn, enactor);
      System.out.println("Match after turn " + currentTurn);
      System.out.println(matchFormatter.matchToString(currentMatchState));
      System.out.println("\n\n");
      log.trace(matchFormatter.matchToString(currentMatchState));

      currentTurn++;
    }

    System.out.println(
        "\n\n== Winner: " + currentMatchState.getMatchStatus().getVictor().toString() + "!");

    show();
  }
}
