package com.shepherdjerred.capstone.tui;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.shepherdjerred.capstone.tui.windows.MainWindow;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

  public static void main(String[] args) throws IOException {
    try {
      Terminal term = new DefaultTerminalFactory().createTerminal();
      Screen screen = new TerminalScreen(term);

      WindowBasedTextGUI gui = new MultiWindowTextGUI(screen);
      screen.startScreen();

      var mainWindow = new MainWindow();
      gui.addWindowAndWait(mainWindow);

      screen.stopScreen();
    } catch (Exception e) {
      log.error("Error", e);
      throw e;
    }
  }
}
