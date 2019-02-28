package com.shepherdjerred.capstone.tui.io;

import com.shepherdjerred.capstone.logic.board.Coordinate;
import com.shepherdjerred.capstone.logic.player.PlayerId;
import com.shepherdjerred.capstone.logic.turn.MovePawnTurn;
import com.shepherdjerred.capstone.logic.turn.PlaceWallTurn;
import com.shepherdjerred.capstone.logic.turn.Turn;

public class TurnParser {

  public Turn parse(String move) {
    int x = 0;
    int y = 0;

    Turn turn = null;

    // Would be nice here to trim the string if extra characters were typed by mistake, then the following code executes...
    if (move.matches("[a-iA-I]+[1-9]+[h, v]?")) {
      //System.out.println(move);

      // MOVE:
      // Examine first character and return proper int
      // Examine second character and return proper int

      char c1 = Character.toLowerCase(move.charAt(0));
      switch (c1) {
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
          throw new IllegalArgumentException(move);
      }

      // Debug:
      System.out.println(x);

      char c2 = move.charAt(1);
      int number = c2 - '0';

      switch (number) {
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
          throw new IllegalArgumentException(move);
      }

      // Debug:
//      System.out.println(y);

      // WALL:
      // If the length of string is 3, examine if horizontal or vertical wall
      // Place wall

      if (move.length() == 3) {
        if (move.endsWith("h")) {
//          System.out.println("Horizontal wall!");
          Coordinate coordinate1 = new Coordinate(x, y + 1);
          Coordinate coordinate2 = new Coordinate(x + 2, y + 1);
          turn = new PlaceWallTurn(PlayerId.NULL, coordinate1, coordinate2);
        } else {
//          System.out.println("Vertical wall!");
          Coordinate coordinate1 = new Coordinate(x + 1, y);
          Coordinate coordinate2 = new Coordinate(x + 1, y + 2);
          turn = new PlaceWallTurn(PlayerId.NULL, coordinate1, coordinate2);
        }
      } else {
        turn = new MovePawnTurn(PlayerId.NULL, null, null, new Coordinate(x, y));
      }
    } else {
      throw new IllegalArgumentException(move);
    }

    return turn;
  }
}
