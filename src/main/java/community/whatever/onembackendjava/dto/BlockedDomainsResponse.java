package community.whatever.onembackendjava.dto;

import java.util.Set;

public record BlockedDomainsResponse(Set<String> blockedDomains) {
}
