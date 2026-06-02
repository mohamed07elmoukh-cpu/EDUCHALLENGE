package com.educhallenge.backend.security;

import com.educhallenge.backend.exception.AccessDeniedException;
import com.educhallenge.users.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AdminAuthorizationService {

	public void assertAdmin(User currentUser) {
		if (currentUser == null || currentUser.getRole() == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole().trim())) {
			throw new AccessDeniedException("Admin role is required to access this resource");
		}
	}
}
