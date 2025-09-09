package br.tec.ebz.connid.connector.bitwarden;

import org.identityconnectors.common.security.GuardedString;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

public abstract class BitwardenConfigurationHandler {
    private static String env(String key) { return System.getenv(key); }

    public static BitwardenConfiguration configFromEnv() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        String base = env("BW_BASE_URL");
        String auth = env("BW_AUTH_URL");
        String id = env("BW_CLIENT_ID");
        String secret = env("BW_CLIENT_SECRET");

        assumeTrue(base != null && !base.isBlank(), "BW_BASE_URL not set");
        assumeTrue(id != null && !id.isBlank(), "BW_CLIENT_ID not set");
        assumeTrue(secret != null && !secret.isBlank(), "BW_CLIENT_SECRET not set");
        assumeTrue(auth != null && !auth.isBlank(), "BW_AUTH_SECRET not set");

        configuration.setHostUrl(base);
        configuration.setClientId(id);
        configuration.setClientSecret(new GuardedString(secret.toCharArray()));
        configuration.setAuthUrl(auth);
        return configuration;
    }
}
