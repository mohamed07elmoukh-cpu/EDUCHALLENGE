package com.educhallenge.auth.service;

import com.educhallenge.auth.dto.LoginRequest;
import com.educhallenge.auth.dto.LoginResponse;
import com.educhallenge.auth.dto.RegisterRequest;
import com.educhallenge.auth.dto.RegisterResponse;
import com.educhallenge.users.dto.UserResponse;
import com.educhallenge.users.entity.User;
import com.educhallenge.users.repository.UserRepository;
import com.educhallenge.users.service.UserService;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

	private final UserService userService;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public AuthService(
			UserService userService,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder
	) {
		this.userService = userService;
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public RegisterResponse register(RegisterRequest request) {
		UserResponse user = userService.registerUser(
				request.getUsername(),
				request.getEmail(),
				request.getPassword()
		);
		return new RegisterResponse("Inscription reussie", user);
	}

	@Transactional(readOnly = true)
	public LoginResponse login(LoginRequest request) {
		String normalizedEmail = request.getEmail().trim().toLowerCase(Locale.ROOT);
		User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}

		UserResponse responseUser = new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getRole(),
				user.getPointsTotal(),
				user.getLevel()
		);

		return new LoginResponse("Connexion reussie", responseUser);
	}
}
