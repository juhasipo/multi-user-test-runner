package fi.vincit.multiusertest;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import fi.vincit.multiusertest.configuration.ConfiguredTest;
import fi.vincit.multiusertest.runner.junit.MultiUserTestRunner;
import fi.vincit.multiusertest.util.User;

@RunWithUsers(producers = {"role:ROLE_ADMIN", "role:ROLE_USER"})
@RunWith(MultiUserTestRunner.class)
public class ProducerOnlyTest extends ConfiguredTest {


    @Test
    @RunWithUsers(producers = {"role:ROLE_ADMIN"})
    public void runProducerAdmin() {
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_USER"})
    public void runConsumerIsUser() {
        assertThat(getConsumerModel().getRole(), is(User.Role.ROLE_USER));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_ADMIN"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerAdminAndProducerAdmin() {
        assertThat(getConsumerModel().getRole(), is(User.Role.ROLE_ADMIN));
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_USER"}, producers = {"role:ROLE_ADMIN"})
    public void runConsumerUserAndProducerAdmin() {
        assertThat(getConsumerModel().getRole(), is(User.Role.ROLE_USER));
        assertThat(getProducerModel().getRole(), is(User.Role.ROLE_ADMIN));
    }

    @Test
    @RunWithUsers(consumers = {"role:ROLE_VISITOR"}, producers = {"role:ROLE_VISITOR"})
    public void neverRun() {
        throw new AssertionError("Should never call this method");
    }

}