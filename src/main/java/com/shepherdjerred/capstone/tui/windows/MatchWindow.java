package com.shepherdjerred.capstone.tui.windows;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.shepherdjerred.capstone.logic.board.BoardFormatter;
import com.shepherdjerred.capstone.logic.match.Match;
import com.shepherdjerred.capstone.logic.match.MatchStatus.Status;
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
    this.gameSettings = gameSettings;

    var layout = new LinearLayout(Direction.HORIZONTAL);
    var panel = new Panel(layout);
    init(panel);
    super.setComponent(panel);
  }

  private void init(Panel panel) {
    match = createMatch();

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

    var startButton = new Button("Start Game", this::run);
    panel.addComponent(startButton);
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

  private void updateInterface() {
    updateMatchLabel();
    updateActivePlayerLabel();
  }

  public void run() {
    var aiMap = gameSettings.getAiPlayers();

    updateInterface();

    while (match.getMatchStatus().getStatus() == Status.IN_PROGRESS) {

      Turn turn;
      if (aiMap.containsKey(match.getActivePlayerId())) {
        var start = Instant.now();
        turn = getAiTurn();
        var end = Instant.now();
        var dur = Duration.between(start, end);
        aiTimeTakenLabel.setText("AI Time: " + dur.toMillis() / 1000 + "s");
        var converter = new TurnToNotationConverter();
        aiMoveLabel.setText("AI Move: " + converter.convert(turn));
      } else {
        var player = match.getActivePlayerId();
        var converter = new NotationToTurnConverter();

        do {
          var turnString = TextInputDialog.showDialog(getTextGUI(),
              "Enter move for player " + player.toInt(),
              "Your turn should be in Quoridor notation",
              "");
          turn = converter.convert(turnString);
        } while (turn == null);

        if (turn instanceof MovePawnTurn) {
          turn = new NormalMovePawnTurn(player, null, ((MovePawnTurn) turn).getDestination());
        } else if (turn instanceof PlaceWallTurn) {
          turn = new PlaceWallTurn(player, ((PlaceWallTurn) turn).getLocation());
        }
      }
      match = match.doTurnUnchecked(turn);
      updateInterface();
    }

    new MessageDialogBuilder()
        .setTitle("Game Over")
        .setText(match.getMatchStatus().getVictor() + " won!")
        .addButton(MessageDialogButton.Close)
        .build()
        .showDialog(getTextGUI());

    close();
  }

  private Turn getAiTurn() {
    var player = match.getActivePlayerId();
    var ai = gameSettings.getAiPlayers().get(player);
    if (ai == null) {
      log.error("No AI");
    }
    return ai.calculateBestTurn(match);
  }
}
