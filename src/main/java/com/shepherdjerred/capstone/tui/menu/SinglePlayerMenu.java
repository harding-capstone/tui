package com.shepherdjerred.capstone.tui.menu;

import com.shepherdjerred.capstone.tui.io.Utils;

public class SinglePlayerMenu implements Menu {

  @Override
  public void show() {
    print();
    handleInput();
  }

  private void handleInput() {
    String s = Utils.getUserInput();

    int value = Utils.stringToInt(s);
    if (value != 0) {
      switch (value) {
        case 1:
          var gameMenu = new GameMenu();
          gameMenu.show();
          break;

        case 2:
          break;

        case 3:
          break;

        default:
          System.out.println("Please enter the number of a valid option.\n");
          // TODO: Display menu again...
      }
    } else {
      System.out.println("Please enter a number.\n");
      // TODO: Display menu again...
    }
  }

  private void print() {
    System.out.println("\n~ Choose an option: ~");
    System.out.println("1.) Two-Player match.");
    System.out.println("2.) Four-Player match.");
    System.out.println("3.) Back.");
  }
}
