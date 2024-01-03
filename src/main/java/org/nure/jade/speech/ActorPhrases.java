package org.nure.jade.speech;

import java.util.List;

public final class ActorPhrases {

	public static final class SpeleologistPhrases {
		public static List<String> pitNear = List.of("I feel breeze");
		public static List<String> wumpusNear = List.of("I smell something");
		public static List<String> goldNear = List.of("I see something shiny");
		public static List<String> wallNear = List.of("I hit the wall");
		public static List<String> wumpusKilledNear = List.of("I hear something");
		public static List<String> nothing = List.of("All clear");
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

