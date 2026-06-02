package com.educhallenge.users.service;

import com.educhallenge.users.dto.CreateUserRequest;
import com.educhallenge.users.dto.UserResponse;
import com.educhallenge.users.entity.User;
import com.educhallenge.backend.exception.EmailAlreadyUsedException;
import com.educhallenge.backend.exception.UsernameAlreadyUsedException;
import com.educhallenge.backend.exception.WeakPasswordException;
import com.educhallenge.users.repository.UserRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public UserResponse createUser(CreateUserRequest request) {
		return registerUser(request.getUsername(), request.getEmail(), request.getPassword());
	}

	public UserResponse registerUser(String username, String email, String rawPassword) {
		String normalizedUsername = username.trim();
		String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

		if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
			throw new EmailAlreadyUsedException("Email already used");
		}

		if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
			throw new UsernameAlreadyUsedException("Username already used");
		}

		validatePasswordStrength(rawPassword);

		User user = new User();
		user.setUsername(normalizedUsername);
		user.setEmail(normalizedEmail);
		user.setPassword(passwordEncoder.encode(rawPassword));
		user.setRole("USER");
		user.setPointsTotal(0);
		user.setLevel(1);

		User savedUser = userRepository.save(user);
		return mapToResponse(savedUser);
	}

	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {
		return userRepository.findAll()
				.stream()
				.map(this::mapToResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public UserResponse getUserById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
		return mapToResponse(user);
	}

	private UserResponse mapToResponse(User user) {
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getRole(),
				user.getPointsTotal(),
				user.getLevel()
		);
	}

	private void validatePasswordStrength(String password) {
		boolean hasUppercase = password.chars().anyMatch(Character::isUpperCase);
		boolean hasLowercase = password.chars().anyMatch(Character::isLowerCase);
		boolean hasDigit = password.chars().anyMatch(Character::isDigit);

		if (!hasUppercase || !hasLowercase || !hasDigit) {
			throw new WeakPasswordException(
					"Password must contain at least one uppercase letter, one lowercase letter, and one digit"
			);
		}
	}
}
