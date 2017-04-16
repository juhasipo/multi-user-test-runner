package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static fi.vincit.multiusertest.rule.expectation2.TestExpectations.expectException;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
@MultiUserTestConfig
public class BasicTest {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorization = new AuthorizationRule();

    @Test
    public void producerLoggedIn() {
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(configuredTest.getProducer().getUsername()));
    }

    @Test
    public void consumerLoggedIn() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(configuredTest.getConsumer().getUsername()));
    }

    @Test
    public void producerLoggedInAfterConsumer() {
        configuredTest.logInAs(LoginRole.CONSUMER);
        configuredTest.logInAs(LoginRole.PRODUCER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(configuredTest.getProducer().getUsername()));
    }

    @Test
    public void expectFailureProducer() throws Throwable {
        authorization.testCall(() -> throwIfUserIs(configuredTest.getProducer()))
                .whenCalledWithAnyOf(RunWithUsers.PRODUCER)
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test
    public void expectFailureWithProducerRole() throws Throwable {
        authorization.testCall(() -> throwIfUserIs(configuredTest.getConsumer()))
                .whenCalledWithAnyOf(RunWithUsers.WITH_PRODUCER_ROLE)
                .then(expectException(IllegalStateException.class))
                .test();
    }

    @Test
    public void expectFailureConsumer() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorization.testCall(() -> throwIfUserRole("role:ROLE_USER"))
                .whenCalledWithAnyOf("role:ROLE_USER")
                .then(expectException(IllegalStateException.class))
                .test();
    }

    private void throwIfUserRole(String identifier) {
        User.Role identifierRole = configuredTest.stringToRole(UserIdentifier.parse(identifier).getIdentifier());
        if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        }
    }

    private void throwIfUserIs(User user) {
        if (SecurityUtil.getLoggedInUser().getUsername().equals(user.getUsername())) {
            throw new IllegalStateException("Thrown when user was " + user);
        }
    }

}
