package com.github.fnwib.mapper;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.testentity.TestNested2;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;

import java.util.Collections;

public class MappersTest {

	@Test
	public void createNestedMapper() {
		LocalConfig localConfig = new LocalConfig();
		localConfig.setMaxNestLevel(2);
		Mappers.createNestedMapper(TestModel.class, localConfig, Collections.emptyList());
	}

	@Getter
	@Setter
	public class TestModel {
		@AutoMapping(complex = ComplexEnum.Nested, order = 2)
		private TestNested2 testNested2;
		@AutoMapping(complex = ComplexEnum.Nested, order = 2)
		private TestNested2 testNested3;
	}
}