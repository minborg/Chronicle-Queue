package net.openhft.chronicle.queue;

import net.openhft.chronicle.core.time.TimeProvider;

@FunctionalInterface
public interface CycleCalculator {

    /**
     * Returns the current cycle for the given parameters.
     *
     * @param rollCycle that is used
     * @param timeProvider to apply for calculation
     * @param offsetMillis to offset (subtract) from the current time before calculation.
     * @return Returns the current cycle for the given parameters
     */
    int currentCycle(RollCycle rollCycle, TimeProvider timeProvider, long offsetMillis);
}