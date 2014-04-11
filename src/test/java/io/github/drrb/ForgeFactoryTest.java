package io.github.drrb;

import com.thoughtworks.go.plugin.api.material.packagerepository.Property;
import com.thoughtworks.go.plugin.api.material.packagerepository.RepositoryConfiguration;
import io.github.drrb.forge.Forge;
import org.junit.Before;
import org.junit.Test;

import static io.github.drrb.ForgePollerPluginConfig.FORGE_URL;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class ForgeFactoryTest {

    private ForgeFactory forgeFactory;

    @Before
    public void setUp() throws Exception {
        forgeFactory = new ForgeFactory();
    }

    @Test
    public void shouldBuildForge() throws Exception {
        RepositoryConfiguration repoConfig = new RepositoryConfiguration();
        repoConfig.add(new Property(FORGE_URL, "http://forge.example.com"));
        Forge forge = forgeFactory.build(repoConfig);
        assertThat(forge.getUrl(), is("http://forge.example.com"));
    }
}
