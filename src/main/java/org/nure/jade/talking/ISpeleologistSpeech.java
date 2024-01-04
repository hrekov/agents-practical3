package org.nure.jade.talking;

import org.nure.core.environment.wumpusworld.WumpusAction;
import org.nure.core.environment.wumpusworld.WumpusPercept;

public interface ISpeleologistSpeech {
	WumpusAction recognizeAction(String speech);

	String tellPercept(WumpusPercept percept);
}
