package com.shepherdjerred.capstone.tui;

import com.shepherdjerred.capstone.logic.board.Coordinate;
import com.shepherdjerred.capstone.logic.player.PlayerId;
import com.shepherdjerred.capstone.logic.turn.MovePawnTurn;
import com.shepherdjerred.capstone.logic.turn.MovePawnTurn.MoveType;
import com.shepherdjerred.capstone.logic.turn.PlaceWallTurn;
import com.shepherdjerred.capstone.logic.turn.Turn;
import java.util.Scanner;

public class Main
{
  private static final String GAME_NAME = "Castle Casters";

  private void displayMainMenu()
  {
    Main main = new Main();

    System.out.println("Welcome to " + GAME_NAME + "!\n");

    System.out.println("~ Choose an option: ~");
    System.out.println("1.) Single-Player.");
    System.out.println("2.) Multi-Player.");
    System.out.println("3.) Exit.\n");

    String s = getUserInput();

    int value = main.stringToInt(s);
    if (value != 0)
    {
      switch (value)
      {
        case 1:
          main.displaySinglePlayerMenu();
          break;

        case 2:
          break;

        case 3:
          System.exit(0);
          break;

        default:
          System.out.println("Please enter the number of a valid option.\n");
          // TODO: Display menu again...
      }
    }
    else
    {
      System.out.println("Please enter a number.\n");
      // TODO: Display menu again...
    }
  }

  private void displaySinglePlayerMenu()
  {
    Main main = new Main();

    System.out.println("\n~ Choose an option: ~");
    System.out.println("1.) Two-Player match.");
    System.out.println("2.) Four-Player match.");
    System.out.println("3.) Back.");
    System.out.println("4.) Exit.\n");

    String s = getUserInput();

    int value = main.stringToInt(s);
    if (value != 0)
    {
      switch (value)
      {
        case 1:
          main.startTwoPlayerMatch();
          break;

        case 2:
          break;

        case 3:

          break;

        case 4:
          System.exit(0);
          break;

        default:
          System.out.println("Please enter the number of a valid option.\n");
          // TODO: Display menu again...
      }
    }
    else
    {
      System.out.println("Please enter a number.\n");
      // TODO: Display menu again...
    }
  }

  private int stringToInt(String s)
  {
    int value;

    try
    {
      value = Integer.parseInt((s));
    }
    catch (NumberFormatException e)
    {
      value = 0;
    }

    return value;
  }

  private void startTwoPlayerMatch()
  {
    // TODO: Create match with default settings...

    System.out.println("\nTwo-Player match started...");
    System.out.println("Player 1 begins first.\n");

    System.out.println("Enter a move: (using format letter [a-i], row number [1-9], h/v wall [optional])");

    String s = getUserInput();
    stringToTurn(s);
  }


  private String getUserInput()
  {
    Scanner scan = new Scanner(System.in);
    String s = scan.next();
    return s;
  }

  private Turn stringToTurn(String move)
  {
    int x = 0;
    int y = 0;

    Turn turn = null;

    // Would be nice here to trim the string if extra characters were typed by mistake, then the following code executes...
    if(move.matches("[a-iA-I]+[1-9]+[h, v]?"))
    {
      //System.out.println(move);

      // MOVE:
      // Examine first character and return proper int
      // Examine second character and return proper int

      char c1 = Character.toLowerCase(move.charAt(0));
      switch (c1)
      {
        case 'a':
          x = 0;
          break;
        case 'b':
          x = 2;
        case 'c':
          x = 4;
          break;
        case 'd':
          x = 6;
          break;
        case 'e':
          x = 8;
          break;
        case 'f':
          x = 10;
          break;
        case 'g':
          x = 12;
          break;
        case 'h':
          x = 14;
          break;
        case 'i':
          x = 16;
          break;
        default:
          System.out.println(x);
      }

      // Debug:
      System.out.println(x);

      char c2 = move.charAt(1);
      int number = c2 - '0';

      switch (number)
      {
        case 1:
          y = 0;
          break;
        case 2:
          y = 2;
          break;
        case 3:
          y = 4;
          break;
        case 4:
          y = 6;
          break;
        case 5:
          y = 8;
          break;
        case 6:
          y = 10;
          break;
        case 7:
          y = 12;
          break;
        case 8:
          y = 14;
          break;
        case 9:
          y = 16;
          break;
        default:
          System.out.println(y);
      }

      // Debug:
      System.out.println(y);

      // WALL:
      // If the length of string is 3, examine if horizontal or vertical wall
      // Place wall

      if(move.length() == 3)
      {
        if(move.endsWith("h"))
        {
          System.out.println("Horizontal wall!");
          Coordinate coordinate1 = new Coordinate(x, y + 1);
          Coordinate coordinate2 = new Coordinate(x + 2, y + 1);
          turn = new PlaceWallTurn(PlayerId.NULL, coordinate1, coordinate2);
        }
        else
          {
          System.out.println("Vertical wall!");
          Coordinate coordinate1 = new Coordinate(x + 1, y);
          Coordinate coordinate2 = new Coordinate(x + 1, y + 2);
          turn = new PlaceWallTurn(PlayerId.NULL, coordinate1, coordinate2);
        }
      }
      else
      {
        turn = new MovePawnTurn(PlayerId.NULL, null, null, new Coordinate(x, y));
      }
    }
    else
    {
      // TODO: Invalid move...
    }

    return turn;
  }

  public static void main(String[] args)
  {
    Main main = new Main();
    main.displayMainMenu();
  }
}