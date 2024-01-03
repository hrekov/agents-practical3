package org.nure.core.logic.propositional.inference;

import org.nure.core.logic.propositional.kb.KnowledgeBase;
import org.nure.core.logic.propositional.parsing.ast.Sentence;

public interface EntailmentChecker {

	/**
	 * Determine if KB |= &alpha;, i.e. alpha is entailed by KB.
	 *
	 * @param kb a Knowledge Base in propositional logic.
	 * @param alpha a propositional sentence.
	 * @return true, if &alpha; is entailed by KB, false otherwise.
	 */
	boolean isEntailed(KnowledgeBase kb, Sentence alpha);
}
