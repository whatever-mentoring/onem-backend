package community.whatever.onembackendkotlin.presentation.dto

data class ShortenUrlCreateRequest(val originUrl: String)

data class ShortenUrlSearchRequest(val shortenUrl: String)

data class BlockedDomainCreateRequest(val url: String)

data class BlockedDomainCheckRequest(val url: String)

data class BlockedDomainDeleteRequest(val url: String)
