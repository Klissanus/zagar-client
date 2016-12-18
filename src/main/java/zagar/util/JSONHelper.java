package main.java.zagar.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.jetbrains.annotations.NotNull;
import protocol.model.*;

/**
 * @author apomosov
 */
public class JSONHelper {
  @NotNull
  private static RuntimeTypeAdapterFactory<Cell> cellAdapterFactory =
          RuntimeTypeAdapterFactory.of(Cell.class)
                  .registerSubtype(Virus.class)
                  .registerSubtype(EjectedMass.class)
                  .registerSubtype(Food.class)
                  .registerSubtype(PlayerCell.class);
  @NotNull
  private static Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .registerTypeAdapterFactory(cellAdapterFactory)
          .create();

  @NotNull
  public static String toJSON(@NotNull Object object) {
    return gson.toJson(object);
  }

  @NotNull
  public static <T> T fromJSON(@NotNull String json, @NotNull Class<T> type) throws JSONDeserializationException {
    try {
      return gson.fromJson(json, type);
    } catch (JsonSyntaxException e) {
      throw new JSONDeserializationException(e);
    }
  }

  @NotNull
  public static JsonObject getJSONObject(@NotNull String string) {
    return gson.fromJson(string, JsonObject.class);
  }
}
