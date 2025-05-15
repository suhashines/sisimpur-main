package com.sisimpur.library.dto;
import jakarta.validation.constraints.* ;

public class BookRequest {


    @NotBlank(message = "title cannot be empty")
    @Size(max=255,message="name cannot exceed 255 characters")
    private String title ;

    @NotNull
    @Min(0)
    private Integer authorId ;
    private String genre ;

    @Min(0)
    private Integer publishedYear ;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }
    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }
    public Integer getAuthorId() {
        return authorId;
    }
    public String getGenre() {
        return genre;
    }
    public Integer getPublishedYear() {
        return publishedYear;
    }
    
    
}
