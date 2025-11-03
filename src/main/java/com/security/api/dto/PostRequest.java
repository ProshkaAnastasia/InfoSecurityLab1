package com.security.api.dto;
import jakarta.validation.constraints.NotBlank;
public class PostRequest { @NotBlank private String title; @NotBlank private String content;
  public String getTitle(){return title;} public void setTitle(String t){this.title=t;}
  public String getContent(){return content;} public void setContent(String c){this.content=c;} }