package com.example.webflux.service;

import com.example.webflux.domain.Anime;
import com.example.webflux.repository.AnimeRepository;
import io.netty.util.internal.StringUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AnimeService {

  private final AnimeRepository animeRepository;

  public Flux<Anime> findAll() {
    return animeRepository.findAll();
  }

  public Mono<Anime> findById(Integer id) {
    return animeRepository.findById(id)
        .switchIfEmpty(monoResponseStatusNotFoundException());
  }

  public <T> Mono<T> monoResponseStatusNotFoundException() {
    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Anime not found"));
  }

  public Mono<Anime> save(Anime anime) {
    return animeRepository.save(anime);
  }

  public Mono<Anime> update(Anime anime) {
    return findById(anime.getId())
        .map(animeFound -> anime.withId(animeFound.getId()))
        .flatMap(animeRepository::save);
  }

  public Mono<Void> delete(Integer id) {
    return animeRepository.deleteById(id);
  }


  @Transactional
  public Flux<Anime> saveAll(List<Anime> animes) {
    return animeRepository.saveAll(animes)
        .doOnNext(this::throwResponseStatusExceptionWhenEmptyName);
  }

  private void throwResponseStatusExceptionWhenEmptyName(Anime anime) {
    if (StringUtil.isNullOrEmpty(anime.getName())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Name");
    }
  }
}
