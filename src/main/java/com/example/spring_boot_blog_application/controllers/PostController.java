package com.example.spring_boot_blog_application.controllers;

import com.example.spring_boot_blog_application.models.Account;
import com.example.spring_boot_blog_application.models.Post;
import com.example.spring_boot_blog_application.services.AccountService;
import com.example.spring_boot_blog_application.services.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;
    private final AccountService accountService;

    // View a single post
    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id, Model model) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            model.addAttribute("post", optionalPost.get());
            return "post"; // View page
        } else {
            return "404"; // Not found page
        }
    }

    // Create post form
    @GetMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post_new"; // New post form page
    }

    // Handle new post creation
    @PostMapping("/posts/new")
    @PreAuthorize("isAuthenticated()")
    public String createNewPost(@ModelAttribute("post") Post post, Principal principal) {
        String authUsername = principal != null ? principal.getName() : "anonymousUser";
        Account account = accountService.findByEmail(authUsername)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        post.setAccount(account);
        postService.save(post);
        return "redirect:/";
    }

    // Edit post form
    @GetMapping("/posts/{id}/edit")
    @PreAuthorize("isAuthenticated()")
    public String editPostForm(@PathVariable Long id, Model model) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            model.addAttribute("post", optionalPost.get());
            return "post_edit"; // Edit post form page
        } else {
            return "404"; // Not found
        }
    }

    // Handle post update
    @PostMapping("/posts/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") Post updatedPost) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            Post existingPost = optionalPost.get();
            existingPost.setTitle(updatedPost.getTitle());
            existingPost.setBody(updatedPost.getBody());

            postService.save(existingPost);
            return "redirect:/posts/" + id;
        } else {
            return "404";
        }
    }

    // Delete a post
    @GetMapping("/posts/{id}/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String deletePost(@PathVariable Long id) {
        Optional<Post> optionalPost = postService.getById(id);
        if (optionalPost.isPresent()) {
            postService.delete(optionalPost.get());
            return "redirect:/";
        } else {
            return "404";
        }
    }
}
