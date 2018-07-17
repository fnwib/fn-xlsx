package com.github.fnwib.testentity;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import lombok.*;

@ToString
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class TestNested {
	@AutoMapping("Nested B")
	private String bb;
	@AutoMapping("Nested A")
	private String aa;
	@AutoMapping(complex = ComplexEnum.Nested, order = 1)
	private TestNested2 testNested2;
}