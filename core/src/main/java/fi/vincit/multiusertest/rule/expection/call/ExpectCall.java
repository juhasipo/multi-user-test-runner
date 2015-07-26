package fi.vincit.multiusertest.rule.expection.call;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;

import fi.vincit.multiusertest.rule.FailMode;
import fi.vincit.multiusertest.rule.expection.Expectation;
import fi.vincit.multiusertest.rule.expection.FunctionCall;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.UserIdentifier;
import fi.vincit.multiusertest.util.UserIdentifiers;

public class ExpectCall implements Expectation {

    private final FunctionCall functionCall;

    private static class Info {
        FailMode failMode;
        Optional<Class<? extends Throwable>> exceptionClass;

        public Info(FailMode failMode, Optional<Class<? extends Throwable>> exceptionClass) {
            this.failMode = failMode;
            this.exceptionClass = exceptionClass;
        }
    }

    private Map<UserIdentifier, Info> expectations = new HashMap<>();

    public ExpectCall(FunctionCall functionCall) {
        this.functionCall = functionCall;
    }

    public ExpectCall toFail(UserIdentifiers identifiers) {
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new Info(FailMode.EXPECT_FAIL, Optional.<Class<? extends Throwable>>of(AccessDeniedException.class)));
        }
        return this;
    }

    public ExpectCall toFailWithException(Class<? extends Throwable> exception, UserIdentifiers identifiers) {
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new Info(FailMode.EXPECT_FAIL, Optional.<Class<? extends Throwable>>of(exception)));
        }
        return this;
    }

    public ExpectCall notToFail(UserIdentifiers identifiers) {
        for (UserIdentifier identifier : identifiers.getIdentifiers()) {
            expectations.put(identifier, new Info(FailMode.EXPECT_NOT_FAIL, Optional.<Class<? extends Throwable>>empty()));
        }
        return this;
    }

    @Override
    public void execute(UserIdentifier userIdentifier) throws Throwable {
        Optional<Info> info = getFailInfo(userIdentifier);
        try {
            functionCall.call();
        } catch (Throwable e) {
            if (info.isPresent()) {
                FailMode failMode = info.get().failMode;
                boolean isExpectedException = isExpectedException(info, e);

                if (failMode == FailMode.EXPECT_NOT_FAIL) {
                    throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
                } else {
                    if(!isExpectedException){
                        throw new AssertionError("Expected to fail with user role, but with different type of exception. " +
                                "Expected <" + info.get().exceptionClass.get() + "> but was <" + e.getClass().getName() + ">", e);
                    } else {
                        return;
                    }
                }
            } else {
                throw new AssertionError("Not expected to fail with user role " + userIdentifier.toString(), e);
            }
        }

        if (info.isPresent()) {
            if (info.get().failMode == FailMode.EXPECT_FAIL) {
                throw new AssertionError("Expected to fail with exception " + info.get().exceptionClass.get().getName());
            }
        }

    }

    private boolean isExpectedException(Optional<Info> info, Throwable e) {
        return info.get().exceptionClass.isPresent() && info.get().exceptionClass.get().isInstance(e);
    }

    private Optional<Info> getFailInfo(UserIdentifier userIdentifier) {
        if (expectations.containsKey(userIdentifier)) {
            return Optional.of(expectations.get(userIdentifier));
        } else {
            return Optional.empty();
        }
    }
}