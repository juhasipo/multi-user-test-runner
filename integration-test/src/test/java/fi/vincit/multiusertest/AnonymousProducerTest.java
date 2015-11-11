package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.Authentication.toFail;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;

@RunWithUsers(producers = {RunWithUsers.ANONYMOUS},
        consumers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class AnonymousProducerTest extends ConfiguredTest {

    @Test
    public void producerLoggedIn() {
        assertThat(SecurityUtil.getLoggedInUser(), nullValue());
    }

    @Test
    public void consumerLoggedIn() {
        logInAs(LoginRole.CONSUMER);
        assertThat(SecurityUtil.getLoggedInUser().getUsername(), is(getConsumer().getUsername()));
    }

    @Test
    public void producerLoggedInAfterUser() {
        logInAs(LoginRole.CONSUMER);
        logInAs(LoginRole.PRODUCER);

        assertThat(SecurityUtil.getLoggedInUser(), nullValue());
    }

    @Test
    public void expectFailureAnonymousProducer() {
        logInAs(LoginRole.PRODUCER);
        authorization().expect(toFail(ifAnyOf(RunWithUsers.PRODUCER)));
        throwIfUserRole(RunWithUsers.ANONYMOUS);
    }

    @Test
    public void dontExpectFailure() {
        authorization().dontExpectToFail();
    }

    private void throwIfUserRole(String identifier) {
        if (identifier.equals(RunWithUsers.ANONYMOUS)) {
            throw new IllegalStateException("Thrown when role was ANONYMOUS");
        } else {
            User.Role identifierRole = stringToRole(UserIdentifier.parse(identifier).getIdentifier());
            if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
                throw new IllegalStateException("Thrown when role was " + identifier);
            }
        }
    }

}