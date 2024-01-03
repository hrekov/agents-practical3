package org.nure.jade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nure.core.environment.wumpusworld.WumpusPercept;

@AllArgsConstructor
@ToString
@Getter
@NoArgsConstructor
public class State {
	WumpusPercept percept;
	int tick;
}
