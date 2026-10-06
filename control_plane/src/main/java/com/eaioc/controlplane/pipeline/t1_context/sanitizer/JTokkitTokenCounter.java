package com.eaioc.controlplane.pipeline.t1_context.sanitizer;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingType;

/**
 * The approved P0 T1.1 counter: JTokkit with encoding {@code CL100K_BASE}, identified as
 * {@code JTOKKIT_CL100K_BASE} / {@code 1.1.0} (interfaces.md §44.2, §44.4; HD-CG-P0.3-09, -14).
 *
 * <p>The encoding is loaded once per JVM, on first use, through the holder idiom. Each counter
 * instance is cheap; the vocabulary is not reloaded per construction.
 *
 * <p><b>Concurrency: NOT VERIFIED.</b> The thread-safety of JTokkit 1.1.0's {@code Encoding} was not
 * established from its documentation or source. A concurrency smoke test exists, but it is evidence,
 * not proof. Do not treat a shared {@link Sanitizer} as thread-safe until that is verified.
 *
 * <p>No fallback counter exists here. A failure of the underlying count propagates to the caller
 * (HD-CG-P0.3-18).
 */
public final class JTokkitTokenCounter implements TokenCounter {

    private static final class Holder {
        static final Encoding CL100K_BASE =
            Encodings.newDefaultEncodingRegistry().getEncoding(EncodingType.CL100K_BASE);
    }

    public JTokkitTokenCounter() {
    }

    @Override
    public TokenCounterIdentity identity() {
        return TokenCounterIdentity.JTOKKIT_CL100K_BASE_1_1_0;
    }

    @Override
    public int countTokens(String text) {
        return Holder.CL100K_BASE.countTokens(text);
    }
}
