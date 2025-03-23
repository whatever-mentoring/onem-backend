package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.application.dto.BlockedDomainCheckRequest
import community.whatever.onembackendkotlin.application.dto.BlockedDomainCreateRequest
import community.whatever.onembackendkotlin.application.dto.BlockedDomainDeleteRequest
import community.whatever.onembackendkotlin.application.exception.DomainAlreadyBlockedException
import community.whatever.onembackendkotlin.domain.BlockedDomain
import community.whatever.onembackendkotlin.domain.BlockedDomainRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.net.URL
import java.util.UUID

@Transactional(readOnly = true)
@Service
class DefaultBlockedDomainService(private val blockedDomainRepository: BlockedDomainRepository) : BlockedDomainService {

    @Transactional
    override fun save(request: BlockedDomainCreateRequest): BlockedDomain {
        val domain = URL(request.url).host
        if (blockedDomainRepository.existsByDomain(domain)) {
            throw DomainAlreadyBlockedException()
        }
        return blockedDomainRepository.save(BlockedDomain(domain = domain, id = UUID.randomUUID()))
    }

    override fun isBlocked(request: BlockedDomainCheckRequest): Boolean {
        return URL(request.url).host.let { blockedDomainRepository.existsByDomain(it) }
    }

    @Transactional
    override fun delete(request: BlockedDomainDeleteRequest) {
        val domain = URL(request.url).host
        blockedDomainRepository.deleteByDomain(domain)
    }

    override fun getAll(): List<BlockedDomain> {
        return blockedDomainRepository.findAll()
    }
}
