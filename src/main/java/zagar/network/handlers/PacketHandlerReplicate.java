package main.java.zagar.network.handlers;

import main.java.zagar.Game;
import main.java.zagar.util.JSONDeserializationException;
import main.java.zagar.util.JSONHelper;
import main.java.zagar.view.cells.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import protocol.commands.CommandReplicate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PacketHandlerReplicate implements PacketHandler {
  @NotNull
  private static final Logger log = LogManager.getLogger(PacketHandlerReplicate.class);

  public void handle(@NotNull String json) {
    CommandReplicate commandReplicate;
    try {
      commandReplicate = JSONHelper.fromJSON(json, CommandReplicate.class);
    } catch (JSONDeserializationException e) {
      e.printStackTrace();
      return;
    }

      List<Cell> cells = commandReplicate.getCells().stream()
              .map(cell->{
                if (cell instanceof protocol.model.PlayerCell) {
                  protocol.model.PlayerCell c = ((protocol.model.PlayerCell) cell);
                  String name = c.getPlayerName();
                  return new PlayerCell(c.getCoordinate(), Cell.generateColor(),c.getMass(),c.getRadius(),name);
                } else if (cell instanceof protocol.model.Virus) {
                  protocol.model.Virus c = ((protocol.model.Virus) cell);
                  return new Virus(c.getCoordinate(),c.getMass(),c.getRadius());
                } else if (cell instanceof protocol.model.Food) {
                  protocol.model.Food c = ((protocol.model.Food) cell);
                  return new Food(c.getCoordinate(),Cell.generateColor(),c.getMass(),c.getRadius());
                } else if (cell instanceof protocol.model.EjectedMass) {
                  protocol.model.EjectedMass c = ((protocol.model.EjectedMass) cell);
                  return new EjectedMass(c.getCoordinate(),Cell.generateColor(),c.getMass(),c.getRadius());
                }
                return null;
              })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
    Game.updateBuffer(cells);
  }
}
