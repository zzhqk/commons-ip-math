package net.ripe.commons.ip.range;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.ripe.commons.ip.resource.EqualsSupport;
import org.apache.commons.lang.Validate;

public abstract class AbstractRange<C extends Comparable<C>, R extends AbstractRange<C, R>>
        extends EqualsSupport implements Iterable<C> {

    private final C start;
    private final C end;

    protected AbstractRange(C start, C end) {
        Validate.notNull(start, "start of range must not be null");
        Validate.notNull(end, "end of range must not be null");
        Validate.isTrue(start.compareTo(end) <= 0, String.format("Invalid range [%s..%s]", start.toString(), end.toString()));
        this.start = start;
        this.end = end;
    }

    protected abstract R newInstance(C start, C end);
    protected abstract C nextOf(C rangeItem);
    protected abstract C previousOf(C rangeItem);


    public C start() {
        return start;
    }

    public C end() {
        return end;
    }

    public boolean contains(R other) {
        return start.compareTo(other.start) <= 0 && end.compareTo(other.end) >= 0;
    }

    public boolean contains(C value) {
        Validate.notNull(value, "A value is required");
        return start.compareTo(value) <= 0 && end.compareTo(value) >= 0;
    }

    public boolean overlaps(R other) {
        return other.contains(start) || other.contains(end) || this.contains(other);
    }

    public boolean isAdjacent(R other) {
        return this.end.equals(other.start) || other.end.equals(this.start);
    }

    public boolean isConsecutive(R other) {
        return nextOf(this.end).equals(other.start) || nextOf(other.end).equals(this.start);
    }

    public R merge(R other) {
        Validate.isTrue(this.overlaps(other), "Merge is only possible for overlapping ranges");
        return mergeWith(other);
    }

    public R mergeConsecutive(R other) {
        Validate.isTrue(this.overlaps(other) || this.isConsecutive(other), "Merge is only possible for overlapping or consecutive ranges");
        return mergeWith(other);
    }

    private R mergeWith(R other) {
        C start = min(this.start(), other.start());
        C end = max(this.end(), other.end());
        return newInstance(start, end);
    }

    public R intersection(R other) {
        C start = max(this.start(), other.start());
        C end = min(this.end(), other.end());
        return newInstance(start, end);
    }

    private C max(C a, C b) {
        return a.compareTo(b) >= 0 ? a : b;
    }

    private C min(C a, C b) {
        return a.compareTo(b) <= 0 ? a : b;
    }

    @SuppressWarnings({"unchecked"})
    public List<R> remove(R other) {
        if (!overlaps(other)) {
            return Collections.singletonList((R)this);

        } else if (other.contains((R) this)) {
            return Collections.emptyList();

        } else if (!this.contains(other.start) && this.contains(other.end)) {
            return Collections.singletonList(newInstance(nextOf(other.end), this.end));

        } else if (this.contains(other.start) && !this.contains(other.end)) {
            return Collections.singletonList(newInstance(this.start, previousOf(other.start)));

        } else {
            if (this.hasSameStart(other)) {
                return Collections.singletonList(newInstance(nextOf(other.end), this.end));

            } else if (this.hasSameEnd(other)) {
                return Collections.singletonList(newInstance(this.start, previousOf(other.start)));

            } else {
                ArrayList<R> rs = new ArrayList<R>(2);
                rs.add(newInstance(this.start, previousOf(other.start)));
                rs.add(newInstance(nextOf(other.end), this.end));
                return rs;
            }
        }
    }

    private boolean hasSameStart(R other) {
        return this.start.equals(other.start);
    }

    private boolean hasSameEnd(R other) {
        return this.end.equals(other.end);
    }

    @Override
    public String toString() {
        return String.format("[%s..%s]", start.toString(), end.toString());
    }

    @Override
    public Iterator<C> iterator() {
        return new RangeIterator();
    }

    private class RangeIterator implements Iterator<C> {

        private C nextValue = start;

        @Override
        public boolean hasNext() {
            return nextValue.compareTo(end) <= 0;
        }

        @Override
        public C next() {
            C valueToReturn = nextValue;
            nextValue = nextOf(valueToReturn);
            return valueToReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected static abstract class AbstractRangeBuilder<C extends Comparable<C>, R extends AbstractRange<C, R>> {
        private final C start;
        private final Class<R> typeOfRange;

        protected AbstractRangeBuilder(C from, Class<R> typeOfRange) {
            this.start = from;
            this.typeOfRange = typeOfRange;
        }

        public R to(C end) {
            try {
                return typeOfRange
                        .getDeclaredConstructor(start.getClass(), end.getClass())
                        .newInstance(start, end);
            } catch (Exception e) {
                throw new RuntimeException(String.format("Failed to create range [%s..%s]", start.toString(), end.toString()));
            }
        }
    }
}
