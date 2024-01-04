package org.nure.jade.talking;

public class ActionStep {
    public static final int ASK_ENV_STATE = 0;
    public static final int RECEIVE_ENV_STATE = 1;
    public static final int SEND_STATE_TO_NAVIGATOR = 2;
    public static final int RECEIVE_NEW_NAVIGATOR_ACTION = 3;
    public static final int SEND_ACTION_TO_ENV = 4;
    public static final int RECEIVE_OK_FROM_ENV = 5;
    public static final int GAME_IS_ENDED = 6;
    public static final int GAME_EXIT = 7;
}
