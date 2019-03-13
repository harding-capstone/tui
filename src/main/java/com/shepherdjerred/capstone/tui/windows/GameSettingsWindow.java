package com.shepherdjerred.capstone.tui.windows;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.CheckBoxList;
import com.googlecode.lanterna.gui2.ComboBox;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.shepherdjerred.capstone.ai.QuoridorAi;
import com.shepherdjerred.capstone.logic.board.BoardSettings;
import com.shepherdjerred.capstone.logic.match.MatchSettings;
import com.shepherdjerred.capstone.logic.player.PlayerCount;
import com.shepherdjerred.capstone.logic.player.QuoridorPlayer;
import com.shepherdjerred.capstone.tui.AiProvider;
import com.shepherdjerred.capstone.tui.AiSettings;
import com.shepherdjerred.capstone.tui.GameSettings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class GameSettingsWindow extends BasicWindow {

  private QuoridorPlayer startingPlayer = QuoridorPlayer.ONE;
  private PlayerCount playerCount = PlayerCount.TWO;
  private Set<QuoridorPlayer> aiPlayers = new HashSet<>();
  private TextBox wallsPerPlayerTextBox;
  private TextBox boardSizeTextBox;

  public GameSettingsWindow() {
    super.setTitle("New Match");
    var panel = createPanel();
    super.setComponent(panel);
  }

  private Panel createPanel() {
    var layout = new GridLayout(2);
    layout.setHorizontalSpacing(5);
    layout.setVerticalSpacing(1);
    var panel = new Panel(layout);
    initializePanelComponents(panel);
    return panel;
  }

  private void initializePanelComponents(Panel panel) {
    createWallsPerPlayerField(panel);
    createStartingPlayerField(panel);
    createPlayerCountField(panel);
    createBoardSizeField(panel);
    createAiPlayersField(panel);
    createBackButton(panel);
    createStartButton(panel);
  }

  private void createWallsPerPlayerField(Panel panel) {
    var wallsPerPlayerLabel = new Label("Walls Per Player");
    wallsPerPlayerTextBox = new TextBox().setText("10").setValidationPattern(getIntPattern());
    panel.addComponent(wallsPerPlayerLabel);
    panel.addComponent(wallsPerPlayerTextBox);
  }

  private void createStartingPlayerField(Panel panel) {
    var startingPlayerLabel = new Label("Starting Player");
    var startingPlayerComboBox = new ComboBox<QuoridorPlayer>()
        .addItem(QuoridorPlayer.ONE)
        .addItem(QuoridorPlayer.TWO)
        .addItem(QuoridorPlayer.THREE)
        .addItem(QuoridorPlayer.FOUR);
    startingPlayerComboBox.addListener((current, prev) -> startingPlayer = QuoridorPlayer.fromInt(
        current + 1));
    startingPlayerComboBox.setSelectedIndex(0);
    panel.addComponent(startingPlayerLabel);
    panel.addComponent(startingPlayerComboBox);
  }

  private void createPlayerCountField(Panel panel) {
    var playerCountLabel = new Label("Player Count");
    var playerCountComboBox = new ComboBox<PlayerCount>();
    playerCountComboBox.addItem(PlayerCount.TWO);
    playerCountComboBox.addItem(PlayerCount.FOUR);
    playerCountComboBox.addListener((current, prev) -> {
      if (current == 0) {
        playerCount = PlayerCount.TWO;
      } else if (current == 1) {
        playerCount = PlayerCount.FOUR;
      } else {
        throw new UnsupportedOperationException();
      }
    });
    playerCountComboBox.setSelectedIndex(0);
    panel.addComponent(playerCountLabel);
    panel.addComponent(playerCountComboBox);
  }

  private void createBoardSizeField(Panel panel) {
    var boardSizeLabel = new Label("Board Size");
    boardSizeTextBox = new TextBox("9").setValidationPattern(getOddIntPattern());
    panel.addComponent(boardSizeLabel);
    panel.addComponent(boardSizeTextBox);
  }

  private void createAiPlayersField(Panel panel) {
    var aiPlayersLabel = new Label("AI Players");
    var aiPlayersCheckBoxes = new CheckBoxList<QuoridorPlayer>();
    aiPlayersCheckBoxes.addItem(QuoridorPlayer.ONE);
    aiPlayersCheckBoxes.addItem(QuoridorPlayer.TWO);
    aiPlayersCheckBoxes.addItem(QuoridorPlayer.THREE);
    aiPlayersCheckBoxes.addItem(QuoridorPlayer.FOUR);
    aiPlayersCheckBoxes.addListener((index, isChecked) -> {
      if (isChecked) {
        aiPlayers.add(QuoridorPlayer.fromInt(index + 1));
      } else {
        aiPlayers.remove(QuoridorPlayer.fromInt(index + 1));
      }
    });
    panel.addComponent(aiPlayersLabel);
    panel.addComponent(aiPlayersCheckBoxes);
  }

  private void createBackButton(Panel panel) {
    panel.addComponent(new Button("Back", super::close));
  }

  private int getWallsPerPlayer() {
    return Integer.parseInt(wallsPerPlayerTextBox.getText());
  }

  private int getBoardSize() {
    return Integer.parseInt(boardSizeTextBox.getText());
  }

  private boolean isAnyFieldInvalid() {
    var gui = super.getTextGUI();
    if (startingPlayer.toInt() > playerCount.toInt()) {
      new MessageDialogBuilder()
          .setTitle("Error")
          .setText("Starting player is not in this game")
          .build()
          .showDialog(gui);
      return true;
    }
    if (aiPlayers.stream().anyMatch(player -> player.toInt() > playerCount.toInt())) {
      new MessageDialogBuilder()
          .setTitle("Error")
          .setText("AI player is not in this game")
          .build()
          .showDialog(gui);
      return true;
    }
    return false;
  }

  private void createStartButton(Panel panel) {
    var startButton = new Button("Start Match", () -> {
      if (isAnyFieldInvalid()) {
        return;
      }

      var gui = super.getTextGUI();
      var aiMap = getPlayerToAiMap();

      if (aiMap.values().stream().anyMatch(Objects::isNull)) {
        new MessageDialogBuilder()
            .setTitle("Error")
            .setText("Error getting AI settings")
            .build()
            .showDialog(gui);
        return;
      }

      var gameSettings = new GameSettings(createMatchSettings(), createBoardSettings(), aiMap);
      var window = new MatchWindow(gameSettings);
      gui.addWindowAndWait(window);
//      super.close();
    });

    panel.addComponent(startButton);
  }

  private Optional<QuoridorAi> promptForQuoridorAi(QuoridorPlayer quoridorPlayer) {
    var aiSettings = new AiSettings(null, quoridorPlayer);

    new ActionListDialogBuilder()
        .setTitle("AI for " + quoridorPlayer)
        .setDescription("Choose an item")
        .addAction("Use default AI", () -> {
          var quoridorAi = new AiProvider().getDefaultQuoridorAi();
          aiSettings.setQuoridorAi(quoridorAi);
        })
        .build()
        .showDialog(getTextGUI());

    var ai = aiSettings.getQuoridorAi();
    if (ai == null) {
      return Optional.empty();
    } else {
      return Optional.of(ai);
    }
  }

  private Map<QuoridorPlayer, QuoridorAi> getPlayerToAiMap() {
    Map<QuoridorPlayer, QuoridorAi> aiMap = new HashMap<>();
    aiPlayers.forEach(player -> {
      var ai = promptForQuoridorAi(player);
      if (ai.isPresent()) {
        aiMap.put(player, ai.get());
      } else {
        aiMap.put(player, null);
      }
    });
    return aiMap;
  }

  private BoardSettings createBoardSettings() {
    return new BoardSettings(getBoardSize(), playerCount);
  }

  private MatchSettings createMatchSettings() {
    return new MatchSettings(getWallsPerPlayer(), startingPlayer, playerCount);
  }

  private Pattern getOddIntPattern() {
    return Pattern.compile("[13579]+");
  }

  private Pattern getIntPattern() {
    return Pattern.compile("\\d+");
  }
}
