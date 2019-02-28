package com.shepherdjerred.capstone.tui.menu;

import com.shepherdjerred.capstone.tui.io.Utils;

public class MainMenu implements Menu {

  @Override
  public void show() {
    print();
    handleInput();
  }

  private void print() {
    System.out.println("Welcome to " + Utils.GAME_NAME + "!\n");

    System.out.println("~ Choose an option: ~");
    System.out.println("1.) Single-Player.");
    System.out.println("2.) Demo storage");
    System.out.println("3.) Demo AI");
    System.out.println("4.) Demo server");
    System.out.println("5.) Exit.\n");
  }

  private void handleInput() {
    String s = Utils.getUserInput();

    int value = Utils.stringToInt(s);
    if (value != 0) {
      switch (value) {
        case 1:
          var singlePlayerMenu = new SinglePlayerMenu();
          singlePlayerMenu.show();
          break;

        case 2:
          System.exit(0);
          break;

        default:
          System.out.println("Please enter the number of a valid option.\n");
          print();
      }
    } else {
      System.out.println("Please enter a number.\n");
      print();
    }
  }
}
