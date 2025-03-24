package com.app.MediQuirk.controller.Admin;

import com.app.MediQuirk.model.UserProfile;
import com.app.MediQuirk.model.Users;
import com.app.MediQuirk.services.RoleService;
import com.app.MediQuirk.services.UserProfileService;
import com.app.MediQuirk.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class UserProfileController {

    @Autowired
    private final UserProfileService userProfileService;

    @Autowired
    private final UserService usersService;

    @Autowired
    private RoleService roleService;

    private static final String UPLOADED_FOLDER = "src/main/resources/static/uploads/user";

    @GetMapping("/userprofiles/add")
    public String showAddForm(Model model) {
        model.addAttribute("userProfile", new UserProfile());
        return "Admin/userprofiles/add-userprofile";
    }

    @PostMapping("/userprofiles/add")
    public String addUserProfile(@Valid @ModelAttribute UserProfile userProfile,
                                 BindingResult result,
                                 @RequestParam("file") MultipartFile file,
                                 Model model) {
        if (result.hasErrors()) {
            return "Admin/userprofiles/add-userprofile";
        }

        try {
            if (!file.isEmpty()) {
                String fileName = saveFile(file);
                userProfile.setProfilePictureUrl(fileName);
            }

            userProfileService.addUserProfile(userProfile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while saving the file.");
            return "Admin/userprofiles/add-userprofile";
        }

        return "redirect:/admin/userprofiles";
    }

    @GetMapping("/userprofiles")
    public String listUserProfiles(Model model,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<UserProfile> userProfilePage = userProfileService.getAllUserProfiles(pageable);

        model.addAttribute("userProfiles", userProfilePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userProfilePage.getTotalPages());
        model.addAttribute("totalItems", userProfilePage.getTotalElements());

        return "Admin/userprofiles/userprofile-list";
    }

    @GetMapping("/userprofiles/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        UserProfile userProfile = userProfileService.getUserProfileById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user profile Id: " + id));
        model.addAttribute("userProfile", userProfile);
        return "Admin/userprofiles/update-userprofile";
    }

    @PostMapping("/userprofiles/update/{id}")
    public String updateUserProfile(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute UserProfile userProfile,
                                    BindingResult result,
                                    @RequestParam("file") MultipartFile file,
                                    Model model) {
        if (result.hasErrors()) {
            userProfile.setUserProfileId(id);
            return "Admin/userprofiles/update-userprofile";
        }

        try {
            UserProfile existingUserProfile = userProfileService.getUserProfileById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user profile Id:" + id));

            if (!file.isEmpty()) {
                String fileName = saveFile(file);
                existingUserProfile.setProfilePictureUrl(fileName);
            }

            // Update other fields
            existingUserProfile.setFirstName(userProfile.getFirstName());
            existingUserProfile.setLastName(userProfile.getLastName());
            // Add other fields as needed

            userProfileService.updateUserProfile(existingUserProfile);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "An error occurred while saving the file.");
            return "Admin/userprofiles/update-userprofile";
        }

        return "redirect:/admin/userprofiles";
    }

    @GetMapping("/userprofiles/delete/{id}")
    public String deleteUserProfile(@PathVariable("id") Long id, Model model) {
        UserProfile userProfile = userProfileService.getUserProfileById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user profile Id: " + id));
        userProfileService.deleteUserProfileById(id);
        return "redirect:/admin/userprofiles";
    }

    @GetMapping("/userprofiles/details/{id}")
    public String showUserProfileDetails(@PathVariable("id") Long id, Model model) {
        UserProfile userProfile = userProfileService.getUserProfileById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user profile Id: " + id));
        model.addAttribute("userProfile", userProfile);
        model.addAttribute("user", userProfile.getUser());
        return "Admin/userprofiles/userprofile-details";
    }

    @GetMapping("/userprofiles/{userProfileId}/add-user")
    public String showAddUserForm(@PathVariable("userProfileId") Long userProfileId, Model model) {
        model.addAttribute("user", new Users());
        model.addAttribute("userProfileId", userProfileId);
        model.addAttribute("roles", roleService.getAllRoles());
        return "Admin/userprofiles/add-user";
    }

    @PostMapping("/userprofiles/{userProfileId}/add-user")
    public String addUser(@PathVariable("userProfileId") Long userProfileId, @Valid Users user, BindingResult result, Model model, @RequestParam List<Long> roleIds) {
        if (result.hasErrors()) {
            return "Admin/userprofiles/add-user";
        }

        UserProfile userProfile = userProfileService.getUserProfileById(userProfileId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user profile Id: " + userProfileId));
        userProfile.setUser(user);
        user.setRoles(roleService.getRolesByIds(roleIds));
        usersService.addUser(user);

        userProfileService.updateUserProfile(userProfile);
        return "redirect:/admin/userprofiles";
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