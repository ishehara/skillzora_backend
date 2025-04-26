
package com.skillzora.skillzora_backend.service;

import com.skillzora.skillzora_backend.dto.BookmarkDto;
import com.skillzora.skillzora_backend.dto.BookmarkRequest;
import com.skillzora.skillzora_backend.dto.UpdateBookmarkRequest;
import com.skillzora.skillzora_backend.exception.ResourceAlreadyExistsException;
import com.skillzora.skillzora_backend.exception.ResourceNotFoundException;
import com.skillzora.skillzora_backend.model.Bookmark;
import com.skillzora.skillzora_backend.repository.BookmarkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookmarkService {

    @Autowired
    private BookmarkRepository bookmarkRepository;

    public BookmarkDto bookmarkPost(String userId, BookmarkRequest bookmarkRequest) {
        // Check if bookmark already exists
        if (bookmarkRepository.existsByUserIdAndPostId(userId, bookmarkRequest.getPostId())) {
            throw new ResourceAlreadyExistsException("Post is already bookmarked");
        }

        Bookmark bookmark = new Bookmark(userId, bookmarkRequest.getPostId(), bookmarkRequest.getNote());
        bookmark = bookmarkRepository.save(bookmark);
        return convertToBookmarkDto(bookmark);
    }

    public List<BookmarkDto> getAllBookmarks(String userId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserId(userId);
        return bookmarks.stream()
                .map(this::convertToBookmarkDto)
                .collect(Collectors.toList());
    }

    public BookmarkDto getBookmarkById(String userId, String bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found with id: " + bookmarkId));

        // Ensure the bookmark belongs to the user
        if (!bookmark.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Bookmark not found with id: " + bookmarkId);
        }

        return convertToBookmarkDto(bookmark);
    }

    public BookmarkDto getBookmarkByPostId(String userId, String postId) {
        Bookmark bookmark = bookmarkRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found for post id: " + postId));
        return convertToBookmarkDto(bookmark);
    }

    public BookmarkDto updateBookmarkNote(String userId, String bookmarkId, UpdateBookmarkRequest updateRequest) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found with id: " + bookmarkId));

        // Ensure the bookmark belongs to the user
        if (!bookmark.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Bookmark not found with id: " + bookmarkId);
        }

        bookmark.setNote(updateRequest.getNote());
        bookmark.setUpdatedAt(new Date());
        bookmark = bookmarkRepository.save(bookmark);
        return convertToBookmarkDto(bookmark);
    }

    public void removeBookmark(String userId, String bookmarkId) {
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new ResourceNotFoundException("Bookmark not found with id: " + bookmarkId));

        // Ensure the bookmark belongs to the user
        if (!bookmark.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Bookmark not found with id: " + bookmarkId);
        }

        bookmarkRepository.delete(bookmark);
    }

    private BookmarkDto convertToBookmarkDto(Bookmark bookmark) {
        BookmarkDto bookmarkDto = new BookmarkDto();
        bookmarkDto.setId(bookmark.getId());
        bookmarkDto.setPostId(bookmark.getPostId());
        bookmarkDto.setNote(bookmark.getNote());
        bookmarkDto.setCreatedAt(bookmark.getCreatedAt());
        bookmarkDto.setUpdatedAt(bookmark.getUpdatedAt());
        return bookmarkDto;
    }
}