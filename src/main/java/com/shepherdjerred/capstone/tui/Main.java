package com.shepherdjerred.capstone.tui;

import com.shepherdjerred.capstone.logic.Player;
import com.shepherdjerred.capstone.logic.board.BoardSettings;
import com.shepherdjerred.capstone.logic.board.Coordinate;
import com.shepherdjerred.capstone.logic.match.Match;
import com.shepherdjerred.capstone.logic.match.MatchSettings;
import com.shepherdjerred.capstone.logic.match.MatchSettings.PlayerCount;
import com.shepherdjerred.capstone.logic.turn.MovePawnTurn;
import com.shepherdjerred.capstone.logic.turn.PlaceWallTurn;
import com.shepherdjerred.capstone.logic.turn.enactor.DefaultTurnEnactorFactory;
import com.shepherdjerred.capstone.logic.turn.validator.DefaultTurnValidatorFactory;
import com.shepherdjerred.capstone.logic.util.MatchFormatter;
import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    var boardSettings = new BoardSettings(9);
    var matchSettings = new MatchSettings(10, boardSettings, PlayerCount.TWO, Player.ONE);

    var matchState = new Match(
        matchSettings,
        DefaultTurnEnactorFactory.INSTANCE,
        DefaultTurnValidatorFactory.INSTANCE
    );

    var turn1 = new MovePawnTurn(Player.ONE, new Coordinate(8, 0), new Coordinate(8, 2));
    var turn2 = new MovePawnTurn(Player.TWO, new Coordinate(8, 16), new Coordinate(8, 14));
    var turn3 = new PlaceWallTurn(Player.ONE, new Coordinate(8, 13), new Coordinate(6, 13));

    var match1 = matchState.doTurn(turn1);
    var match2 = match1.doTurn(turn2);
    var match3 = match2.doTurn(turn3);

    var matchFormatter = MatchFormatter.INSTANCE;

    List<Match> matchStates = new ArrayList<>();
    matchStates.add(matchState);
    matchStates.add(match1);
    matchStates.add(match2);
    matchStates.add(match3);

    System.out.println(matchFormatter.matchesToString(matchStates));
  }
}
