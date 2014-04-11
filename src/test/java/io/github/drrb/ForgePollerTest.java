package io.github.drrb;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import com.thoughtworks.go.plugin.api.response.Result;
import io.github.drrb.forge.Forge;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static io.github.drrb.forge.Forge.PingFailure;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ForgePollerTest {
    private ForgePoller poller;
    @Mock
    private Forge forge;
    @Mock
    private ForgeFactory forgeFactory;

    private PackageConfiguration packageConfig;
    private RepositoryConfiguration repoConfig;

    @Before
    public void setUp() throws Exception {
        poller = new ForgePoller(forgeFactory);

        packageConfig = new PackageConfiguration();
        repoConfig = new RepositoryConfiguration();

        when(forgeFactory.build(repoConfig)).thenReturn(forge);
    }

    @Test
    public void shouldReturnSuccessWhenForgeConnectionSucceeds() throws Exception {
        Result result = poller.checkConnectionToRepository(repoConfig);

        assertThat(result.isSuccessful(), is(true));
    }

    @Test
    public void shouldReturnFailureWhenForgeConnectionFails() throws Exception {
        doThrow(new PingFailure("Connection refused")).when(forge).ping(packageConfig);

        Result result = poller.checkConnectionToRepository(repoConfig);

        assertThat(result.isSuccessful(), is(false));
        assertThat(result.getMessages(), hasItem(containsString("Connection refused")));
    }

    @Test
    public void shouldReturnSuccessWhenPackageConnectionSucceeds() throws Exception {
        Result result = poller.checkConnectionToPackage(packageConfig, repoConfig);

        assertThat(result.isSuccessful(), is(true));
    }

    @Test
    public void shouldReturnFailureWhenPackageConnectionFails() throws Exception {
        doThrow(new PingFailure("Not found")).when(forge).ping(packageConfig);

        Result result = poller.checkConnectionToPackage(packageConfig, repoConfig);

        assertThat(result.isSuccessful(), is(false));
        assertThat(result.getMessages(), hasItem(containsString("Not found")));
    }
}
