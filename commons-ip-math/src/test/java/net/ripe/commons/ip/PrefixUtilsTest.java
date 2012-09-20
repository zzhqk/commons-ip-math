package net.ripe.commons.ip;

import static org.junit.Assert.*;
import org.junit.Test;

public class PrefixUtilsTest {

    @Test
    public void shouldReturnTrueForValidPrefix() {
        assertTrue(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0/0")));
        assertTrue(PrefixUtils.isValidPrefix(Ipv6Range.parse("::/0")));
    }

    @Test
    public void shouldReturnFalseForInvalidPrefix() {
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0-0.0.0.2")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.from(1585324288l).to(1585324799l)));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.1-0.0.0.3")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.1-255.255.255.255")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.0-255.255.255.254")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.1-255.255.255.254")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv4Range.parse("0.0.0.2-255.255.255.254")));

        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::0-::2")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::1-::3")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::1-ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
        assertFalse(PrefixUtils.isValidPrefix(Ipv6Range.parse("::2-ffff:ffff:ffff:ffff:ffff:ffff:ffff:fffe")));
    }
}
