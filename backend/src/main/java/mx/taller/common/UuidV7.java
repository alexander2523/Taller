package mx.taller.common;
import java.security.SecureRandom;
import java.util.UUID;
/** UUIDv7 generator: timestamp (48 bits), version/variant bits and cryptographic randomness. */
public final class UuidV7 {
 private static final SecureRandom RANDOM = new SecureRandom(); private UuidV7() {}
 public static UUID create() { long ms=System.currentTimeMillis(); long most=(ms<<16)|0x7000L|RANDOM.nextInt(0x1000); long least=0x8000000000000000L|(RANDOM.nextLong()&0x3fffffffffffffffL); return new UUID(most,least); }
}
