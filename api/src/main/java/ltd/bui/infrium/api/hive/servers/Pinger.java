package ltd.bui.infrium.api.hive.servers;

import lombok.Cleanup;
import lombok.Data;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Data
public final class Pinger implements Serializable, Cloneable {

  private static final String COLOR_CODE = "\u00A7"; // § Symbol
  private static final String COLOR_IDK = "\u0000"; // unknown symbol

  private String address;
  private int port, timeout, pingVersion, protocolVersion, playersOnline, maxPlayers;
  private String gameVersion, motd, lastResponse;

  public Pinger(final String address, final int port) {
    this.address = "localhost";
    this.port = 25565;
    this.timeout = 2300;
    this.pingVersion = -1;
    this.protocolVersion = -1;
    this.playersOnline = -1;
    this.maxPlayers = -1;
    this.setAddress(address);
    this.setPort(port);
  }

  public boolean ping() {
    // don't know how this works - just found into the protocol wiki of minecraft
    // just edited a bit of it to make it work
    try {
      @Cleanup Socket socket = new Socket();
      socket.setSoTimeout(this.timeout);
      socket.connect(new InetSocketAddress(this.getAddress(), this.getPort()), this.getTimeout());
      @Cleanup final OutputStream outputStream = socket.getOutputStream();
      @Cleanup final DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
      @Cleanup final InputStream inputStream = socket.getInputStream();
      @Cleanup
      final InputStreamReader inputStreamReader =
          new InputStreamReader(inputStream, StandardCharsets.UTF_16BE);

      dataOutputStream.write(0xFE);

      int b;
      final StringBuilder str = new StringBuilder();
      while ((b = inputStream.read()) != -1) {
        if (b > 16 && b != 255 && b != 23 && b != 24) {
          str.append((char) b);
        }
      }

      this.lastResponse = str.toString();
      if (lastResponse.startsWith(COLOR_CODE)) {
        final String[] data = lastResponse.split(COLOR_IDK);
        this.setPingVersion(Integer.parseInt(data[0].substring(1)));
        this.setProtocolVersion(Integer.parseInt(data[1]));
        this.setGameVersion(data[2]);
        this.setMotd(data[3]);
        this.setPlayersOnline(Integer.parseInt(data[4]));
        this.setMaxPlayers(Integer.parseInt(data[5]));
      } else {
        final String[] data = lastResponse.split(COLOR_CODE);
        this.setMotd(data[0]);
        this.setPlayersOnline(Integer.parseInt(data[1]));
        this.setMaxPlayers(Integer.parseInt(data[2]));
      }
    } catch (IOException exception) {
      this.pingVersion = -1;
      this.protocolVersion = -1;
      this.playersOnline = -1;
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Pinger{address='%s', port=%d, timeout=%d, pingVersion=%d, protocolVersion=%d, playersOnline=%d, maxPlayers=%d, gameVersion='%s', motd='%s', lastResponse='%s'}"
        .formatted(
            address,
            port,
            timeout,
            pingVersion,
            protocolVersion,
            playersOnline,
            maxPlayers,
            gameVersion,
            motd,
            lastResponse);
  }

  public Pinger clone() {
    return new Pinger(this.address, this.port);
  }
}
