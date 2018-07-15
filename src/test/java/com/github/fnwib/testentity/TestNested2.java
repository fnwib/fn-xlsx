package com.github.fnwib.testentity;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import lombok.*;

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