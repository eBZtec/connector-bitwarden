package br.tec.ebz.connid.connector.bitwarden;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitwardenConfigurationTest {

    @Test
    void validate() {
        BitwardenConfiguration bitwardenConfiguration = new BitwardenConfiguration();
        bitwardenConfiguration.validate();
    }
}