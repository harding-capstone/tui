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
import com.shepherdjerred.capstone.ai.alphabeta.pruning.PruningAlphaBetaQuoridorAi;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.DeepWallNodePruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.NodePruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.PieceDistanceNodePruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.RandomDiscardNodePruningRule;
import com.shepherdjerred.capstone.ai.evaluator.EvaluatorWeights;
import com.shepherdjerred.capstone.ai.evaluator.MatchEvaluator;
import com.shepherdjerred.capstone.ai.evaluator.WeightedMatchEvaluator;
import com.shepherdjerred.capstone.logic.board.BoardSettings;
import com.shepherdjerred.capstone.logic.match.MatchSettings;
import com.shepherdjerred.capstone.logic.player.PlayerCount;
import com.shepherdjerred.capstone.logic.player.QuoridorPlayer;
import com.shepherdjerred.capstone.tui.AiSettings;
import com.shepherdjerred.capstone.tui.GameSettings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

public class NewMatchWindow extends BasicWindow {

  private int wallsPerPlayer = 10;
  private QuoridorPlayer startingPlayer = QuoridorPlayer.ONE;
  private PlayerCount playerCount = PlayerCount.TWO;
  private int boardSize = 9;
  private Set<QuoridorPlayer> aiPlayers = new HashSet<>();

  public NewMatchWindow() {
    super("New Match");

    var panel = new Panel();
    panel.setLayoutManager(new GridLayout(2));

    var wallsPerPlayerLabel = new Label("Walls Per Player");
    var wallsPerPlayerTextBox = new TextBox().setText("10").setValidationPattern(getIntPattern());
    panel.addComponent(wallsPerPlayerLabel);
    panel.addComponent(wallsPerPlayerTextBox);

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

    var boardSizeLabel = new Label("Board Size");
    var boardSizeTextBox = new TextBox("9").setValidationPattern(getOddIntPattern());
    panel.addComponent(boardSizeLabel);
    panel.addComponent(boardSizeTextBox);

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

    panel.addComponent(new Button("Exit", this::close));
    setComponent(panel);

    var startButton = new Button("Start Match", () -> {
      this.wallsPerPlayer = Integer.parseInt(wallsPerPlayerTextBox.getText());
      this.boardSize = Integer.parseInt(boardSizeTextBox.getText());
      var gui = getTextGUI();
      if (startingPlayer.toInt() > playerCount.toInt()) {
        new MessageDialogBuilder()
            .setTitle("Error")
            .setText("Starting player is not in this game")
            .build()
            .showDialog(gui);
        return;
      }
      if (aiPlayers.stream().anyMatch(player -> player.toInt() > playerCount.toInt())) {
        new MessageDialogBuilder()
            .setTitle("Error")
            .setText("AI player is not in this game")
            .build()
            .showDialog(gui);
        return;
      }

      var aiMap = getAiMap();

      if (aiMap.values().stream().anyMatch(Objects::isNull)) {
        new MessageDialogBuilder()
            .setTitle("Error")
            .setText("Error getting AI settings")
            .build()
            .showDialog(gui);
        return;
      }

      var gameSettings = new GameSettings(getMatchSettings(), getBoardSettings(), aiMap);
      var window = new MatchWindow(gameSettings);
      gui.addWindowAndWait(window);
      close();
    });
    panel.addComponent(startButton);
  }

  private final Optional<QuoridorAi> getQuoridorAi(QuoridorPlayer quoridorPlayer) {
    var aiSettings = new AiSettings(null, quoridorPlayer);

    new ActionListDialogBuilder()
        .setTitle("AI for " + quoridorPlayer)
        .setDescription("Choose an item")
        .addAction("Use best current AI", () -> {
          var evaluatorWeights = new EvaluatorWeights(
              2293.7109999771455,
              398.7527547140071,
              4762.159725078656,
              9407.150981288025,
              -6985.356279833557
          );

          MatchEvaluator matchEvaluator = new WeightedMatchEvaluator(evaluatorWeights);

          Set<NodePruningRule> pruningRules = new HashSet<>();
          pruningRules.add(new RandomDiscardNodePruningRule(50));
          pruningRules.add(new DeepWallNodePruningRule(2));
          pruningRules.add(new PieceDistanceNodePruningRule(3));

          var quoridorAi = new PruningAlphaBetaQuoridorAi(matchEvaluator, 4, pruningRules);

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

  private final Map<QuoridorPlayer, QuoridorAi> getAiMap() {
    Map<QuoridorPlayer, QuoridorAi> aiMap = new HashMap<>();
    aiPlayers.forEach(player -> {
      var ai = getQuoridorAi(player);
      if (ai.isPresent()) {
        aiMap.put(player, ai.get());
      } else {
        aiMap.put(player, null);
      }
    });
    return aiMap;
  }

  private BoardSettings getBoardSettings() {
    return new BoardSettings(boardSize, playerCount);
  }

  private MatchSettings getMatchSettings() {
    return new MatchSettings(wallsPerPlayer, startingPlayer, playerCount);
  }

  private Pattern getOddIntPattern() {
    return Pattern.compile("[13579]+");
  }

  private Pattern getIntPattern() {
    return Pattern.compile("\\d+");
  }
}
