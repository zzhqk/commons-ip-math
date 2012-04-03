package net.ripe.commons.ip.range;

import net.ripe.commons.ip.resource.Asn;

public class AsnRange extends AbstractRange<Asn, AsnRange> {

    protected AsnRange(Asn start, Asn end) {
        super(start, end);
    }

    @Override
    protected AsnRange newInstance(Asn start, Asn end) {
        return new AsnRange(start, end);
    }

    @Override
    protected Asn nextOf(Asn rangeItem) {
        return rangeItem.next();
    }

    @Override
    protected Asn previousOf(Asn rangeItem) {
        return rangeItem.previous();
    }

    public static AsnRangeBuilder from(Asn from) {
        return new AsnRangeBuilder(from);
    }

    public static class AsnRangeBuilder extends AbstractRangeBuilder<Asn, AsnRange> {
        protected AsnRangeBuilder(Asn from) {
            super(from, AsnRange.class);
        }
    }
}
