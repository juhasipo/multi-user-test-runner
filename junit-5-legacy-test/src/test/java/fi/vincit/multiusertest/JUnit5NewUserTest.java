package fi.vincit.multiusertest;

import fi.vincit.multiusertest.annotation.MultiUserConfigClass;
import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.rule.AuthorizationRule;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        consumers = {RunWithUsers.WITH_PRODUCER_ROLE})
@RunWith(MultiUserTestRunner.class)
public class JUnit5NewUserTest {

    @MultiUserConfigClass
    private ConfiguredTest configuredTest = new ConfiguredTest();

    @Rule
    public AuthorizationRule authorizationRule = new AuthorizationRule();

    @Test
    @Ignore("TODO")
    public void test() {
    }

    /*
    @Test(expected = AssertionError.class)
    public void expectFailureNewUser() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(call(this::throwException)
                        .toFail(ifAnyOf(RunWithUsers.WITH_PRODUCER_ROLE))
        );
    }

    @Test
    public void expectFailureUser() throws Throwable {
        configuredTest.logInAs(LoginRole.CONSUMER);
        authorizationRule.expect(call(() -> throwIfUserRole("role:ROLE_USER"))
                        .toFail(ifAnyOf("role:ROLE_USER"))
        );
    }

    private void throwIfUserRole(String identifier) {
        User.Role identifierRole = configuredTest.stringToRole(UserIdentifier.parse(identifier).getIdentifier());
        if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        }
    }
    */

    private void throwException() {
        throw new IllegalStateException();
    }
}
