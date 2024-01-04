package org.nure.jade.talking;

import java.util.List;

public final class ActorPhrases {

	public static final class SpeleologistPhrases {
		public static final String pitNear = "I feel breeze";
		public static final String wumpusNear ="I smell something";
		public static final String goldNear = "I see something shiny";
		public static final String wallNear = "I hit the wall";
		public static final String wumpusKilledNear = "I hear something";
		public static final String nothing = "All clear";
	}

	public static final class NavigatorPhrases {
		public static List<String> goForward = List.of("Go forward");
		public static List<String> turnLeft = List.of("Turn left");
		public static List<String> turnRight = List.of("Turn right");
		public static List<String> shoot = List.of("Shoot");
		public static List<String> grab = List.of("Grab the gold");
		public static List<String> climb = List.of("Climb the ladder");
	}
}

