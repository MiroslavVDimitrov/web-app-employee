package com.miro.employee.controller;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miro.employee.exception.TokenRefreshException;
import com.miro.employee.model.ERole;
import com.miro.employee.model.RefreshToken;
import com.miro.employee.model.Role;
import com.miro.employee.model.User;
import com.miro.employee.payload.request.LoginRequest;
import com.miro.employee.payload.request.SignupRequest;
import com.miro.employee.payload.response.MessageResponse;
import com.miro.employee.payload.response.UserInfoResponse;
import com.miro.employee.repository.RoleRepository;
import com.miro.employee.repository.UserRepository;
import com.miro.employee.security.jwt.JwtUtils;
import com.miro.employee.security.service.UserDetailsImpl;
import com.miro.employee.security.service.RefreshTokenService;



//  Disable CORS
@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  JwtUtils jwtUtils;

  @Autowired
  RefreshTokenService refreshTokenService;

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
      @RequestParam Map<String, String> allParams, @RequestHeader Map<String, String> header) {

    Authentication authentication = authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

    List<String> roles = userDetails.getAuthorities().stream()
        .map(item -> item.getAuthority())
        .collect(Collectors.toList());

    // if user login credentials are OK, token in data base will be delete
    if (authentication.isAuthenticated()) {
      refreshTokenService.deleteByUserId(userDetails.getId());
    }

    RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

    ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

 

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(new UserInfoResponse(userDetails.getId(),
            userDetails.getUsername(),
            userDetails.getEmail(),
            roles));

  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
    }

    // Create new user's account
    User user = new User(signUpRequest.getUsername(),
        signUpRequest.getEmail(),
        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(ERole.ROLE_USER)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
          case "admin":
            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(adminRole);

            break;
          case "mod":
            Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(modRole);

            break;
          default:
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }

  @PostMapping("/signout")
  public ResponseEntity<?> logoutUser() {
    Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (principle.toString() != "anonymousUser") {
      Long userId = ((UserDetailsImpl) principle).getId();
      refreshTokenService.deleteByUserId(userId);
    }

    ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
    ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
        .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
        .body(new MessageResponse("You've been signed out!"));
  }


  @PostMapping("/refreshtoken")
  public ResponseEntity<?> refreshtoken(HttpServletRequest request) {
   
    String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
    

    if ((refreshToken != null) && (refreshToken.length() > 0)) {
      return refreshTokenService.findByToken(refreshToken)
          .map(refreshTokenService::verifyExpiration)
          .map(RefreshToken::getUser)
          .map(user -> {
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);

            System.out.println(ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Token is refreshed successfully!")));

            return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponse("Token is refreshed successfully!"));
          })
          .orElseThrow(() -> new TokenRefreshException(refreshToken,
              "Refresh token is not in database!"));
    }

    return ResponseEntity.badRequest().body(new MessageResponse("Refresh Token is empty!"));
  }

}