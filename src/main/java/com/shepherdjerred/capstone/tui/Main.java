package com.shepherdjerred.capstone.tui;

import com.shepherdjerred.capstone.tui.menu.MainMenu;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    var scanner = new Scanner(System.in);
    var mainMenu = new MainMenu(scanner);
    mainMenu.show();
  }
}
