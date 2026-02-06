package za.co.rubhub.service;

import za.co.rubhub.model.User;
import za.co.rubhub.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public User save(User user) {
        // Generate referral code if not present
        if (user.getReferralCode() == null || user.getReferralCode().isEmpty()) {
        }
        return userRepository.save(user);
    }


    public List<User> findByUserType(String userType) {
        return userRepository.findByUserType(userType);
    }

    public List<User> findByStatus(String status) {
        return userRepository.findByStatus(status);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean authenticate(String email, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authenticate'");
    }

}