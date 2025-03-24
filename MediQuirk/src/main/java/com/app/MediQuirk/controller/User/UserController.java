package com.app.MediQuirk.controller.User;

import com.app.MediQuirk.model.UserProfile;
import com.app.MediQuirk.model.Users;
import com.app.MediQuirk.services.UserProfileService;
import com.app.MediQuirk.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Autowired
    private final UserProfileService userProfileService;

    private static final String UPLOADED_FOLDER = "/static/uploads/user";

    @GetMapping("/login")
    public String login() {
        return "User/users/login";
    }

    @GetMapping("/register")
    public String register(@NotNull Model model) {
        model.addAttribute("user", new Users());
        return "User/users/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") Users user,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toArray(String[]::new);
            model.addAttribute("errors", errors);
            return "User/users/register";
        }

        userService.save(user);
        userService.setDefaultRole(user.getUsername(), false);

        return "redirect:/login";
    }
    @GetMapping("/oauth2/success")
    public String oauth2LoginSuccess(@AuthenticationPrincipal OAuth2User principal, Model model) {
        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");

        Users user = userService.processOAuth2User(email, name);

        model.addAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String showUserProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Users> optionalUser = userService.findByUsername(username);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            UserProfile userProfile = user.getUserProfile();

            if (userProfile == null) {
                userProfile = new UserProfile();
                userProfile.setUser(user);
            }

            model.addAttribute("userProfile", userProfile);
        } else {
            model.addAttribute("errorMessage", "User not found.");
        }

        return "User/users/profile";
    }

    @PostMapping("/profile/update")
    public String updateUserProfile(@ModelAttribute UserProfile userProfile,
                                    @RequestParam(value = "file", required = false) MultipartFile file,
                                    RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<Users> optionalUser = userService.findByUsername(username);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                UserProfile existingProfile = user.getUserProfile();

                if (existingProfile == null) {
                    existingProfile = new UserProfile();
                    existingProfile.setUser(user);
                }

                // Update profile fields
                existingProfile.setFirstName(userProfile.getFirstName());
                existingProfile.setLastName(userProfile.getLastName());
                existingProfile.setGender(userProfile.getGender());
                existingProfile.setDateOfBirth(userProfile.getDateOfBirth());
                existingProfile.setAddress(userProfile.getAddress());

                if (file != null && !file.isEmpty()) {
                    String newAvatarUrl = userProfileService.updateUserProfileWithAvatar(existingProfile, file);
                    existingProfile.setProfilePictureUrl(newAvatarUrl);
                }

                userProfileService.updateUserProfile(existingProfile);

                redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while saving the file: " + e.getMessage());
        }

        return "redirect:/profile";
    }


    @PostMapping("/profile/change-avatar")
    public String changeAvatar(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            Optional<Users> optionalUser = userService.findByUsername(username);

            if (optionalUser.isPresent()) {
                Users user = optionalUser.get();
                UserProfile userProfile = user.getUserProfile();

                if (userProfile == null) {
                    userProfile = new UserProfile();
                    userProfile.setUser(user);
                }

                String newAvatarUrl = userProfileService.updateUserProfileWithAvatar(userProfile, file);
                userProfile.setProfilePictureUrl(newAvatarUrl);
                userProfileService.updateUserProfile(userProfile);

                redirectAttributes.addFlashAttribute("successMessage", "Avatar updated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while saving the file.");
        }

        return "redirect:/profile";
    }



    private String saveFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path uploadDir = Paths.get(UPLOADED_FOLDER);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        Path filePath = uploadDir.resolve(fileName);
        Files.write(filePath, file.getBytes());
        return fileName;
    }
}