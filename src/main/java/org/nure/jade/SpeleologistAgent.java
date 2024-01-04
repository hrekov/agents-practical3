package org.nure.jade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.ControllerException;
import org.nure.core.environment.wumpusworld.WumpusAction;
import org.nure.jade.speech.SpeleologistSpeech;
import org.nure.jade.speech.ISpeleologistSpeech;

import static org.nure.jade.speech.ActionStep.*;

public class SpeleologistAgent extends Agent {
	private ISpeleologistSpeech speech;

	@Override
	protected void takeDown() {
		System.out.println(agentMessagePrefix + getAID().getName() + "is terminating.");

        try {
			getContainerController().getAgent(navigatorAid.getLocalName()).kill();
			getContainerController().getAgent(environmentAid.getLocalName()).kill();
		} catch(ControllerException e) {
			System.exit(0);
		}
	}

	@Override
	protected void setup() {
		speech = new SpeleologistSpeech();

		registerMe();
		System.out.println(agentMessagePrefix + getAID().getName() + " is ready.");

		addBehaviour(new SpeleologistBehaviour());
	}

	private void registerMe() {
		final var dfd = new DFAgentDescription();
		dfd.setName(getAID());
		final var sd = new ServiceDescription();

		sd.setType("speleologist");
		sd.setName("wumpus-world");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			System.err.println(agentMessagePrefix + "Failed to be registered in the cluster");
			System.err.println(agentMessagePrefix + '\n' + fe.getMessage());
		}
	}

	class SpeleologistBehaviour extends Behaviour {
		WumpusAction wumpusAction;
		ObjectMapper objectMapper = new ObjectMapper();
		private MessageTemplate mt;
		private State currentState;
		private int step = 0;

		private void discover() {
            DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			sd.setType("environment");
			template.addServices(sd);

			DFAgentDescription template2 = new DFAgentDescription();
			ServiceDescription sd2 = new ServiceDescription();
			sd2.setType("navigator");
			template2.addServices(sd2);
			try {
				DFAgentDescription[] result = DFService.search(myAgent, template);
				environmentAid = result[0].getName();
				DFAgentDescription[] result2 = DFService.search(myAgent, template2);
				navigatorAid = result2[0].getName();
			} catch(FIPAException fe) {
				System.err.println(agentMessagePrefix + "Failed to discover environment or navigator agents");
				System.err.println(agentMessagePrefix + '\n' + fe.getMessage());;
			}
		}

		private void askEnvState() {
			discover();

			ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
			request.setContent("Current state");

			request.addReceiver(environmentAid);

			myAgent.send(request);

			mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchReplyTo(new AID[]{myAgent.getAID()}));

			System.out.println(agentMessagePrefix + "Request for current state sent.");

			step = 1;
		}

		private void receiveEnvState() {
			ACLMessage reply = myAgent.receive(mt);

			if(reply != null) {

				if(reply.getPerformative() == ACLMessage.INFORM) {
					String state = reply.getContent();

					System.out.println(agentMessagePrefix + "Received state info from environment = " + state);

					try {
						currentState = objectMapper.readValue(state, State.class);
					} catch (JsonProcessingException e) {
						throw new RuntimeException(e);
					}
				}
				step = 2;
				return;
			}

			block();
		}

		private void sendStateToNavigator() {
			ACLMessage state = new ACLMessage(ACLMessage.INFORM);

			state.setLanguage("English");
			state.setOntology("WumpusWorld");

			state.addReceiver(navigatorAid);
			String feelings = speech.tellPercept(currentState.percept);
			state.setContent(feelings);

			myAgent.send(state);

			mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
					MessageTemplate.MatchReplyTo(new AID[]{myAgent.getAID()}));

			System.out.println(agentMessagePrefix + "Sent state to the navigator agent");
			step = 3;
		}

		private void receiveNewNavigatorAction() {
			ACLMessage reply2 = myAgent.receive(mt);
			if(reply2 != null) {
				String action = reply2.getContent();
				wumpusAction = speech.recognizeAction(action);
				System.out.println(agentMessagePrefix + "Received new action from navigator = " + action);
				step = 4;
				return;
			}

			block();
		}

		private void sendActionToEnv() {
			ACLMessage action = new ACLMessage(ACLMessage.CFP);
			action.setConversationId("environment");
			action.addReceiver(environmentAid);
			action.setContent(wumpusAction.getSymbol());
			myAgent.send(action);

			mt = MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
					MessageTemplate.MatchReplyTo(new AID[]{myAgent.getAID()})
			);

			step = 5;
		}

		private void receiveOkFromEnv() {
			ACLMessage envReply = myAgent.receive(mt);
			if(envReply != null) {
				if(envReply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL) {
					if(WumpusAction.CLIMB.equals(wumpusAction)) {
						System.out.println(agentMessagePrefix + "Climbed out of the cave.");
						step = 6;
					} else {
						System.out.println(agentMessagePrefix + "Going to the zero step.");
						step = 0;
					}

				}

				return;
			}

			block();
		}

		private void gameIsEnded() {
			System.out.println(agentMessagePrefix + "Game is ended.");
			step = GAME_EXIT;
			myAgent.doDelete();
		}

		@Override
		public void action() {
			switch(step) {
				case ASK_ENV_STATE:
					askEnvState();
					break;
				case RECEIVE_ENV_STATE:
					receiveEnvState();
					break;
				case SEND_STATE_TO_NAVIGATOR:
					sendStateToNavigator();
					break;
				case RECEIVE_NEW_NAVIGATOR_ACTION:
					receiveNewNavigatorAction();
					break;
				case SEND_ACTION_TO_ENV:
					sendActionToEnv();
					break;
				case RECEIVE_OK_FROM_ENV:
					receiveOkFromEnv();
					break;
				case GAME_IS_ENDED:
					gameIsEnded();
			}
		}

		@Override
		public boolean done() {
			return step == 7;
		}
	}

	private static final String agentMessagePrefix = "Speleologist-agent: ";
	private AID environmentAid;
	private AID navigatorAid;
}
