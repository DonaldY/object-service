package com.donaldy.utils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class NanoTimes {

    public static class NanoClock extends Clock {
        private final Clock clock;

        private final long initialNanos;

        private final Instant initialInstant;

        public NanoClock() {
            this(Clock.systemUTC());
        }

        public NanoClock(final Clock clock) {
            this.clock = clock;
            initialInstant = clock.instant();
            initialNanos = getSystemNanos();
        }

        @Override
        public ZoneId getZone() {
            return clock.getZone();
        }

        @Override
        public Instant instant() {
            return initialInstant.plusNanos(getSystemNanos() - initialNanos);
        }

        @Override
        public Clock withZone(final ZoneId zone) {
            return new NanoClock(clock.withZone(zone));
        }

        private long getSystemNanos() {
            return System.nanoTime();
        }
    }
    private static final Clock clock = new NanoClock();

    public static Instant nanoTime() {
        return Instant.now(clock);
    }

    public static long nanoTimestamp(Instant instant) {
        return instant.getEpochSecond() * 1000000000 + instant.getNano();
    }

    public static long nanoTimestamp() {
        return nanoTimestamp(nanoTime());
    }
}

