package com.github.fnwib.context;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fnwib.jackson.JacksonMapper;
import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.plugin.deser.CellDeserializer;
import com.github.fnwib.plugin.deser.LocalDateCellDeserializer;
import com.github.fnwib.plugin.ser.Serializer;

import java.util.Collection;
import java.util.Objects;

/**
 * 全局配置
 */
public enum Context {
	/**
	 * singleton
	 */
	INSTANCE;

	private final DeserializerConfig deserializerConfig;
	private final SerializerConfig serializerConfig;
	private final LocalConfig contextConfig;
	private final JacksonMapper jacksonMapper;

	Context() {
		deserializerConfig = new DeserializerConfig();
		deserializerConfig.register(new LocalDateCellDeserializer());
		serializerConfig = new SerializerConfig();
		this.contextConfig = new LocalConfig();
		jacksonMapper = new JacksonMapper();
	}

	public ObjectMapper getObjectMapper() {
		return jacksonMapper.get();
	}

	public synchronized void registerJacksonModules(SimpleModule... simpleModules) {
		Objects.requireNonNull(simpleModules);
		for (SimpleModule module : simpleModules) {
			jacksonMapper.registerModule(module);
		}
	}

	/**
	 * 注册全局反序列化组件
	 *
	 * @param cellDeserializer
	 */
	public synchronized void register(CellDeserializer<?> cellDeserializer) {
		this.deserializerConfig.register(cellDeserializer);
	}

	public CellDeserializer findCellDeserializer(JavaType javaType) {
		return deserializerConfig.findCellDeserializer(javaType);
	}

	/**
	 * 注册全局序列化组件
	 *
	 * @param serializer
	 */
	public synchronized void register(Serializer serializer) {
		this.serializerConfig.register(serializer);
	}

	public Serializer findSerializer(JavaType javaType) {
		return serializerConfig.findSerializer(javaType);
	}

	/**
	 * 注册全局值处理器
	 *
	 * @param valueHandlers
	 */
	public synchronized void registerContentValueHandlers(Collection<ValueHandler> valueHandlers) {
		this.contextConfig.registerReadContentValueHandlers(valueHandlers);
	}


	public Collection<ValueHandler> findContentValueHandlers() {
		return contextConfig.getContentValueHandlers();
	}

	/**
	 * 注册title处理器
	 *
	 * @param valueHandlers
	 */
	public synchronized void registerTitltValueHandlers(Collection<ValueHandler> valueHandlers) {
		contextConfig.registerTitleValueHandlers(valueHandlers);
	}

	public LocalConfig getContextConfig() {
		return contextConfig;
	}
}
