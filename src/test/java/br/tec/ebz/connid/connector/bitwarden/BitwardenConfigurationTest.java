package br.tec.ebz.connid.connector.bitwarden;

import org.identityconnectors.common.security.GuardedString;
import org.identityconnectors.framework.common.exceptions.ConfigurationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenConfigurationTest {

    @Test
    void validateValidConfigurationInputs() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();

        configuration.setHostUrl("https://iam.validurl.com");
        configuration.setClientId("I_AM_A_CLIENTID");
        configuration.setClientSecret(new GuardedString("i_am_client_secret".toCharArray()));

        configuration.validate();

        assertEquals("https://iam.validurl.com", configuration.getHostUrl());
        assertEquals("I_AM_A_CLIENTID", configuration.getClientId());
        assertNotNull(configuration.getClientSecret());
    }

    @Test
    void validateNullHostConfiguration() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        configuration.setClientId("I_AM_A_CLIENTID");
        configuration.setClientSecret(new GuardedString("i_am_client_secret".toCharArray()));

        assertThrows(ConfigurationException.class, configuration::validate);
    }

    @Test
    void validateEmptyHostConfiguration() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        configuration.setHostUrl(" ");
        configuration.setClientId("I_AM_A_CLIENTID");
        configuration.setClientSecret(new GuardedString("i_am_client_secret".toCharArray()));

        assertThrows(ConfigurationException.class, configuration::validate);
    }

    @Test
    void validateNullClientIdConfiguration() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        configuration.setHostUrl("https://iam.validurl.com");
        configuration.setClientSecret(new GuardedString("i_am_client_secret".toCharArray()));

        assertThrows(ConfigurationException.class, configuration::validate);
    }

    @Test
    void validateEmptyClientIdConfiguration() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        configuration.setHostUrl("https://iam.validurl.com");
        configuration.setClientId("");
        configuration.setClientSecret(new GuardedString("i_am_client_secret".toCharArray()));

        assertThrows(ConfigurationException.class, configuration::validate);
    }

    @Test
    void validateEmptyClientSecretConfiguration() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();
        configuration.setHostUrl("https://iam.validurl.com");
        configuration.setClientId("I_AM_A_CLIENT_ID");

        assertThrows(ConfigurationException.class, configuration::validate);
    }

    @Test
    void validInvalidHostUrlConfiguration() {
        BitwardenConfiguration configuration = new BitwardenConfiguration();

        configuration.setHostUrl("iam.validurl.com");
        configuration.setClientId("I_AM_A_CLIENTID");
        configuration.setClientSecret(new GuardedString("i_am_client_secret".toCharArray()));

        assertThrows(ConfigurationException.class, configuration::validate);
    }

}