package com.example.imagecropper.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GsonUtil {

    public static String ArrayListToJson(List list) {
        return new Gson().toJson(list);
    }

    public static List<?> JsonToArrayList(String stringJson, Type listType) {
        // java: Type type = new TypeToken<List<JsonBean>>(){}.getType();
        // kotlin: val listType = object : TypeToken<List<JsonBean>>() { }.type
       return new Gson().fromJson(stringJson, listType);
    }

    public static Object JsonToObject(String stringJson, Class covertObject) {
        return new Gson().fromJson(stringJson, covertObject);
    }

    public static Gson getCustomGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        CustomizedObjectTypeAdapter adapter = new CustomizedObjectTypeAdapter();
        gsonBuilder.registerTypeAdapter(Map.class, adapter);
        gsonBuilder.registerTypeAdapter(List.class, adapter);
        return gsonBuilder.create();
    }

    public static class CustomizedObjectTypeAdapter extends TypeAdapter<Object> {
        private final TypeAdapter<Object> delegate = new Gson().getAdapter(Object.class);

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            delegate.write(out, value);
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            JsonToken token = in.peek();
            switch (token) {
                case BEGIN_ARRAY:
                    List<Object> list = new ArrayList<Object>();
                    in.beginArray();
                    while (in.hasNext()) {
                        list.add(read(in));
                    }
                    in.endArray();
                    return list;

                case BEGIN_OBJECT:
                    Map<String, Object> map = new LinkedTreeMap<String, Object>();
                    in.beginObject();
                    while (in.hasNext()) {
                        map.put(in.nextName(), read(in));
                    }
                    in.endObject();
                    return map;

                case STRING:
                    return in.nextString();

                case NUMBER:
                    //return in.nextDouble();
                    String n = in.nextString();
                    if (n.contains(".") || n.contains("e") || n.contains("E")) {
                        BigDecimal value = BigDecimal.valueOf(Double.parseDouble(n));
                        return new JsonPrimitive(value);
                    }
                    return Long.parseLong(n);

                case BOOLEAN:
                    return in.nextBoolean();

                case NULL:
                    in.nextNull();
                    return null;

                default:
                    throw new IllegalStateException();
            }
        }
    }

}
