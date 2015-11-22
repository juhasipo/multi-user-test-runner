package fi.vincit.multiusertest.util;

import fi.vincit.multiusertest.annotation.RunWithUsers;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;

import java.util.Arrays;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TestMethodFilterTest {

    @Test
    public void testRunMetdhodWhenProducerAndConsumerSet() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[]{"role:ROLE_ADMIN"}, new String[]{"role:ROLE_ADMIN"});
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testDontRunMethodWhenConsumerDoesntMatch() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[] {"role:ROLE_ADMIN"}, new String[] {"role:ROLE_ADMIN"});
        assertThat(r.shouldRun(method), is(false));
    }

    @Test
    public void testDontRunMethodWhenProducerDoesntMatch() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[] {"role:ROLE_ADMIN"}, new String[] {"role:ROLE_ADMIN"});
        assertThat(r.shouldRun(method), is(false));
    }

    @Test
    public void testRunMethodWhenMultipleConsumerRoles() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"));

        FrameworkMethod method = mock(FrameworkMethod.class);

        mockTestUsers(method,
                new String[]{"role:ROLE_ADMIN"},
                new String[]{"role:ROLE_ADMIN", "role:ROLE_USER"}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenMultipleProducerRoles() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{"role:ROLE_ADMIN", "role:ROLE_USER"},
                new String[]{"role:ROLE_ADMIN"}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoConsumerDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{"role:ROLE_USER"},
                new String[]{}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoProducerDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{},
                new String[]{"role:ROLE_ADMIN"}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoRolesDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method,
                new String[]{},
                new String[]{}
        );
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenNoAnnotationDefined() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testFilterMethods() {
        TestMethodFilter filter = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method1 = mock(FrameworkMethod.class);
        mockTestUsers(method1,
                new String[]{"role:ROLE_USER"},
                new String[]{"role:ROLE_ADMIN"}
        );

        FrameworkMethod method2 = mock(FrameworkMethod.class);
        mockTestUsers(method2,
                new String[]{"role:ROLE_ADMIN"},
                new String[]{"role:ROLE_ADMIN"}
        );

        FrameworkMethod method3 = mock(FrameworkMethod.class);
        mockTestUsers(method3,
                new String[]{"role:ROLE_USER", "role:ROLE_ADMIN"},
                new String[]{"role:ROLE_ADMIN"}
        );

        assertThat(filter.filter(Arrays.asList(method1, method2, method3)),
                is(Arrays.asList(method1, method3)));
    }

    @Test
    public void testFilterMethods_EmptyList() {
        TestMethodFilter filter = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_USER"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        assertThat(filter.filter(Arrays.<FrameworkMethod>asList()).size(), is(0));
    }

    @Test(expected = NullPointerException.class)
    public void testRunMethodWhenConsumerNotSet() {
        new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                null);
    }

    @Test(expected = NullPointerException.class)
    public void testRunMethodWhenProducerNotSet() {
        new TestMethodFilter(
                null,
                UserIdentifier.parse("role:ROLE_ADMIN"));
    }

    @Test(expected = NullPointerException.class)
    public void testRunMethodWhenProducerNorConsumerNotSet() {
        new TestMethodFilter(
                null,
                null);
    }

    @Test
    public void testRunMethodWhenUserWithProducerRole() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_ADMIN"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[]{"role:ROLE_ADMIN"}, new String[]{RunWithUsers.WITH_PRODUCER_ROLE});
        assertThat(r.shouldRun(method), is(true));
    }

    @Test
    public void testRunMethodWhenUserWithProducerRole_WrongConsumerRole() {
        TestMethodFilter r = new TestMethodFilter(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"));

        FrameworkMethod method = mock(FrameworkMethod.class);
        mockTestUsers(method, new String[]{"role:ROLE_ADMIN"}, new String[]{RunWithUsers.WITH_PRODUCER_ROLE});
        assertThat(r.shouldRun(method), is(false));
    }

    private void mockTestUsers(FrameworkMethod method, String[] producers, String[] consumers) {
        RunWithUsers testUsers = mock(RunWithUsers.class);

        when(testUsers.producers()).thenReturn(producers);
        when(testUsers.consumers()).thenReturn(consumers);

        when(method.getAnnotation(RunWithUsers.class)).thenReturn(testUsers);
    }
}
