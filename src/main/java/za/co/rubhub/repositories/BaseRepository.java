package za.co.rubhub.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    
    Optional<T> findByIdAndIsActiveTrue(ID id);
    
    List<T> findByIsActiveTrue();
    
    Page<T> findByIsActiveTrue(Pageable pageable);
    
    List<T> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<T> findByUpdatedAtAfter(LocalDateTime since);
    
    long countByIsActiveTrue();
    
    boolean existsByIdAndIsActiveTrue(ID id);
}