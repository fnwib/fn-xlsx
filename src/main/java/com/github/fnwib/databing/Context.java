package com.github.fnwib.databing;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.deser.CellDeserializer;
import com.github.fnwib.databing.deser.DeserializerConfig;
import com.github.fnwib.databing.ser.Serializer;
import com.github.fnwib.databing.ser.SerializerConfig;
import com.github.fnwib.databing.title.TitleResolver;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.databing.valuehandler.ValueHandlerConfig;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Set;

public enum Context {

    INSTANCE;

    private final DeserializerConfig deserializerConfig = new DeserializerConfig();
    private final SerializerConfig   serializerConfig   = new SerializerConfig();
    private final ValueHandlerConfig valueHandlerConfig = new ValueHandlerConfig();
    private final TitleResolver      titleResolver      = new TitleResolver();

    /**
     * 注册类型转换器
     *
     * @param cellDeserializer
     */
    public void register(CellDeserializer<?> cellDeserializer) {
        this.deserializerConfig.register(cellDeserializer);
    }

    public CellDeserializer<?> findCellDeserializer(JavaType javaType) {
        return deserializerConfig.findCellDeserializer(javaType);
    }


    public void register(Serializer serializer) {
        this.serializerConfig.register(serializer);
    }

    /**
     * @param javaType
     * @return
     */
    public Serializer findSerializer(JavaType javaType) {
        return serializerConfig.findSerializer(javaType);
    }

    /**
     * 注册全局值处理器
     *
     * @param valueHandlers
     */
    public void registerContentValueHandlers(ValueHandler... valueHandlers) {
        for (ValueHandler valueHandler : valueHandlers) {
            this.valueHandlerConfig.register(valueHandler);
        }
    }


    public List<ValueHandler> findContentValueHandlers() {
        return valueHandlerConfig.getValueHandlers();
    }

    /**
     * 注册title处理器
     *
     * @param valueHandlers
     */
    public void registerTitltValueHandlers(List<ValueHandler> valueHandlers) {
        titleResolver.register(valueHandlers);
    }

    /**
     * 注册title处理器
     *
     * @param valueHandlers
     */
    public void registerTitleValueHandlers(ValueHandler... valueHandlers) {
        titleResolver.register(valueHandlers);
    }

    public Set<PropertyConverter> resolve(Class<?> entityClass, Row row) {
        return titleResolver.resolve(entityClass, row);
    }
}
