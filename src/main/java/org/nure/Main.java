package org.nure;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) throws Exception {
        var runtime = Runtime.instance();

        // Profile for main agent container
        var profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true");
        runtime.createMainContainer(profile);

        createEnvironmentAgent(runtime);
        createSpeleologistAgent(runtime);
        createNavigatorAgent(runtime);
    }


    private static void createSpeleologistAgent(Runtime runtime) throws StaleProxyException {
        var speleologistProfile = new ProfileImpl();
        speleologistProfile.setParameter(Profile.MAIN_HOST, "localhost");
        speleologistProfile.setParameter(Profile.CONTAINER_NAME, "speleologist-agent");

        var speleologistContainer = runtime.createAgentContainer(speleologistProfile);

        speleologistContainer
                .createNewAgent("speleologist-agent", "org.nure.jade.SpeleologistAgent", new Object[]{})
                .start();
    }

    private static void createEnvironmentAgent(Runtime runtime) throws StaleProxyException {
        var environmentProfile = new ProfileImpl();
        environmentProfile.setParameter(Profile.MAIN_HOST, "localhost");
        environmentProfile.setParameter(Profile.CONTAINER_NAME, "environment-agent");

        var environmentContainer = runtime.createAgentContainer(environmentProfile);

        environmentContainer
                .createNewAgent("environment-agent", "org.nure.jade.EnvironmentAgent", new Object[]{})
                .start();
    }


    private static void createNavigatorAgent(Runtime runtime) throws StaleProxyException {
        var navigatorProfile = new ProfileImpl();
        navigatorProfile.setParameter(Profile.MAIN_HOST, "localhost");
        navigatorProfile.setParameter(Profile.CONTAINER_NAME, "navigator-agent");

        var navigatorContainer = runtime.createAgentContainer(navigatorProfile);

        navigatorContainer
                .createNewAgent("navigator-agent", "org.nure.jade.NavigatorAgent", new Object[]{})
                .start();
    }
}
