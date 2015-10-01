package fi.vincit.multiusertest.runner.junit;

import java.util.Collections;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.Suite;

import fi.vincit.multiusertest.annotation.MultiUserTestConfig;
import fi.vincit.multiusertest.annotation.TestUsers;
import fi.vincit.multiusertest.util.Optional;
import fi.vincit.multiusertest.util.TestConfiguration;
import fi.vincit.multiusertest.util.UserIdentifier;

/**
 * <p>
 * Test runner for executing tests with multiple creator-user combinations. Creator
 * is the user creating a resource e.g. project or a customer. User is the user who
 * later uses/edits/deletes the previously created resource. Used with JUnit's
 * {@link org.junit.runner.RunWith} annotation.
 * </p>
 * <p>
 * MultiUserTestRunner requires the test class to have {@link fi.vincit.multiusertest.annotation.TestUsers} annotation which
 * will define with what users the tests are executed. To configure the test runner use
 * {@link fi.vincit.multiusertest.annotation.MultiUserTestConfig} annotation.
 * </p>
 * <p>
 * There are two different type of definitions that can be used: roles and users. Roles
 * create a new user to DB with the given role. Users use existing users that are found in
 * the DB. The syntax is <i>[role|user]:[role name|username]</i> e.g. <i>"role:ROLE_ADMIN"</i> or <i>"user:username"</i>.
 * </p>
 * <p>
 * There are also two special definitions that can be used for test users (not creators): {@link fi.vincit.multiusertest.annotation.TestUsers#CREATOR} and
 * {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER}. {@link fi.vincit.multiusertest.annotation.TestUsers#CREATOR} uses
 * the the same user as the resource was generated. {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER}
 * creates a new user with the same role as the creator had.
 * </p>
 * <p>
 * There is also a special role {@link fi.vincit.multiusertest.annotation.TestUsers#ANONYMOUS} both for
 * the creator and the user. This means that the current user or creator isn't logged in.
 * </p>
 * <p>
 * If no users are defined {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER} will be used as the default
 * user definition. Creators can't use {@link fi.vincit.multiusertest.annotation.TestUsers#NEW_USER} or
 * {@link fi.vincit.multiusertest.annotation.TestUsers#CREATOR} roles since those roles are tied to the current
 * creator role.
 * </p>
 */
public class MultiUserTestRunner extends Suite {

    private static final List<Runner> NO_RUNNERS = Collections
            .<Runner>emptyList();
    public static final String ROLE_PREFIX = "role:";
    public static final String USER_PREFIX = "user:";

    private final List<Runner> runners;

    public MultiUserTestRunner(Class<?> klass) throws Throwable {
        super(klass, NO_RUNNERS);
        TestConfiguration configuration = getConfigurationOrThrow();
        TestRunnerFactory runnerFactory = createTestRunner(configuration);
        this.runners = runnerFactory.createRunnersForRoles(
                configuration.getCreatorIdentifiers(),
                configuration.getUserIdentifiers()
        );
    }

    private TestRunnerFactory createTestRunner(TestConfiguration testConfiguration) throws NoSuchMethodException {
        if (testConfiguration.getRunner().isPresent()) {
            try {
                return new TestRunnerFactory(
                        getTestClass(),
                        testConfiguration.getRunner().get().getConstructor(Class.class, UserIdentifier.class, UserIdentifier.class)
                );
            } catch (NoSuchMethodException e) {
                throw new NoSuchMethodException("Runner must have constructor with class, UserIdentifier, UserIdentifier parameters");
            }

        } else {
            throw new IllegalArgumentException("TestUsers.runner must not be null");
        }
    }


    private TestConfiguration getConfigurationOrThrow() throws Exception {
        Optional<TestUsers> testRolesAnnotation =
                Optional.ofNullable(getTestClass().getJavaClass().getAnnotation(TestUsers.class));
        Optional<MultiUserTestConfig> config =
                Optional.ofNullable(getTestClass().getJavaClass().getAnnotation(MultiUserTestConfig.class));
        if (testRolesAnnotation.isPresent()) {
            return TestConfiguration.fromTestUsers(testRolesAnnotation, config);
        } else {
            throw new IllegalStateException(
                    "No users defined for test class "
                            + getTestClass().getName()
                            + " Use " + TestUsers.class.getName() + " class"
            );
        }
    }

    @Override
    protected List<Runner> getChildren() {
        return runners;
    }

}
