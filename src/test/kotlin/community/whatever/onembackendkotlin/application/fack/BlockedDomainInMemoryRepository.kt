package community.whatever.onembackendkotlin.application.fack

import community.whatever.onembackendkotlin.domain.BlockedDomain
import community.whatever.onembackendkotlin.domain.BlockedDomainRepository

class BlockedDomainInMemoryRepository : BlockedDomainRepository {
    private val blockedDomains = mutableMapOf<String, BlockedDomain>()

    override fun save(blockedDomain: BlockedDomain): BlockedDomain {
        return blockedDomain.also { blockedDomains[it.domain] = it }
    }

    override fun existsByDomain(domain: String): Boolean {
        return blockedDomains.containsKey(domain)
    }

    override fun deleteByDomain(domain: String) {
        blockedDomains.remove(domain)
    }

    override fun findAll(): List<BlockedDomain> {
        return blockedDomains.values.toList()
    }
}
