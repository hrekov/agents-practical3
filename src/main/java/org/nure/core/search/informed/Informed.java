package org.nure.core.search.informed;

import java.util.function.ToDoubleFunction;

import org.nure.core.search.framework.Node;

/**
 * Search algorithms which make use of heuristics to guide the search
 * are expected to implement this interface.
 *
 * @author Ruediger Lunde
 */
public interface Informed<S, A> {
	void setHeuristicFunction(ToDoubleFunction<Node<S, A>> h);
}
