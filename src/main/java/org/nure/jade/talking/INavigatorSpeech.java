package org.nure.jade.talking;

import org.nure.core.environment.wumpusworld.WumpusAction;
import org.nure.core.environment.wumpusworld.WumpusPercept;

public interface INavigatorSpeech {
	String tellAction(WumpusAction action);

	WumpusPercept recognize(String speech);
}
