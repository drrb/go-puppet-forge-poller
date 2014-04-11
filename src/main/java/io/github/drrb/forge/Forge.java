package io.github.drrb.forge;

import com.thoughtworks.go.plugin.api.material.packagerepository.PackageConfiguration;

public class Forge {
    public static class PingFailure extends Exception {
        public PingFailure(String message) {
            super(message);
        }
    }

    public void ping() throws PingFailure {
    }

    public void ping(PackageConfiguration packageConfig) throws PingFailure {
    }
}
