package org.nure.core.probability.bayes;

import org.nure.core.probability.CategoricalDistribution;
import org.nure.core.probability.Factor;
import org.nure.core.probability.proposition.AssignmentProposition;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): page 512.<br>
 * <br>
 * A Conditional Probability Table, or CPT, can be used for representing
 * conditional probabilities for discrete (finite) random variables. Each row in
 * a CPT contains the conditional probability of each node value for a
 * <b>conditioning case</b>.
 *
 * @author Ciaran O'Reilly
 */
public interface ConditionalProbabilityTable extends
		ConditionalProbabilityDistribution {

	@Override
	CategoricalDistribution getConditioningCase(Object... parentValues);

	@Override
	CategoricalDistribution getConditioningCase(
			AssignmentProposition... parentValues);

	/**
	 * Construct a Factor consisting of the Random Variables from the
	 * Conditional Probability Table that are not part of the evidence (see
	 * AIMA3e pg. 524).
	 *
	 * @param evidence
	 * @return a Factor for the Random Variables from the Conditional
	 * Probability Table that are not part of the evidence.
	 */
	Factor getFactorFor(AssignmentProposition... evidence);
}
