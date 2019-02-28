package com.shepherdjerred.capstone.tui.menu;

import com.shepherdjerred.capstone.tui.io.Utils;
import java.util.Scanner;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SinglePlayerMenu implements Menu {

  private final Scanner scanner;

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
          var twoPlayer = new GameMenu(scanner, Utils.createMatch());
          twoPlayer.show();
          break;

        case 2:
          var mainMenu = new MainMenu(scanner);
          mainMenu.show();
          break;

        default:
          System.out.println("Please enter the number of a valid option.\n");
          show();
      }
    } else {
      System.out.println("Please enter a number.\n");
      show();
    }
  }

  private void print() {
    System.out.println("\n~ Choose an option: ~");
    System.out.println("1.) Two-Player match.");
    System.out.println("2.) Back.");
  }
}
