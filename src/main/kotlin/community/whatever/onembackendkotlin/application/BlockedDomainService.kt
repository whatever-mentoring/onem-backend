package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.domain.BlockedDomain

interface BlockedDomainService {

    fun save(url: String): BlockedDomain

    fun isBlocked(url: String): Boolean

    fun delete(url: String)

    fun getAll(): List<BlockedDomain>
}
