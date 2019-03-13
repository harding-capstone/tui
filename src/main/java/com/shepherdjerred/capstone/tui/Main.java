package com.shepherdjerred.capstone.tui;

import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.TextGUIThread.ExceptionHandler;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.shepherdjerred.capstone.tui.windows.MainWindow;
import java.io.IOException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {

  public static void main(String[] args) throws IOException {
    showMainWindow();
  }

  private static void showMainWindow() throws IOException {
    var terminal = new DefaultTerminalFactory().createTerminal();
    var screen = new TerminalScreen(terminal);

    var multiWindowGui = new MultiWindowTextGUI(screen);
    multiWindowGui.getGUIThread().setExceptionHandler(new ExceptionHandler() {
      @Override
      public boolean onIOException(IOException e) {
        log.error(e);
        return false;
      }

      @Override
      public boolean onRuntimeException(RuntimeException e) {
        log.error(e);
        return false;
      }
    });

    screen.startScreen();

    var mainWindow = new MainWindow();
    multiWindowGui.addWindowAndWait(mainWindow);

    screen.stopScreen();
  }
}
