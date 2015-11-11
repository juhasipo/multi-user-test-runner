package fi.vincit.multiusertest.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;

import fi.vincit.multiusertest.test.AbstractUserRoleIT;

public class RunnerDelegateTest {

    @Test
    public void testGetName() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        assertThat(
                delegate.getName(new TestClass(Object.class)),
                is("java.lang.Object: producer = role:ROLE_ADMIN; consumer = role:ROLE_USER")
        );
    }

    @Test
    public void getName() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        assertThat(
                delegate.testName(mockFrameworkMethod()),
                is("testMethod: producer = role:ROLE_ADMIN; consumer = role:ROLE_USER")
        );
    }

    @Ignore
    private static class TestConfig extends AbstractUserRoleIT<String, String> {
        @Override
        public void loginWithUser(String s) {
        }

        @Override
        public String createUser(String username, String firstName, String lastName, String userRole, LoginRole loginRole) {
            return null;
        }

        @Override
        public String stringToRole(String role) {
            return null;
        }

        @Override
        public String getUserByUsername(String username) {
            return null;
        }

        public RoleContainer<String> getConsumerModel() {
            return super.getConsumerModel();
        }

        public RoleContainer<String> getProducerModel() {
            return super.getProducerModel();
        }
    }

    @Test
    public void testCreateTest() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );
        TestConfig instance = (TestConfig) delegate.createTest(new TestConfig());

        assertThat(instance, notNullValue());
        assertThat(instance.getProducerModel(), notNullValue());
        assertThat(instance.getProducerModel().getIdentifier(), is("ROLE_ADMIN"));

        assertThat(instance.getConsumerModel(), notNullValue());
        assertThat(instance.getConsumerModel().getIdentifier(), is("ROLE_USER"));
    }

    @Test(expected = IllegalStateException.class)
    public void testCreateTestWithInvalidClassType() {
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER")
        );

        delegate.createTest(new Object());
    }

    @Test
    public void testIsIgnoredByChild() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(false);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), false), is(true));
    }

    private FrameworkMethod mockFrameworkMethod() {
        FrameworkMethod frameworkMethod = mock(FrameworkMethod.class);
        when(frameworkMethod.getName()).thenReturn("testMethod");
        return frameworkMethod;
    }

    @Test
    public void testIsIgnoredWhenParentIsIgnored() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(true);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), true), is(true));
    }

    @Test
    public void testIsIgnoredWhenParentAndChildAreIgnored() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(false);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), true), is(true));
    }

    @Test
    public void testIsNotIgnored() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        when(testMethodFilter.shouldRun(any(FrameworkMethod.class))).thenReturn(true);

        assertThat(delegate.isIgnored(mockFrameworkMethod(), false), is(false));
    }

    @Test
    public void testFilter() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        List<FrameworkMethod> methods = Arrays.asList(mockFrameworkMethod(), mockFrameworkMethod());
        when(testMethodFilter.filter(any(List.class))).thenReturn(methods);

        List<FrameworkMethod> inputMethods = Arrays.asList(mockFrameworkMethod(), mockFrameworkMethod(), mockFrameworkMethod());
        assertThat(delegate.filterMethods(inputMethods), is(methods));
    }

    @Test
    public void testFilterWhenNothingToRun() {
        TestMethodFilter testMethodFilter = mock(TestMethodFilter.class);
        RunnerDelegate delegate = new RunnerDelegate(
                UserIdentifier.parse("role:ROLE_ADMIN"),
                UserIdentifier.parse("role:ROLE_USER"),
                testMethodFilter
        );

        List<FrameworkMethod> methods = Collections.emptyList();
        when(testMethodFilter.filter(any(List.class))).thenReturn(methods);

        List<FrameworkMethod> inputMethods = Arrays.asList(mockFrameworkMethod(), mockFrameworkMethod(), mockFrameworkMethod());
        assertThat(delegate.filterMethods(inputMethods), is(inputMethods));
    }
}