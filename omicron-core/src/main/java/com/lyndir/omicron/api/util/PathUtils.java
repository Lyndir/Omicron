package com.lyndir.omicron.api.util;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.lyndir.lhunath.opal.system.util.NNFunctionNN;
import com.lyndir.lhunath.opal.system.util.PredicateNN;
import java.util.*;
import javax.annotation.Nonnull;


public abstract class PathUtils {

    /**
     * A breath-first search from root.
     *
     * @param root               The object to start the search from.
     * @param foundFunction      The function that checks a neighbouring object to see if it's the object we're looking for.
     * @param costFunction       The function that determines the cost for navigating from a given object to a given neighbouring object.
     * @param maxCost            The maximum cost of a path.  Any paths that cost more than this amount are abandoned.
     * @param neighboursFunction The function that determines what an object's direct neighbours are.
     * @param <E>                The type of objects we're searching.
     *
     * @return An optional path to the found object, or absent if no path was found (no neighbours left or all paths too expensive).
     */
    public static <E> Optional<Path<E>> find(final E root, final PredicateNN<E> foundFunction,
                                             final NNFunctionNN<Step<E>, Double> costFunction, final double maxCost,
                                             final NNFunctionNN<E, Iterable<E>> neighboursFunction) {

        // Test the root.
        if (foundFunction.apply( root ))
            return Optional.of( new Path<>( root, 0 ) );

        // Initialize breath-first.
        Set<E> testedNodes = new HashSet<>();
        Deque<Path<E>> testPaths = new LinkedList<>();
        testPaths.addLast( new Path<>( root, 0 ) );
        testedNodes.add( root );

        // Search breath-first.
        while (!testPaths.isEmpty()) {
            Path<E> testPath = testPaths.removeFirst();

            // Check each neighbour.
            for (final E neighbour : neighboursFunction.apply( testPath.getTarget() )) {
                if (!testedNodes.add( neighbour ))
                    // Neighbour was already tested.
                    continue;

                double neighbourCost = testPath.getCost() + costFunction.apply( new Step<>( testPath.getTarget(), neighbour ) );
                if (neighbourCost > maxCost)
                    // Stepping to neighbour from here would exceed maximum cost.
                    continue;

                // Did we find the target?
                Path<E> neighbourPath = new Path<>( testPath, neighbour, neighbourCost );
                if (foundFunction.apply( neighbour ))
                    return Optional.of( neighbourPath );

                // Neighbour is not the target, add it for testing its neighbours later.
                testPaths.add( neighbourPath );
            }
        }

        return Optional.absent();
    }

    /**
     * A variation of the breath-first search from root which just enumerates all the objects around root.
     *
     * @param root               The object to start the search from.
     * @param radius             The maximum distance of an object.  Any objects farther removed from the root than the radius are
     *                           abandoned
     *                           and not included.
     * @param neighboursFunction The function that determines what an object's direct neighbours are.
     * @param <E>                The type of objects we're searching.
     *
     * @return A collection of the object's neighbours.
     */
    public static <E> Collection<E> neighbours(final E root, final int radius, final NNFunctionNN<E, Iterable<E>> neighboursFunction) {

        if (radius == 0)
            return ImmutableSet.of( root );

        // Initialize breath-first.
        Set<E> neighbours = new HashSet<>();
        Deque<Path<E>> testPaths = new LinkedList<>();
        testPaths.addLast( new Path<>( root, 0 ) );
        neighbours.add( root );

        // Search breath-first.
        while (!testPaths.isEmpty()) {
            Path<E> testPath = testPaths.removeFirst();

            // Check each neighbour.
            for (final E neighbour : neighboursFunction.apply( testPath.getTarget() )) {
                if (!neighbours.add( neighbour ))
                    // Neighbour was already tested.
                    continue;

                double neighbourCost = testPath.getCost() + 1;
                if (neighbourCost > radius)
                    // Stepping to neighbour from here would exceed maximum cost.
                    continue;

                // Add it for testing its neighbours later.
                testPaths.add( new Path<>( testPath, neighbour, neighbourCost ) );
            }
        }

        return neighbours;
    }

    public static class Path<E> {

        private final Optional<Path<E>> parent;
        private final E                 target;
        private final double            cost;

        Path(final E target, final double cost) {
            parent = Optional.absent();
            this.target = target;
            this.cost = cost;
        }

        Path(@Nonnull final Path<E> parent, final E target, final double cost) {
            this.parent = Optional.of( parent );
            this.target = target;
            this.cost = cost;
        }

        public Optional<Path<E>> getParent() {
            return parent;
        }

        public double getCost() {
            return cost;
        }

        public E getTarget() {
            return target;
        }
    }


    public static class Step<E> {

        private final E from;
        private final E to;

        Step(final E from, final E to) {
            this.from = from;
            this.to = to;
        }

        public E getFrom() {
            return from;
        }

        public E getTo() {
            return to;
        }
    }
}
