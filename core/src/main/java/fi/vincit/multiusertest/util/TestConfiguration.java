package fi.vincit.multiusertest.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import fi.vincit.multiusertest.annotation.TestUsers;

public class TestConfiguration {

    private final Collection<UserIdentifier> creatorIdentifiers;
    private final Collection<UserIdentifier> userIdentifiers;
    private final Optional<Class<?>> runner;
    private final Optional<Class<? extends Throwable>> defaultException;

    public static TestConfiguration fromTestUsers(TestUsers testUsers) {
        if (testUsers != null) {
            return new TestConfiguration(
                    getDefinitions(testUsers.creators()),
                    getDefinitions(testUsers.users()),
                    testUsers.runner(),
                    testUsers.defaultException()
            );
        } else {
            return new TestConfiguration();
        }
    }

    private static Collection<UserIdentifier> getDefinitions(String[] definitions) {
        if (definitions != null) {
            Collection<UserIdentifier> userIdentifiers = new LinkedHashSet<>();
            for (String user : definitions) {
                userIdentifiers.add(UserIdentifier.parse(user));
            }
            return userIdentifiers;
        } else {
            return Collections.emptySet();
        }
    }

    TestConfiguration() {
        this.creatorIdentifiers = Collections.emptySet();
        this.userIdentifiers = Collections.emptySet();
        this.runner = Optional.empty();
        this.defaultException = Optional.empty();
    }

    TestConfiguration(Collection<UserIdentifier> creatorIdentifiers, Collection<UserIdentifier> userIdentifiers, Class<?> runner, Class<? extends Throwable> defaultException) {
        this.creatorIdentifiers = creatorIdentifiers;
        this.userIdentifiers = userIdentifiers;
        this.runner = Optional.<Class<?>>ofNullable(runner);
        this.defaultException = Optional.<Class<? extends Throwable>>ofNullable(defaultException);
    }

    public Collection<UserIdentifier> getCreatorIdentifiers() {
        return creatorIdentifiers;
    }

    public Collection<UserIdentifier> getUserIdentifiers() {
        return userIdentifiers;
    }

    public Optional<Class<?>> getRunner() {
        return runner;
    }

    public Optional<Class<? extends Throwable>> getDefaultException() {
        return defaultException;
    }
}
