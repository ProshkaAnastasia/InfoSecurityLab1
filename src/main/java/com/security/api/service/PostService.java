package com.security.api.service;

import com.security.api.dto.PostRequest;
import com.security.api.entity.Post;
import com.security.api.repository.PostRepository;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {
    
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Post create(PostRequest request, String author) {
        Post post = new Post();
        post.setTitle(Encode.forHtml(request.getTitle()));
        post.setContent(Encode.forHtml(request.getContent()));
        post.setAuthor(author);
        return postRepository.save(post);
    }

    public List<Post> list() {
        return postRepository.findAll();
    }
}
