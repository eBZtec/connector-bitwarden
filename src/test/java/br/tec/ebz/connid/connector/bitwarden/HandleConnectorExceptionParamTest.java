package br.tec.ebz.connid.connector.bitwarden;

import br.tec.ebz.connid.connector.bitwarden.processing.GroupsProcessing;
import br.tec.ebz.connid.connector.bitwarden.processing.MemberProcessing;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.NotFoundException;
import org.apache.cxf.interceptor.Fault;
import org.identityconnectors.framework.common.exceptions.*;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.OperationOptionsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HandleConnectorExceptionParamTest {
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field f = BitwardenConnector.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }

    static Stream<TestCase> provide_exceptions() {
        return Stream.of(

                new TestCase(
                        new UnsupportedOperationException("invalid op"),
                        UnsupportedOperationException.class,
                        "my-message"
                ),

                new TestCase(
                        new Fault(new RuntimeException("fault!")),
                        ConnectionFailedException.class,
                        "fault"
                ),

                new TestCase(
                        new NotFoundException("not found"),
                        UnknownUidException.class,
                        "not found"
                ),

                new TestCase(
                        new BadRequestException("bad req"),
                        InvalidAttributeValueException.class,
                        "400"
                ),

                new TestCase(
                        new IOException("io failure"),
                        ConnectorIOException.class,
                        "io failure"
                ),

                new TestCase(
                        new SocketTimeoutException("io failure"),
                        OperationTimeoutException.class,
                        "io failure"
                ),

                new TestCase(
                        new NoRouteToHostException("io failure"),
                        OperationTimeoutException.class,
                        "io failure"
                ),

                new TestCase(
                        new UnknownUidException("io failure"),
                        UnknownUidException.class,
                        "io failure"
                ),

                new TestCase(
                        new NotFoundException("io failure"),
                        UnknownUidException.class,
                        "io failure"
                ),

                new TestCase(
                        new NotAuthorizedException("io failure"),
                        InvalidCredentialException.class,
                        "io failure"
                ),

                new TestCase(
                        new ConnectionFailedException("io failure"),
                        ConnectionFailedException.class,
                        "io failure"
                ),

                new TestCase(
                        new ConnectorException("io failure"),
                        ConnectorException.class,
                        "io failure"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provide_exceptions")
    @DisplayName("Should convert exception to expected type and message")
    void should_call_handle_connector_exception_when_create_member_fails(TestCase tc) throws Exception {
        BitwardenConnector connector = new BitwardenConnector();

        Exception actual = tc.inputException;

        MemberProcessing memberProcessing = mock(MemberProcessing.class);
        GroupsProcessing groupsProcessing = mock(GroupsProcessing.class);

        setField(connector, "memberProcessing", memberProcessing);
        setField(connector, "groupsProcessing", groupsProcessing);

        when(memberProcessing.create(anySet(), any(OperationOptions.class)))
                .thenAnswer(invocation -> { throw actual; });

        ObjectClass objectClass = new ObjectClass(MemberProcessing.OBJECT_CLASS_NAME);
        Set<Attribute> attrs = Collections.emptySet();
        OperationOptions options = new OperationOptionsBuilder().build();

        assertThrows(tc.expectedExceptionClass,
                () -> connector.create(objectClass, attrs, options));
    }

    @ParameterizedTest
    @MethodSource("provide_exceptions")
    @DisplayName("Should convert exception to expected type and message")
    void should_call_handle_connector_exception_when_create_group_fails(TestCase tc) throws Exception {
        BitwardenConnector connector = new BitwardenConnector();

        Exception actual = tc.inputException;

        GroupsProcessing groupsProcessing = mock(GroupsProcessing.class);

        setField(connector, "groupsProcessing", groupsProcessing);

        when(groupsProcessing.create(anySet(), any(OperationOptions.class)))
                .thenAnswer(invocation -> { throw actual; });

        ObjectClass objectClass = new ObjectClass(GroupsProcessing.OBJECT_CLASS_NAME);
        Set<Attribute> attrs = Collections.emptySet();
        OperationOptions options = new OperationOptionsBuilder().build();

        assertThrows(tc.expectedExceptionClass,
                () -> connector.create(objectClass, attrs, options));
    }

    static class TestCase {
        final Exception inputException;
        final Class<? extends Exception> expectedExceptionClass;
        final String expectedMessageContains;

        TestCase(Exception inputException,
                 Class<? extends Exception> expectedExceptionClass,
                 String expectedMessageContains) {

            this.inputException = inputException;
            this.expectedExceptionClass = expectedExceptionClass;
            this.expectedMessageContains = expectedMessageContains;
        }
    }
}
