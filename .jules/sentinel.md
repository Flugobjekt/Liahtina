## 2025-02-28 - [Sentinel] Constant-time Comparison for Authentication
**Vulnerability:** A non-constant-time string comparison (`String.equals()`) was used for password verification in `RconClient.java` for the RCON protocol authentication. This could theoretically allow an attacker to guess the password through timing attacks, deducing the correct password character by character.
**Learning:** String comparisons in security-sensitive scenarios like password checking must avoid short-circuiting logic that returns early upon encountering a mismatch.
**Prevention:** Use `java.security.MessageDigest.isEqual()` on byte arrays (encoded with `StandardCharsets.UTF_8`) to perform constant-time comparisons of secrets, keys, and tokens.
