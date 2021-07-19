package com.example.webflux.controller;

import com.example.webflux.domain.Anime;
import com.example.webflux.repository.AnimeRepository;
import com.example.webflux.service.AnimeService;
import com.example.webflux.util.AnimeCreator;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

  @InjectMocks
  private AnimeController animeController;

  @Mock
  private AnimeService animeService;

  private final Anime anime = AnimeCreator.createValidAnime();

  @BeforeAll
  public static void blockHoundSetup() {
    BlockHound.install();
  }

  @Test
  public void blockHoundWorks() {
    try {
      FutureTask<?> task = new FutureTask<>(() -> {
        Thread.sleep(0);
        return "";
      });
      Schedulers.parallel().schedule(task);

      task.get(10, TimeUnit.SECONDS);
      Assertions.fail("Should fail");
    } catch (Exception e) {
      Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
    }
  }

  @BeforeEach
  public void setUp() {
    BDDMockito.when(animeService.findAll())
        .thenReturn(Flux.just(anime));

    BDDMockito.when(animeService.findById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeService.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeService.delete(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.empty());

    BDDMockito.when(animeService.update(AnimeCreator.createValidAnime()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeService.saveAll(List.of(AnimeCreator.createAnimeToBeSaved())))
        .thenReturn(Flux.just(anime, anime));
  }

  @Test
  @DisplayName("findAll returns a flux of anime")
  public void findAll_ReturnFluxOFAnime_WhenSuccessful() {

    StepVerifier.create(animeController.listAll())
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns Mono with anime when exists")
  public void findBydId_ReturnMonoOfAnime_WhenSuccessful() {

    StepVerifier.create(animeController.findById(1))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("save creates anime when successful")
  public void save_CreatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeController.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes anime when successful")
  public void delete_RemovesAnime_WhenSuccessful() {
    StepVerifier.create(animeController.delete(1))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("update saves updated anime when successful")
  public void update_UpdatesAnime_WhenSuccessful() {
    StepVerifier.create(animeController.update(1, AnimeCreator.createValidAnime()))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("Save all creates a list of animes when successful")
  public void saveAll_createsListOfAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeController.saveBatch(List.of(animeToBeSaved)))
        .expectSubscription()
        .expectNext(anime, anime)
        .verifyComplete();
  }
}