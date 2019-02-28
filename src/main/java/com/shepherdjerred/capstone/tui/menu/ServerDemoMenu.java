package com.shepherdjerred.capstone.tui.menu;

import com.shepherdjerred.capstone.common.chat.ChatHistory;
import com.shepherdjerred.capstone.common.lobby.Lobby;
import com.shepherdjerred.capstone.common.lobby.LobbySettings;
import com.shepherdjerred.capstone.common.lobby.LobbySettings.LobbyType;
import com.shepherdjerred.capstone.logic.board.Board;
import com.shepherdjerred.capstone.logic.board.BoardPieces;
import com.shepherdjerred.capstone.logic.board.BoardPiecesInitializer;
import com.shepherdjerred.capstone.logic.board.BoardSettings;
import com.shepherdjerred.capstone.logic.board.layout.BoardCellsInitializer;
import com.shepherdjerred.capstone.logic.board.layout.BoardLayout;
import com.shepherdjerred.capstone.logic.match.Match;
import com.shepherdjerred.capstone.logic.match.MatchSettings;
import com.shepherdjerred.capstone.logic.match.MatchSettings.PlayerCount;
import com.shepherdjerred.capstone.logic.player.PlayerId;
import com.shepherdjerred.capstone.server.network.connection.local.LocalConnectionBridge;
import com.shepherdjerred.capstone.server.network.packet.PlayerInitializationPacket;
import com.shepherdjerred.capstone.server.server.GameServer;
import com.shepherdjerred.capstone.server.server.ServerSettings;
import java.util.Scanner;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerDemoMenu implements Menu {

  private final Scanner scanner;

  @Override
  public void show() {
    print();
    handleInput();
  }

  private void handleInput() {
    var input = scanner.next();

    if (input.equals("1")) {
      doDemo();
    } else if (input.equals("2")) {
      var mainMenu = new MainMenu(scanner);
      mainMenu.show();
    } else {
      System.out.println("Invalid input; try again");
      show();
    }
  }

  private void print() {
    System.out.println("\n~ Choose an option: ~");
    System.out.println("1.) Demo.");
    System.out.println("2.) Back.");
  }

  private GameServer createServer() {
    BoardSettings boardSettings = new BoardSettings(9, PlayerCount.TWO);
    MatchSettings matchSettings = new MatchSettings(10, PlayerId.ONE, boardSettings);
    BoardCellsInitializer boardCellsInitializer = new BoardCellsInitializer();
    BoardLayout boardLayout = BoardLayout.fromBoardSettings(boardCellsInitializer, boardSettings);
    BoardPiecesInitializer boardPiecesInitializer = new BoardPiecesInitializer();
    BoardPieces boardPieces = BoardPieces.initializePieceLocations(boardSettings,
        boardPiecesInitializer);
    Board board = Board.createBoard(boardLayout, boardPieces);
    Match match = Match.startNewMatch(matchSettings, board);

    LobbySettings lobbySettings = new LobbySettings("My Lobby!",
        matchSettings,
        LobbyType.LOCAL,
        false);

    return new GameServer(new ServerSettings(),
        new ChatHistory(),
        match,
        Lobby.fromLobbySettings(lobbySettings));
  }

  private void doDemo(){
    LocalConnectionBridge localConnectionBridge = new LocalConnectionBridge();

    GameServer gameServer = createServer();

    gameServer.connectLocalClient(localConnectionBridge);

    new Thread(() -> {
      try {
        gameServer.run();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }).start();

    var infoMessage = new PlayerInitializationPacket("Jerred");
    localConnectionBridge.publishToServer(infoMessage);

    // TODO let the processing stop...
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    show();
  }
}
