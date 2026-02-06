package za.co.rubhub.service;

import za.co.rubhub.model.LoyaltyProgram;
import za.co.rubhub.repositories.LoyaltyProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoyaltyProgramService {
    private final LoyaltyProgramRepository loyaltyProgramRepository;

    public LoyaltyProgram createProgram(LoyaltyProgram program) {
        program.setCreatedAt(LocalDateTime.now());
        return loyaltyProgramRepository.save(program);
    }

    public List<LoyaltyProgram> getAllActivePrograms() {
        return loyaltyProgramRepository.findByIsActiveTrue();
    }
}