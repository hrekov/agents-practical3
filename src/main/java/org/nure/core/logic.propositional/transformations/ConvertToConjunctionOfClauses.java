package org.nure.core.logic.propositional.transformations;

import java.util.Set;

import org.nure.core.logic.propositional.kb.data.Clause;
import org.nure.core.logic.propositional.kb.data.ConjunctionOfClauses;
import org.nure.core.logic.propositional.parsing.ast.Sentence;

/**
 * Artificial Intelligence A Modern Approach (3rd Edition): page 253.<br>
 * <br>
 * A sentence expression as a conjunction of clauses is said to be in
 * <b>conjunctive normal form</b> or <b>CNF</b>.<br>
 *
 * <pre>
 * CNFSentence -> Clause_1 & ... & Clause_n
 *      Clause -> Literal_1 | ... | Literal_m
 *     Literal -> Symbol : ~Symbol
 *      Symbol -> P : Q : R : ... // (1)
 * </pre>
 * <p>
 * Figure 7.14 A grammar for conjunctive normal form.<br>
 * <br>
 * Note (1): While the book states 'We use symbols that start with an upper case
 * letter and may contain other letters or subscripts' in this implementation we
 * allow any legal java identifier to stand in for a proposition symbol.<br>
 *
 * @author Ciaran O'Reilly
 * @author Ravi Mohan
 * @author Mike Stampone
 * @author Ruediger Lunde
 */
public class ConvertToConjunctionOfClauses {

	/**
	 * Returns the specified sentence in its logically equivalent conjunction of
	 * clauses.
	 *
	 * @param s a propositional logic sentence
	 * @return the input sentence converted to it logically equivalent
	 * conjunction of clauses.
	 */
	public static ConjunctionOfClauses apply(Sentence s) {
		Sentence cnfSentence = ConvertToCNF.apply(s);
		Set<Clause> clauses = ClauseCollector.getClausesFrom(cnfSentence);
		return new ConjunctionOfClauses(clauses);
	}
}