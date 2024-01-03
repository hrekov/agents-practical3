package org.nure.jade;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.nure.core.environment.wumpusworld.AgentPosition;
import org.nure.core.environment.wumpusworld.EfficientHybridWumpusAgent;
import org.nure.core.environment.wumpusworld.WumpusAction;
import org.nure.core.environment.wumpusworld.WumpusPercept;
import org.nure.jade.speech.INavigatorSpeech;
import org.nure.jade.speech.NavigatorSpeech;

public class NavigatorAgent extends Agent {
	private static final String agentMessagePrefix = "Navigator-agent: ";
	EfficientHybridWumpusAgent agent;
	INavigatorSpeech speech;
	private AID speleologistAid;

	@Override
	protected void takeDown() {
		System.out.println( agentMessagePrefix+ getAID().getName() + "is terminating.");
	}

	@Override
	protected void setup() {
		agent = new EfficientHybridWumpusAgent(4, 4, new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH));
		speech = new NavigatorSpeech();
		registerMe();
		discover();
		System.out.println(agentMessagePrefix + getAID().getName() + " is ready.");
		addBehaviour(new ListenBehavior());
	}

	private void registerMe() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("navigator");
		sd.setName("wumpus-world");
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			System.err.println(agentMessagePrefix + "Failed to be registered in the cluster");
			System.err.println(agentMessagePrefix + '\n' + fe.getMessage());
		}
	}

	private void discover() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("speleologist");
		template.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(this, template);
			speleologistAid = result[0].getName();
		} catch(FIPAException fe) {
			System.err.println(agentMessagePrefix + "Failed to discover speleologist agents");
			System.err.println(agentMessagePrefix + '\n' + fe.getMessage());;
		}
	}

	private class ListenBehavior extends CyclicBehaviour {
		@Override
		public void action() {
			//query - propose
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				String state = msg.getContent();
				WumpusPercept percept = speech.recognize(state);
				System.out.println(agentMessagePrefix + "received feelings. Feelings = " + state);
				addBehaviour(new FindActionBehaviour(percept));
			} else {
				block();
			}
		}
	}

	private class FindActionBehaviour extends OneShotBehaviour {
		WumpusPercept percept;

		FindActionBehaviour(WumpusPercept percept) {
			this.percept = percept;
		}

		@Override
		public void action() {
			WumpusAction action = agent.act(percept).orElseThrow();
			ACLMessage reply = new ACLMessage(ACLMessage.PROPOSE);
			System.out.println(agentMessagePrefix + "decided on action. Action = " + action);
			String actionSentence = speech.tellAction(action);
			reply.setLanguage("English");
			reply.setOntology("WumpusWorld");
			reply.setContent(actionSentence);
			reply.addReplyTo(speleologistAid);
			reply.addReceiver(speleologistAid);
			myAgent.send(reply);
		}
	}
}
