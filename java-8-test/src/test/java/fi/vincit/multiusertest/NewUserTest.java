package fi.vincit.multiusertest;

import static fi.vincit.multiusertest.rule.expection.Expectations.call;
import static fi.vincit.multiusertest.util.UserIdentifiers.ifAnyOf;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.LoginRole;
import fi.vincit.multiusertest.util.SecurityUtil;
import fi.vincit.multiusertest.util.User;
import fi.vincit.multiusertest.util.UserIdentifier;

@TestUsers(creators = {"role:ROLE_ADMIN", "role:ROLE_USER"},
        users = {TestUsers.NEW_USER})
@RunWith(MultiUserTestRunner.class)
public class NewUserTest extends ConfiguredTest {

    @Test(expected = AssertionError.class)
    public void expectFailureNewUser() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(call(this::throwException)
                        .toFail(ifAnyOf(TestUsers.NEW_USER))
        );
    }

    @Test
    public void expectFailureUser() throws Throwable {
        logInAs(LoginRole.USER);
        authorization().expect(call(() -> throwIfUserRole("role:ROLE_USER"))
                        .toFail(ifAnyOf("role:ROLE_USER"))
        );
    }

    private void throwIfUserRole(String identifier) {
        User.Role identifierRole = stringToRole(UserIdentifier.parse(identifier).getIdentifier());
        if (SecurityUtil.getLoggedInUser().getRole() == identifierRole) {
            throw new IllegalStateException("Thrown when role was " + identifier);
        }
    }

    private void throwException() {
        throw new IllegalStateException();
    }
}
