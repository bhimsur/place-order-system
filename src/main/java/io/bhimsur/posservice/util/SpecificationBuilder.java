package io.bhimsur.posservice.util;

import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecificationBuilder<T> implements Specification<T> {
	private String key;
	private String operation;
	private Object value;

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (operation.equalsIgnoreCase("=")) {
			Expression<String> expression;
			if (this.key.contains(".")) {
				String[] keys = this.key.split("\\.");
				Join<?,?> join = root.join(keys[0], JoinType.LEFT);
				query.distinct(true);
				expression = join.get(keys[1]);
			} else {
				expression = root.get(this.key);
			}
			return builder.equal(expression, this.value);
		} else if (operation.equalsIgnoreCase("in")) {
			return builder.in(root.get(this.key)).value(this.value);
		} else if (operation.equalsIgnoreCase("%%")) {
			String val = "%" + this.value.toString().toLowerCase() + "%";
			Expression<String> expression;
			if (this.key.contains(".")) {
				String[] keys = this.key.split("\\.");
				Join<?,?> join = root.join(keys[0], JoinType.LEFT);
				query.distinct(true);
				expression = builder.lower(join.get(keys[1]));
			} else {
				expression = builder.lower(root.get(this.key));
			}
			return builder.like(expression, val);
		} else if (operation.equalsIgnoreCase("isNull")) {
			Expression<String> expression;
			if (this.key.contains(".")) {
				String[] keys = this.key.split("\\.");
				Join<?,?> join = root.join(keys[0], JoinType.LEFT);
				expression = join.get(keys[1]);
			} else {
				expression = root.get(this.key);
			}
			return builder.isNull(expression);
		}
		return null;
	}
}