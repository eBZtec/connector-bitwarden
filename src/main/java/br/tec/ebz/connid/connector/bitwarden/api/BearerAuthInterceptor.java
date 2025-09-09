package br.tec.ebz.connid.connector.bitwarden.api;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BearerAuthInterceptor extends AbstractPhaseInterceptor<Message> {
    private final String token;

    public BearerAuthInterceptor(String token) {
        super(Phase.PREPARE_SEND); // Fase correta para headers
        this.token = token;
    }

    @Override
    public void handleMessage(Message message) throws Fault {
        Map<String, List<String>> headers =
                (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        if (headers == null) {
            headers = new HashMap<>();
            message.put(Message.PROTOCOL_HEADERS, headers);
        }

        headers.put("Authorization", List.of("Bearer " + token));
    }
}
