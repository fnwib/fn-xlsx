package com.github.fnwib.mapper;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.plugin.ValueHandler;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Collections;

@Slf4j
public class MappersTest {

	/**
	 * 校验嵌套层数
	 */
	@Test(expected = SettingException.class)
	public void createNestedMapper() {
		LocalConfig localConfig = new LocalConfig();
		localConfig.setMaxNestLevel(1);
		Mappers.createNestedMapper(TestModel.class, localConfig, Collections.emptyList());
	}

	/**
	 * 不支持没有getter、setter的类
	 */
	@Test(expected = SettingException.class)
	public void canSupport1() {
		Mappers.canSupport(NoGetterSetter.class);
	}

	/**
	 * 不支持基本类型
	 */
	@Test(expected = SettingException.class)
	public void canSupport2() {
		Mappers.canSupport(int.class);
	}

	/**
	 * 不支持接口
	 */
	@Test(expected = SettingException.class)
	public void canSupport3() {
		Mappers.canSupport(ValueHandler.class);
	}

	@Getter
	@Setter
	public class TestModel {
		@AutoMapping(complex = ComplexEnum.NESTED)
		private TestNested2 testNested2;
		@AutoMapping(complex = ComplexEnum.NESTED)
		private TestNested2 testNested3;
	}

	public class NoGetterSetter {
		@AutoMapping(complex = ComplexEnum.NESTED)
		private TestNested2 testNested2;
		@AutoMapping(complex = ComplexEnum.NESTED)
		private TestNested2 testNested3;
	}


	@ToString
	@Getter
	@Setter
	@EqualsAndHashCode
	@AllArgsConstructor
	@NoArgsConstructor
	public class TestNested2 {
		@AutoMapping("Nested C")
		private String bb;
		@AutoMapping("Nested D")
		private String aa;
	}
}