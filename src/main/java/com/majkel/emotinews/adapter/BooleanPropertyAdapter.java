package com.majkel.emotinews.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.IOException;

public final class BooleanPropertyAdapter extends TypeAdapter<BooleanProperty> {
    @Override
    public void write(JsonWriter writer, BooleanProperty booleanProperty) throws IOException {
        if(booleanProperty==null)
        {
            writer.nullValue();
            return;
        }
        writer.value(booleanProperty.get());
    }

    @Override
    public BooleanProperty read(JsonReader reader) throws IOException{
        JsonToken jsonToken=reader.peek();
        if(jsonToken==JsonToken.NULL)
        {
            reader.nextNull();
            return new SimpleBooleanProperty(false);
        }

        boolean val= reader.nextBoolean();
        return new SimpleBooleanProperty(val);
    }
}
