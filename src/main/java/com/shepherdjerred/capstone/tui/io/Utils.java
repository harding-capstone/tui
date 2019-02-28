package com.shepherdjerred.capstone.tui.io;

import java.util.Scanner;

public class Utils {

  public static final String GAME_NAME = "Castle Casters";

  public static int stringToInt(String s) {
    int value;
    value = Integer.parseInt((s));
    return value;
  }

  public static String getUserInput() {
    Scanner scan = new Scanner(System.in);
    String s = scan.next();
    return s;
  }

}
