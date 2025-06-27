package com.infrium.api.mongoserializer;

import com.infrium.api.util.Constants;
import org.bson.Document;

public class MongoSerializer {
  MongoSerializer() {}

  public static Document serialize(Object object) {
    return Document.parse(Constants.get().getGson().toJson(object));
  }

  public static <T> T deserialize(Document d, Class<T> clazz) {
    return Constants.get().getGson().fromJson(d.toJson(), clazz);
  }
}
