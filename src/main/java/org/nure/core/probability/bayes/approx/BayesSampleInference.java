package org.nure.core.probability.bayes.approx;

import org.nure.core.probability.CategoricalDistribution;
import org.nure.core.probability.RandomVariable;
import org.nure.core.probability.bayes.BayesianNetwork;
import org.nure.core.probability.proposition.AssignmentProposition;

/**
 * General interface to be implemented by approximate Bayesian Inference
 * algorithms.
 *
 * @author Ciaran O'Reilly
 */
public interface BayesSampleInference {
	/**
	 * @param X the query variables.
	 * @param observedEvidence observed values for variables E.
	 * @param bn a Bayes net with variables {X} &cup; E &cup; Y /* Y = hidden
	 * variables
	 * @param N the total number of samples to be generated
	 * @return an estimate of <b>P</b>(X|e).
	 */
	CategoricalDistribution ask(final RandomVariable[] X,
	                            final AssignmentProposition[] observedEvidence,
	                            final BayesianNetwork bn, int N);
}
