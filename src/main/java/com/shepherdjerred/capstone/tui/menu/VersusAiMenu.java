package com.shepherdjerred.capstone.tui.menu;

import com.shepherdjerred.capstone.ai.ab.AlphaBetaQuoridorAi;
import com.shepherdjerred.capstone.ai.ab.evaluator.DefaultMatchEvaluator;
import com.shepherdjerred.capstone.logic.board.Coordinate;
import com.shepherdjerred.capstone.logic.match.Match;
import com.shepherdjerred.capstone.logic.turn.MovePawnTurn;
import com.shepherdjerred.capstone.logic.turn.MovePawnTurn.MoveType;
import com.shepherdjerred.capstone.logic.turn.PlaceWallTurn;
import com.shepherdjerred.capstone.logic.turn.enactor.MatchTurnEnactor;
import com.shepherdjerred.capstone.logic.turn.enactor.TurnEnactorFactory;
import com.shepherdjerred.capstone.logic.turn.validator.TurnValidator;
import com.shepherdjerred.capstone.logic.util.MatchFormatter;
import com.shepherdjerred.capstone.storage.save.FilesystemSavedGameRepository;
import com.shepherdjerred.capstone.tui.io.TurnParser;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Scanner;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class VersusAiMenu implements Menu {

  private final Scanner scanner;
  private Match match;

  @Override
  public void show() {
    print();
    handleInput();
  }

  private void print() {
    var matchFormatter = new MatchFormatter();
    System.out.println(matchFormatter.matchToString(match));
    System.out.println("Current turn: " + match.getActivePlayerId());
    System.out.println(
        "Enter your turn in Quoridor board notation (i.e. a1 or a1h), UNDO, SAVE, or EXIT");
  }

  private void handleInput() {
    var validator = new TurnValidator();
    var enactor = new MatchTurnEnactor(new TurnEnactorFactory(), new TurnValidator());
    var turnParser = new TurnParser();
    var input = scanner.next();

    if (input.equals("UNDO")) {
      match = match.getMatchHistory().pop();
      show();
      return;
    }

    if (input.equals("SAVE")) {
      var path = Paths.get("").toAbsolutePath();
      var repository = new FilesystemSavedGameRepository(path);
      try {
        repository.saveMatch(Instant.now().toString(), match);
      } catch (IOException e) {
        System.out.println("Error saving game");
        log.error(e);
      }
      show();
      return;
    }

    if (input.equals("EXIT")) {
      var mainMenu = new MainMenu(scanner);
      mainMenu.show();
      return;
    }

    try {
      var turn = turnParser.parse(input);

      if (turn instanceof MovePawnTurn) {
        MoveType type;
        var src = match.getBoard().getPawnLocation(match.getActivePlayerId());
        var dest = ((MovePawnTurn) turn).getDestination();

        if (Coordinate.calculateManhattanDistance(src, dest) > 2) {
          if (Coordinate.areCoordinatesDiagonal(src, dest)) {
            type = MoveType.JUMP_DIAGONAL;
          } else {
            type = MoveType.JUMP_STRAIGHT;
          }
        } else {
          type = MoveType.NORMAL;
        }

        turn = new MovePawnTurn(match.getActivePlayerId(),
            type,
            src,
            ((MovePawnTurn) turn).getDestination());
      }

      if (turn instanceof PlaceWallTurn) {
        turn = new PlaceWallTurn(match.getActivePlayerId(),
            ((PlaceWallTurn) turn).getFirstCoordinate(),
            ((PlaceWallTurn) turn).getSecondCoordinate());
      }

      var validatorOutput = validator.isTurnValid(turn, match);
      if (validatorOutput.isError()) {
        System.out.println("Invalid turn! try again");
        System.out.println(validatorOutput);
        System.out.println(turn);

        show();
        return;
      } else {
        match = match.doTurn(turn, enactor);
      }
    } catch (IllegalArgumentException e) {
      System.out.println("Could not convert input to turn; try again!");

      show();
      return;
    }

    print();

    var alphaBetaAi = new AlphaBetaQuoridorAi(new DefaultMatchEvaluator(), 2);
    var aiTurn = alphaBetaAi.calculateBestTurn(match);
    match = match.doTurn(aiTurn, enactor);

    show();
  }
}
