package com.shepherdjerred.capstone.tui;

import com.shepherdjerred.capstone.ai.QuoridorAi;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.PruningAlphaBetaQuoridorAi;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.DeepWallNodePruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.NodePruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.PieceDistanceNodePruningRule;
import com.shepherdjerred.capstone.ai.alphabeta.pruning.rules.RandomDiscardNodePruningRule;
import com.shepherdjerred.capstone.ai.evaluator.EvaluatorWeights;
import com.shepherdjerred.capstone.ai.evaluator.MatchEvaluator;
import com.shepherdjerred.capstone.ai.evaluator.WeightedMatchEvaluator;
import java.util.HashSet;
import java.util.Set;

public class AiProvider {

  public QuoridorAi getDefaultQuoridorAi() {
    var evaluatorWeights = new EvaluatorWeights(
        2293.7109999771455,
        398.7527547140071,
        4762.159725078656,
        9407.150981288025,
        -6985.356279833557
    );

    MatchEvaluator matchEvaluator = new WeightedMatchEvaluator(evaluatorWeights);

    Set<NodePruningRule> pruningRules = new HashSet<>();
    pruningRules.add(new RandomDiscardNodePruningRule(50));
    pruningRules.add(new DeepWallNodePruningRule(2));
    pruningRules.add(new PieceDistanceNodePruningRule(3));

    return new PruningAlphaBetaQuoridorAi(matchEvaluator, 4, pruningRules);
  }
}
