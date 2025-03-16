package community.whatever.onembackendjava.repository;

import community.whatever.onembackendjava.entity.BlockedDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BlockedDomainRepository {

    private final community.whatever.onembackendjava.dao.BlockedDomainRepository blockedDomainDao;
    
    public boolean save(BlockedDomain blockedDomain) {
        return blockedDomainDao.save(blockedDomain);
    }
    
    public boolean delete(String domain) {
        return blockedDomainDao.delete(domain);
    }
    
    public Set<String> findAll() {
        return blockedDomainDao.findAll().stream()
                .map(BlockedDomain::getDomain)
                .collect(Collectors.toSet());
    }
    
    public boolean exists(String domain) {
        return blockedDomainDao.exists(domain);
    }
}
