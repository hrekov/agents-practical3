package org.nure.jade.speech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.nure.core.environment.wumpusworld.WumpusAction;
import org.nure.core.environment.wumpusworld.WumpusPercept;

public class SpeleologistSpeech implements ISpeleologistSpeech {
	private final Random randomGenerator;
	private final Map<String, WumpusAction> actionKeyWords;

	public SpeleologistSpeech() {
		randomGenerator = new Random();

		actionKeyWords = new HashMap<>();
		actionKeyWords.put(ActionKeyWord.turnLeft, WumpusAction.TURN_LEFT);
		actionKeyWords.put(ActionKeyWord.turnRight, WumpusAction.TURN_RIGHT);
		actionKeyWords.put(ActionKeyWord.goForward, WumpusAction.FORWARD);
		actionKeyWords.put(ActionKeyWord.shoot, WumpusAction.SHOOT);
		actionKeyWords.put(ActionKeyWord.grab, WumpusAction.GRAB);
		actionKeyWords.put(ActionKeyWord.climb, WumpusAction.CLIMB);
	}

	@Override
	public WumpusAction recognizeAction(String speech) {
		final var finalSpeech = speech.toLowerCase();

		return actionKeyWords.keySet().stream()
				.filter(finalSpeech::contains)
				.findFirst()
				.map(actionKeyWords::get)
				.orElseThrow();
	}

	@Override
	public String tellPercept(WumpusPercept percept) {
		final List<String> feelings = new ArrayList<>();

		if(percept.isBreeze()) {
			feelings.add(getSentence(ActorPhrases.SpeleologistPhrases.pitNear));
		}

		if(percept.isStench()) {
			feelings.add(getSentence(ActorPhrases.SpeleologistPhrases.wumpusNear));
		}

		if(percept.isGlitter()) {
			feelings.add(getSentence(ActorPhrases.SpeleologistPhrases.goldNear));
		}

		if(percept.isBump()) {
			feelings.add(getSentence(ActorPhrases.SpeleologistPhrases.wallNear));
		}

		if(percept.isScream()) {
			feelings.add(getSentence(ActorPhrases.SpeleologistPhrases.wumpusKilledNear));
		}

		if(feelings.isEmpty()) {
			feelings.add(getSentence(ActorPhrases.SpeleologistPhrases.nothing));
		}

		return String.join(". ", feelings);
	}

	private String getSentence(List<String> sentences) {
		int index = randomGenerator.nextInt(sentences.size());
		return sentences.get(index);
	}
}
