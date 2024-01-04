package org.nure.jade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.nure.core.environment.wumpusworld.HybridWumpusAgent;
import org.nure.core.environment.wumpusworld.WumpusAction;
import org.nure.core.environment.wumpusworld.WumpusCave;
import org.nure.core.environment.wumpusworld.WumpusEnvironment;
import org.nure.core.environment.wumpusworld.WumpusPercept;

public class EnvironmentAgent extends Agent {
	private static final String agentMessagePrefix = "Environment-agent: ";
	private WumpusEnvironment wumpusEnvironment;
	private AID speleologistAID;
	private HybridWumpusAgent speleologist;
	private WumpusPercept percept;
	private int tick = 0;

	private class AcceptBehaviour extends OneShotBehaviour {
		@Override
		public void action() {
			ACLMessage report = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			report.setContent("OK");
			report.addReceiver(speleologistAID);
			report.addReplyTo(speleologistAID);

			System.out.println(agentMessagePrefix + "step performed.");

			myAgent.send(report);
		}
	}


	private class QueryBehaviour extends OneShotBehaviour {
		ObjectMapper objectMapper = new ObjectMapper();

		@Override
		public void action() {
			AgentPosition agentPosition = wumpusEnvironment.getAgentPosition(speleologist);
			percept = wumpusEnvironment.getPerceptSeenBy(speleologist);
			ACLMessage report = new ACLMessage(ACLMessage.INFORM);

			try {
				report.setContent(objectMapper.writeValueAsString(new State(percept, tick++)));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}

			report.addReceiver(speleologistAID);
			report.addReplyTo(speleologistAID);
			myAgent.send(report);
			System.out.println(agentMessagePrefix + "percept sent to speleologist");
			System.out.println(agentMessagePrefix + "current position: " + agentPosition);
		}
	}

	@Override
	protected void takeDown() {
		System.out.println(agentMessagePrefix + getAID().getName() + " terminating.");
	}

	@Override
	protected void setup() {
		registerMe();
		wumpusEnvironment = new WumpusEnvironment(new WumpusCave(4, 4, ""
				+ ". . . P "
				+ "P G . . "
				+ "P . . W "
				+ "S . P . "));

		speleologist = new EfficientHybridWumpusAgent(4, 4, new AgentPosition(1, 1, AgentPosition.Orientation.FACING_NORTH));
		percept = new WumpusPercept();
		wumpusEnvironment.addAgent(speleologist);

		final var template = new DFAgentDescription();
		final var sd = new ServiceDescription();
		sd.setType("speleologist");
		template.addServices(sd);

		try {
			DFAgentDescription[] result = DFService.search(this, template);
			speleologistAID = result[0].getName();
		} catch(FIPAException fe) {
			System.err.println(agentMessagePrefix + "Failed to discover speleologist agents");
			System.err.println(agentMessagePrefix + '\n' + fe.getMessage());;
		}

		addBehaviour(new ListenBehavior());
		System.out.println(agentMessagePrefix + getAID().getName() + " is ready.");
	}

	private void registerMe() {
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("environment");
		sd.setName("wumpus-world");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch(FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private class ListenBehavior extends CyclicBehaviour {
		public void action() {
			//query - propose
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchPerformative(ACLMessage.CFP));
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null) {
				if(ACLMessage.REQUEST == msg.getPerformative()) {
					addBehaviour(new QueryBehaviour());
				} else if(ACLMessage.CFP == msg.getPerformative()) {
					String move = msg.getContent();
					wumpusEnvironment.execute(speleologist, WumpusAction.fromString(move));
					addBehaviour(new AcceptBehaviour());
				} else {
					block();
				}
			} else {
				block();
			}
		}
	}
}
