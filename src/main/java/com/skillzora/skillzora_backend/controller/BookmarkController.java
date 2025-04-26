
package com.skillzora.skillzora_backend.controller;

import com.skillzora.skillzora_backend.dto.BookmarkDto;
import com.skillzora.skillzora_backend.dto.BookmarkRequest;
import com.skillzora.skillzora_backend.dto.UpdateBookmarkRequest;
import com.skillzora.skillzora_backend.dto.UserDto;
import com.skillzora.skillzora_backend.service.BookmarkService;
import com.skillzora.skillzora_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookmarkController {

    @Autowired
    private BookmarkService bookmarkService;

    @Autowired
    private UserService userService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkDto> createBookmark(@Valid @RequestBody BookmarkRequest bookmarkRequest) {
        String userId = getCurrentUserId();
        BookmarkDto bookmarkDto = bookmarkService.bookmarkPost(userId, bookmarkRequest);
        return new ResponseEntity<>(bookmarkDto, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BookmarkDto>> getAllBookmarks() {
        String userId = getCurrentUserId();
        List<BookmarkDto> bookmarks = bookmarkService.getAllBookmarks(userId);
        return ResponseEntity.ok(bookmarks);
    }

    @GetMapping("/{bookmarkId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkDto> getBookmarkById(@PathVariable String bookmarkId) {
        String userId = getCurrentUserId();
        BookmarkDto bookmark = bookmarkService.getBookmarkById(userId, bookmarkId);
        return ResponseEntity.ok(bookmark);
    }

    @GetMapping("/post/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkDto> getBookmarkByPostId(@PathVariable String postId) {
        String userId = getCurrentUserId();
        BookmarkDto bookmark = bookmarkService.getBookmarkByPostId(userId, postId);
        return ResponseEntity.ok(bookmark);
    }

    @PutMapping("/{bookmarkId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookmarkDto> updateBookmark(
            @PathVariable String bookmarkId,
            @Valid @RequestBody UpdateBookmarkRequest updateRequest) {
        String userId = getCurrentUserId();
        BookmarkDto updatedBookmark = bookmarkService.updateBookmarkNote(userId, bookmarkId, updateRequest);
        return ResponseEntity.ok(updatedBookmark);
    }

    @DeleteMapping("/{bookmarkId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteBookmark(@PathVariable String bookmarkId) {
        String userId = getCurrentUserId();
        bookmarkService.removeBookmark(userId, bookmarkId);
        return ResponseEntity.noContent().build();
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        UserDto userDto = userService.getUserProfileByUsername(userDetails.getUsername());
        return userDto.getId();
    }
}