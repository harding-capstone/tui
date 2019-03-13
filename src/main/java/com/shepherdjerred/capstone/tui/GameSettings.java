package com.shepherdjerred.capstone.tui;

import com.shepherdjerred.capstone.ai.QuoridorAi;
import com.shepherdjerred.capstone.logic.board.BoardSettings;
import com.shepherdjerred.capstone.logic.match.MatchSettings;
import com.shepherdjerred.capstone.logic.player.QuoridorPlayer;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameSettings {

  private final MatchSettings matchSettings;
  private final BoardSettings boardSettings;
  private final Map<QuoridorPlayer, QuoridorAi> aiPlayers;
}
