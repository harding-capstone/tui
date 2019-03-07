package com.shepherdjerred.capstone.tui.windows;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.shepherdjerred.capstone.logic.board.BoardFormatter;
import com.shepherdjerred.capstone.logic.match.Match;
import com.shepherdjerred.capstone.logic.turn.MovePawnTurn;
import com.shepherdjerred.capstone.logic.turn.NormalMovePawnTurn;
import com.shepherdjerred.capstone.logic.turn.PlaceWallTurn;
import com.shepherdjerred.capstone.logic.turn.Turn;
import com.shepherdjerred.capstone.logic.turn.notation.NotationToTurnConverter;
import com.shepherdjerred.capstone.logic.turn.notation.TurnToNotationConverter;
import com.shepherdjerred.capstone.tui.GameSettings;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MatchWindow extends BasicWindow {

  private final GameSettings gameSettings;
  private Match match;
  private Label activePlayerLabel;
  private Label boardLabel;
  private Label aiTimeTakenLabel;
  private Label aiMoveLabel;

  public MatchWindow(GameSettings gameSettings) {
    super("Match");

    if (getTextGUI() == null) {
      log.error("GUI IS NULL");
    }

    this.gameSettings = gameSettings;
    match = createMatch();

    var panel = new Panel();
    panel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));

    var detailsPanel = new Panel();
    detailsPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
    panel.addComponent(detailsPanel);

    activePlayerLabel = new Label("");
    detailsPanel.addComponent(activePlayerLabel);

    aiTimeTakenLabel = new Label("AI Time:");
    detailsPanel.addComponent(aiTimeTakenLabel);

    aiMoveLabel = new Label("AI Move:");
    detailsPanel.addComponent(aiMoveLabel);

    boardLabel = new Label("");
    panel.addComponent(boardLabel);

    setComponent(panel);



    try {
      run();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private Match createMatch() {
    return Match.from(gameSettings.getMatchSettings(), gameSettings.getBoardSettings());
  }

  private void updateMatchLabel() {
    var formatter = new BoardFormatter();
    boardLabel.setText(formatter.boardToString(match.getBoard()));
  }

  private void updateActivePlayerLabel() {
    var player = match.getActivePlayerId();
    if (gameSettings.getAiPlayers().containsKey(player)) {
      activePlayerLabel.setText(player + " (AI)");
    } else {
      activePlayerLabel.setText(player + " (Human)");
    }
  }

  private void run() throws InterruptedException {
    var aiMap = gameSettings.getAiPlayers();

//    final long secondsPerFrame = 1000 / 30;
//    while (match.getMatchStatus().getStatus() == Status.IN_PROGRESS) {
//      var time = System.currentTimeMillis();
//
    updateMatchLabel();
    updateActivePlayerLabel();
    if (aiMap.containsKey(match.getActivePlayerId())) {
      handleAiTurn();
    } else {
      handlePlayerTurn();
    }
//
//      Thread.sleep((time + secondsPerFrame) - System.currentTimeMillis());
//    }

//    new MessageDialogBuilder()
//        .setTitle("Game Over")
//        .setText(match.getMatchStatus().getVictor() + " won!")
//        .addButton(MessageDialogButton.Close)
//        .build()
//        .showDialog(getTextGUI());
//
//    close();
  }

  private void handleAiTurn() {
    var player = match.getActivePlayerId();
    var ai = gameSettings.getAiPlayers().get(player);
    if (ai == null) {
      log.error("No AI");
    }
    var start = Instant.now();
    var turn = ai.calculateBestTurn(match);
    var end = Instant.now();
    match = match.doTurnUnchecked(turn);
    var dur = Duration.between(start, end);
    aiTimeTakenLabel.setText("AI Time: " + dur.toMillis() / 1000 + "s");
    var converter = new TurnToNotationConverter();
    aiMoveLabel.setText("AI Move: " + converter.convert(turn));
  }

  private void handlePlayerTurn() {
    var player = match.getActivePlayerId();
    var converter = new NotationToTurnConverter();
    Turn turn;



    do {
      var turnString = TextInputDialog.showDialog(getTextGUI(), "Enter Move", "", "");
      turn = converter.convert(turnString);
    } while (turn == null);

    if (turn instanceof MovePawnTurn) {
      turn = new NormalMovePawnTurn(player, null, ((MovePawnTurn) turn).getDestination());
    } else if (turn instanceof PlaceWallTurn) {
      turn = new PlaceWallTurn(player, ((PlaceWallTurn) turn).getLocation());
    }

    match = match.doTurnUnchecked(turn);
  }
}
