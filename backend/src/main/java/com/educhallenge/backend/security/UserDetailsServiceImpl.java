package com.educhallenge.backend.security;

import com.educhallenge.users.entity.User;
import com.educhallenge.users.repository.UserRepository;
import java.util.Locale;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		String normalizedEmail = username == null ? "" : username.trim().toLowerCase(Locale.ROOT);

		User user = userRepository.findByEmailIgnoreCase(normalizedEmail)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new AuthenticatedUserDetails(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getPassword(),
				user.getRole()
		);
	}
}
