package fi.vincit.multiusertest.rule.expectation2.call;

import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class FunctionCallExceptionExpectationTest {
    @Rule
    public ExpectedException expectException = ExpectedException.none();

    @Test
    public void throwIfExceptionIsExpected() throws Exception {
        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class
                );

        expectException.expect(AssertionError.class);
        sut.handleExceptionNotThrown(UserIdentifier.getAnonymous());
    }

    @Test
    public void throwIfExpectationNotExpected() throws Throwable {
        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalStateException());
    }

    @Test
    public void throwIfExpectationNotExpected_UnexpectedException() throws Throwable {
        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class
                );

        expectException.expect(IllegalArgumentException.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void throwIfExpectationNotExpected_CaptureDerivedExceptions() throws Throwable {
        FunctionCallExceptionExpectation<RuntimeException> sut =
                new FunctionCallExceptionExpectation<>(
                        RuntimeException.class
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException());
    }

    @Test
    public void throwIfExpectationNotExpected_Assert() throws Throwable {
        Assertion assertion = new Assertion();

        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class,
                        expectException -> assertion.exception = expectException
                );

        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalStateException("Foo"));
        assertThat(assertion.exception.getMessage(), is("Foo"));
    }

    @Test
    public void throwIfExpectationNotExpected_DontAssertIfWrongException() throws Throwable {
        Assertion assertion = new Assertion();

        FunctionCallExceptionExpectation<IllegalStateException> sut =
                new FunctionCallExceptionExpectation<>(
                        IllegalStateException.class,
                        expectException -> assertion.exception = expectException
                );

        expectException.expect(IllegalArgumentException.class);
        sut.handleThrownException(UserIdentifier.getAnonymous(), new IllegalArgumentException("Foo"));
        assertThat(assertion.exception, nullValue());
    }

    private static class Assertion {
        Throwable exception;
    }
}