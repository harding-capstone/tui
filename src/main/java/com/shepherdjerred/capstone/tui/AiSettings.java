package com.shepherdjerred.capstone.tui;

import com.shepherdjerred.capstone.ai.QuoridorAi;
import com.shepherdjerred.capstone.logic.player.QuoridorPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AiSettings {

  private QuoridorAi quoridorAi;
  private final QuoridorPlayer quoridorPlayer;
}
