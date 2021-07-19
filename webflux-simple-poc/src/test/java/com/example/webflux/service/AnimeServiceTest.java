package com.example.webflux.service;

import com.example.webflux.domain.Anime;
import com.example.webflux.repository.AnimeRepository;
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
import org.springframework.web.server.ResponseStatusException;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

  @InjectMocks
  private AnimeService animeService;

  @Mock
  private AnimeRepository animeRepository;

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
    BDDMockito.when(animeRepository.findAll())
        .thenReturn(Flux.just(anime));

    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepository.save(AnimeCreator.createAnimeToBeSaved()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepository.deleteById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.empty());

    BDDMockito.when(animeRepository.save(AnimeCreator.createValidAnime()))
        .thenReturn(Mono.just(anime));

    BDDMockito.when(animeRepository.saveAll(List.of(AnimeCreator.createAnimeToBeSaved())))
        .thenReturn(Flux.just(anime, anime));
  }

  @Test
  @DisplayName("findAll returns a flux of anime")
  public void findAll_ReturnFluxOFAnime_WhenSuccessful() {

    StepVerifier.create(animeService.findAll())
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns Mono with anime when exists")
  public void findBydId_ReturnMonoOfAnime_WhenSuccessful() {

    StepVerifier.create(animeService.findById(1))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("findById returns Mono error when anime does not when exists")
  public void findBydId_ReturnMonoError_WhenEmptyMonoIsReturned() {
    BDDMockito.when(animeRepository.findById(ArgumentMatchers.anyInt()))
        .thenReturn(Mono.empty());

    StepVerifier.create(animeService.findById(1))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }


  @Test
  @DisplayName("save creates anime when successful")
  public void save_CreatesAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeService.save(animeToBeSaved))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("delete removes anime when successful")
  public void delete_RemovesAnime_WhenSuccessful() {
    StepVerifier.create(animeService.delete(1))
        .expectSubscription()
        .verifyComplete();
  }

  @Test
  @DisplayName("update saves updated anime when successful")
  public void update_UpdatesAnime_WhenSuccessful() {
    StepVerifier.create(animeService.update(AnimeCreator.createValidAnime()))
        .expectSubscription()
        .expectNext(anime)
        .verifyComplete();
  }

  @Test
  @DisplayName("update mono error when anime when empty mono is returned")
  public void update_ReturnError_WhenMonoEmptyIsReturned() {
    BDDMockito.when(animeRepository.findById(1))
        .thenReturn(Mono.empty());

    StepVerifier.create(animeService.update(AnimeCreator.createValidAnime()))
        .expectSubscription()
        .expectError(ResponseStatusException.class)
        .verify();
  }

  @Test
  @DisplayName("Save all creates a list of animes when successful")
  public void saveAll_createsListOfAnime_WhenSuccessful() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved)))
        .expectSubscription()
        .expectNext(anime, anime)
        .verifyComplete();
  }


  @Test
  @DisplayName("Save all return error when one or more anime contains empty name")
  public void saveAll_returnError_WhenNameIsEmpty() {
    Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

    BDDMockito.when(animeRepository.saveAll(ArgumentMatchers.anyIterable()))
        .thenReturn(Flux.just(anime, anime.withName("")));

    StepVerifier.create(animeService.saveAll(List.of(animeToBeSaved, animeToBeSaved.withName(""))))
        .expectSubscription()
        .expectNext(anime)
        .expectError(ResponseStatusException.class)
        .verify();
  }
}