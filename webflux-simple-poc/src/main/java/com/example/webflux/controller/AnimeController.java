package com.example.webflux.controller;

import com.example.webflux.domain.Anime;
import com.example.webflux.repository.AnimeRepository;
import com.example.webflux.service.AnimeService;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/animes")
@RequiredArgsConstructor
@Slf4j
public class AnimeController {

  private final AnimeService animeService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Flux<Anime> listAll() {
    return animeService.findAll();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<Anime> findById(@PathVariable Integer id) {
    return animeService.findById(id);
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Anime> save(@Valid @RequestBody Anime anime) {
    return animeService.save(anime);
  }


  @PostMapping("/batch")
  @ResponseStatus(HttpStatus.CREATED)
  public Flux<Anime> saveBatch(@RequestBody List<Anime> animes){
    return animeService.saveAll(animes);
  }


  @PutMapping("/{id}")
  public Mono<Anime> update(@PathVariable Integer id, @Valid @RequestBody Anime anime) {
    return animeService.update(anime.withId(id));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> delete(@PathVariable Integer id) {
    return animeService.delete(id);
  }

}
