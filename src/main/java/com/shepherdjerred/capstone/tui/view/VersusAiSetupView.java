package com.shepherdjerred.capstone.tui.view;

import com.shepherdjerred.capstone.ai.QuoridorAi;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.PruningAlphaBetaQuoridorAi;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.DeepWallPruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.PieceDistancePruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.PruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.RandomDiscardPruningRule;
import com.shepherdjerred.capstone.ai.evaluator.EvaluatorWeights;
import com.shepherdjerred.capstone.ai.evaluator.MatchEvaluator;
import com.shepherdjerred.capstone.ai.evaluator.WeightedMatchEvaluator;
import com.shepherdjerred.capstone.logic.board.BoardSettings;
import com.shepherdjerred.capstone.logic.match.MatchSettings;
import com.shepherdjerred.capstone.logic.player.PlayerCount;
import com.shepherdjerred.capstone.logic.player.PlayerId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class VersusAiSetupView implements View {

  private final Scanner scanner;

  @Override
  public Optional<View> display() {
    boolean shouldContinue = true;
    while (shouldContinue) {
      PlayerId startingPlayer;
      PlayerId aiPlayer;

      System.out.println("Starting player");
      var input = scanner.next();

      try {
        var startingPlayerInt = Integer.parseInt(input);
        startingPlayer = PlayerId.fromInt(startingPlayerInt);
      } catch (Exception e) {
        log.error("Error parsing starting player", e);
        continue;
      }

      System.out.println("AI player");
      input = scanner.next();

      try {
        var aiPlayerInt = Integer.parseInt(input);
        aiPlayer = PlayerId.fromInt(aiPlayerInt);
      } catch (Exception e) {
        log.error("Error parsing ai player", e);
        continue;
      }

      BoardSettings boardSettings = new BoardSettings(9, PlayerCount.TWO);
      MatchSettings matchSettings = new MatchSettings(10, startingPlayer, PlayerCount.TWO);

      EvaluatorWeights evaluatorWeights = new EvaluatorWeights(
          0,
          6,
          6,
          9,
          1
      );

      MatchEvaluator matchEvaluator = new WeightedMatchEvaluator(evaluatorWeights);

      Set<PruningRule> pruningRules = new HashSet<>();
      pruningRules.add(new RandomDiscardPruningRule(90));
      pruningRules.add(new DeepWallPruningRule(5));
      pruningRules.add(new PieceDistancePruningRule(2));

      QuoridorAi quoridorAi = new PruningAlphaBetaQuoridorAi(matchEvaluator, 2, pruningRules);

      return Optional.of(new PlayerVersusAiView(scanner,
          boardSettings,
          matchSettings,
          quoridorAi,
          aiPlayer));
    }

    return Optional.of(new MainMenuView(scanner));
  }
}
