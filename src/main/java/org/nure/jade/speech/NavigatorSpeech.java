package org.nure.jade.speech;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.nure.core.environment.wumpusworld.WumpusAction;
import org.nure.core.environment.wumpusworld.WumpusPercept;

public class NavigatorSpeech implements INavigatorSpeech {
	private final Random randomGenerator;
	private final Map<WumpusAction, List<String>> actionSentences;

	public NavigatorSpeech() {
		randomGenerator = new Random();

		actionSentences = new HashMap<>();
		actionSentences.put(WumpusAction.TURN_LEFT, ActorPhrases.NavigatorPhrases.turnLeft);
		actionSentences.put(WumpusAction.TURN_RIGHT, ActorPhrases.NavigatorPhrases.turnRight);
		actionSentences.put(WumpusAction.FORWARD, ActorPhrases.NavigatorPhrases.goForward);
		actionSentences.put(WumpusAction.SHOOT, ActorPhrases.NavigatorPhrases.shoot);
		actionSentences.put(WumpusAction.GRAB, ActorPhrases.NavigatorPhrases.grab);
		actionSentences.put(WumpusAction.CLIMB, ActorPhrases.NavigatorPhrases.climb);

	}

	@Override
	public String tellAction(WumpusAction action) {
		final var sentences = actionSentences.get(action);
		int index = randomGenerator.nextInt(sentences.size());

		return sentences.get(index);
	}

	@Override
	public WumpusPercept recognize(String speech) {
		final var feelings = Arrays.stream(speech.split(". ")).map(String::toLowerCase).collect(Collectors.toList());

		final var percept = new WumpusPercept();

		for(String feeling : feelings) {
			if(feeling.contains(PerceptKeyWord.stench)) {
				percept.setStench();
			}

			if(feeling.contains(PerceptKeyWord.breeze)) {
				percept.setBreeze();
			}

			if(feeling.contains(PerceptKeyWord.glitter)) {
				percept.setGlitter();
			}

			if(feeling.contains(PerceptKeyWord.bump)) {
				percept.setBump();
			}

			if(feeling.contains(PerceptKeyWord.scream)) {
				percept.setScream();
			}
		}

		return percept;
	}
}
