package com.example.webflux.util;

import com.example.webflux.domain.Anime;

public class AnimeCreator {


  public static Anime createAnimeToBeSaved() {
    return Anime.builder()
        .name("lala")
        .build();
  }

  public static Anime createValidAnime() {
    return Anime.builder()
        .id(1)
        .name("Naruto")
        .build();
  }

  public static Anime createValidUpdateAnime() {
    return Anime.builder()
        .id(2)
        .name("Anime")
        .build();
  }
}
