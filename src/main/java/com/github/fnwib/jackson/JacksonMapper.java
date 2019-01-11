package com.github.fnwib.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class JacksonMapper {

	private final ObjectMapper mapper;

	public JacksonMapper() {
		this.mapper = new ObjectMapper().findAndRegisterModules();
		SimpleModule m = new SimpleModule();
		m.addKeyDeserializer(Sequence.class, new CellSequenceDeserializer());
		mapper.registerModule(m);
	}

	public void registerModule(SimpleModule... simpleModules) {
		Objects.requireNonNull(simpleModules);
		for (SimpleModule module : simpleModules) {
			mapper.registerModule(module);
		}
	}

	public ObjectMapper get() {
		return mapper;
	}

}
