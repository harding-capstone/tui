package com.shepherdjerred.capstone.tui.windows;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;
import com.shepherdjerred.capstone.ai.QuoridorAi;
import com.shepherdjerred.capstone.logic.board.BoardSettings;
import com.shepherdjerred.capstone.logic.match.MatchSettings;
import com.shepherdjerred.capstone.logic.player.PlayerCount;
import com.shepherdjerred.capstone.logic.player.QuoridorPlayer;
import com.shepherdjerred.capstone.tui.AiProvider;
import com.shepherdjerred.capstone.tui.GameSettings;
import java.util.HashMap;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class MainWindow extends BasicWindow {

  public MainWindow() {
    super.setTitle("Main Menu");
    var panel = createPanel();
    super.setComponent(panel);
  }

  private Panel createPanel() {
    var layout = new LinearLayout(Direction.VERTICAL);
    var panel = new Panel(layout);
    initializePanelComponents(panel);
    return panel;
  }

  private void initializePanelComponents(Panel panel) {
    createAndAddWelcomeLabel(panel);
    createAndAddPlayButton(panel);
    createAndQuickPlayButton(panel);
    createAndAddExitButton(panel);
  }

  private void createAndAddWelcomeLabel(Panel panel) {
    var label = new Label("Welcome to Castle Casters!");
    panel.addComponent(label);
  }

  private void createAndAddPlayButton(Panel panel) {
    var button = new Button("Play a custom game", () -> {
      var matchSettingsWindow = new GameSettingsWindow();
      super.getTextGUI().addWindowAndWait(matchSettingsWindow);
    });
    panel.addComponent(button);
  }

  private void createAndQuickPlayButton(Panel panel) {
    var button = new Button("Quick Play", () -> {
      var matchWindow = new MatchWindow(getDefaultGameSettings());
      super.getTextGUI().addWindowAndWait(matchWindow);
    });
    panel.addComponent(button);
  }

  private void createAndAddExitButton(Panel panel) {
    var button = new Button("Exit", this::close);
    panel.addComponent(button);
  }

  private GameSettings getDefaultGameSettings() {
    var matchSettings = new MatchSettings(10, QuoridorPlayer.ONE, PlayerCount.TWO);
    var boardSettings = new BoardSettings(9, PlayerCount.TWO);
    var aiMap = new HashMap<QuoridorPlayer, QuoridorAi>();
    aiMap.put(QuoridorPlayer.TWO, new AiProvider().getDefaultQuoridorAi());
    return new GameSettings(matchSettings, boardSettings, aiMap);
  }
}
