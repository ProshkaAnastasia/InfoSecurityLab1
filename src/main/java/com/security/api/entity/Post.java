package com.security.api.entity;
import jakarta.persistence.*;
@Entity
public class Post { @Id @GeneratedValue private Long id; private String title; @Column(length=4000) private String content; private String author;
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
  public String getContent(){return content;} public void setContent(String c){this.content=c;}
  public String getAuthor(){return author;} public void setAuthor(String a){this.author=a;} }