package com.shepherdjerred.capstone.tui.windows;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.Panel;

public class MainWindow extends BasicWindow {

  public MainWindow() {
    super("Main Menu");

    var panel = new Panel();
    panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
    panel.addComponent(new Label("Welcome to Castle Casters!"));
    panel.addComponent(new Button("Play a game", () -> {
      var matchSettingsWindow = new NewMatchWindow();
      getTextGUI().addWindowAndWait(matchSettingsWindow);
      close();
    }));
    panel.addComponent(new Button("Exit", this::close));
    setComponent(panel);
  }
}
