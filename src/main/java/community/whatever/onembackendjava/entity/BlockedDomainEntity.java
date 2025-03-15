package community.whatever.onembackendjava.entity;

import static lombok.AccessLevel.*;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlockedDomainEntity {

	private Long id;

	private String domain;

	@Builder(access = PRIVATE)
	private BlockedDomainEntity(Long id, String domain) {
		this.id = id;
		this.domain = domain;
	}

	public static BlockedDomainEntity from(String domain) {
		return BlockedDomainEntity.builder()
			.domain(domain)
			.build();
	}
}
