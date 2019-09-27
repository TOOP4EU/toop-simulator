package eu.toop.simulator;

import org.eclipse.jetty.server.Server;

public interface IServerStartObserver {
  void serverStarted(Server aServer);
}
